package cn.nispring.rail12306.scheduled;

import cn.nispring.rail12306.config.DataProperties;
import cn.nispring.rail12306.entity.AreaEntity;
import cn.nispring.rail12306.entity.CarEntity;
import cn.nispring.rail12306.entity.CarLayoutEntity;
import cn.nispring.rail12306.entity.PriceEntity;
import cn.nispring.rail12306.entity.StationEntity;
import cn.nispring.rail12306.entity.StopEntity;
import cn.nispring.rail12306.entity.TrainEntity;
import cn.nispring.rail12306.mapper.AreaMapper;
import cn.nispring.rail12306.mapper.CarLayoutMapper;
import cn.nispring.rail12306.mapper.CarMapper;
import cn.nispring.rail12306.mapper.PriceMapper;
import cn.nispring.rail12306.mapper.StationMapper;
import cn.nispring.rail12306.mapper.StopMapper;
import cn.nispring.rail12306.mapper.TrainMapper;
import cn.nispring.rail12306.model.SeatType;
import cn.nispring.rail12306.model.layout.Layout;
import cn.nispring.rail12306.service.AreaService;
import cn.nispring.rail12306.service.CarService;
import cn.nispring.rail12306.service.StationService;
import cn.nispring.rail12306.service.TrainService;
import org.jspecify.annotations.NonNull;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static cn.nispring.rail12306.util.Util.readCsv;

@Component
public class TrainImport implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TrainImport.class);

    private static final int DAY_TO_GEN = 15;
    private static final int BASIC_BATCH_SIZE = 1000;

    private final StopMapper stopMapper;
    private final DataProperties dataProperties;
    private final ObjectMapper objectMapper;
    private final CarLayoutMapper carLayoutMapper;
    private final StationService stationService;
    private final PriceMapper priceMapper;
    private final TransactionTemplate transactionTemplate;
    private final AreaMapper areaMapper;
    private final CarMapper carMapper;
    private final TrainMapper trainMapper;
    private final StationMapper stationMapper;
    private final JdbcTemplate jdbcTemplate;
    private final AreaService areaService;
    private final TrainService trainService;
    private final CarService carService;

    public TrainImport(StopMapper stopMapper, DataProperties dataProperties, ObjectMapper objectMapper,
                       CarLayoutMapper carLayoutMapper, StationService stationService, PriceMapper priceMapper,
                       PlatformTransactionManager transactionManager,
                       AreaMapper areaMapper, CarMapper carMapper, TrainMapper trainMapper,
                       StationMapper stationMapper, JdbcTemplate jdbcTemplate,
                       AreaService areaService, TrainService trainService, CarService carService) {
        this.stopMapper = stopMapper;
        this.dataProperties = dataProperties;
        this.objectMapper = objectMapper;
        this.carLayoutMapper = carLayoutMapper;
        this.stationService = stationService;
        this.priceMapper = priceMapper;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.areaMapper = areaMapper;
        this.carMapper = carMapper;
        this.trainMapper = trainMapper;
        this.stationMapper = stationMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.areaService = areaService;
        this.trainService = trainService;
        this.carService = carService;
    }

    /**
     * 应用启动后执行一次
     */
    @Override
    public void run(@NonNull ApplicationArguments args) throws Exception {
        importTrain();
    }

    /**
     * 每天 12:00（北京时间）执行
     */
    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Shanghai")
    public void importTrain() throws IOException {
        // 先导基础数据，后续 stops/car_layouts/prices 都依赖它
        importBasicData();

        stopMapper.deleteOld();
        log.info("delete old records for table stops");

        Path csvPath = dataProperties.getDir().resolve("stops.csv");
        List<StopEntity> stops = new ArrayList<>();
        List<String[]> rows = readCsv(csvPath);
        for (String[] row : rows) {
            StopEntity stop = new StopEntity(
                    null,
                    Long.parseLong(row[0]),
                    Integer.parseInt(row[1]),
                    Long.parseLong(row[2]),
                    row[3],
                    Integer.parseInt(row[4]),
                    LocalTime.of(
                            Integer.parseInt(row[5].substring(0, 2)),
                            Integer.parseInt(row[5].substring(2, 4))
                    ),
                    Integer.parseInt(row[6]),
                    LocalTime.of(
                            Integer.parseInt(row[7].substring(0, 2)),
                            Integer.parseInt(row[7].substring(2, 4))
                    )
            );
            stops.add(stop);
        }
        log.info("read {} records from stops csv", stops.size());

        LocalDate now = LocalDate.now();

        for (int i = 0; i < DAY_TO_GEN; i += 1) {
            LocalDate date = now.plusDays(i);
            if (stopMapper.existsByDate(date)) {
                continue;
            }

            for (StopEntity stop : stops) {
                stop.setTrainDate(date);
            }
            int n = 1000;
            for (int j = 0; j < stops.size(); j += n) {
                List<StopEntity> sub = stops.subList(j, Math.min(j + n, stops.size()));
                stopMapper.insertBatch(sub);
            }
            log.info("import records on {} for stops", date);
        }

        // ======================================================================

        carLayoutMapper.deleteOld();
        log.info("delete old records for table car_layouts");

        Path jsonPath = dataProperties.getDir().resolve("layout_g.json");
        Layout layout = objectMapper.readValue(jsonPath.toFile(), Layout.class);
        log.info("read layout_g.json");

        Map<String, Long> gaotieMap = new HashMap<>();
        for (StopEntity stop : stops) {
            if (stop.getTrainCode().startsWith("G")) {
                gaotieMap.put(stop.getTrainCode(), stop.getTrainId());
            }
        }
        Set<Long> gaotieSet = new HashSet<>(gaotieMap.values());
        List<Long> gaotieIds = new ArrayList<>(gaotieSet);
        Collections.sort(gaotieIds);
        List<CarLayoutEntity> carLayouts = new ArrayList<>();
        for (Long trainId : gaotieIds) {
            carLayouts.add(new CarLayoutEntity(
                    null,
                    trainId,
                    null,
                    layout
            ));
        }

        for (int i = 0; i < DAY_TO_GEN; i += 1) {
            LocalDate date = now.plusDays(i);
            if (carLayoutMapper.existsByDate(date)) {
                continue;
            }

            for (CarLayoutEntity entity : carLayouts) {
                entity.setTrainDate(date);
            }
            int n = 1000;
            for (int j = 0; j < carLayouts.size(); j += n) {
                List<CarLayoutEntity> sub = carLayouts.subList(j, Math.min(j + n, carLayouts.size()));
                carLayoutMapper.insertBatch(sub);
            }
            log.info("import records on {} for car_layouts", date);
        }

        // ======================================================================

        priceMapper.deleteOld();
        log.info("delete old records for table prices");

        Map<Long, List<StopEntity>> stopMap = new HashMap<>();
        for (StopEntity stop : stops) {
            if (stop.getTrainCode().startsWith("G")) {
                if (!stopMap.containsKey(stop.getTrainId())) {
                    stopMap.put(stop.getTrainId(), new ArrayList<>());
                }
                stopMap.get(stop.getTrainId()).add(stop);
            }
        }
        List<PriceEntity> prices = new ArrayList<>();
        for (List<StopEntity> trainStops : stopMap.values()) {
            for (int i = 0; i < trainStops.size() - 1; i += 1) {
                for (int j = i + 1; j < trainStops.size(); j += 1) {
                    StopEntity fromStop = trainStops.get(i);
                    StopEntity toStop = trainStops.get(j);
                    Long fromAreaId = stationService.get(fromStop.getStationId()).areaId();
                    Long toAreaId = stationService.get(toStop.getStationId()).areaId();

                    // 定价和里程挂钩，为了方便先用运行时间估计一下
                    int diffMinute = ((toStop.getArriveDay() * 86400 + toStop.getArriveTime().toSecondOfDay()) -
                                      (fromStop.getStartDay() * 86400 + fromStop.getStartTime().toSecondOfDay())) / 60;

                    prices.add(new PriceEntity(
                            null,
                            fromAreaId,
                            toAreaId,
                            fromStop.getTrainId(),
                            fromStop.getStationId(),
                            toStop.getStationId(),
                            fromStop.getStopIdx(),
                            toStop.getStopIdx(),
                            SeatType.SECOND_CLASS,
                            true,
                            diffMinute * 20
                    ));

                    prices.add(new PriceEntity(
                            null,
                            fromAreaId,
                            toAreaId,
                            fromStop.getTrainId(),
                            fromStop.getStationId(),
                            toStop.getStationId(),
                            fromStop.getStopIdx(),
                            toStop.getStopIdx(),
                            SeatType.FIRST_CLASS,
                            true,
                            diffMinute * 30
                    ));

                    prices.add(new PriceEntity(
                            null,
                            fromAreaId,
                            toAreaId,
                            fromStop.getTrainId(),
                            fromStop.getStationId(),
                            toStop.getStationId(),
                            fromStop.getStopIdx(),
                            toStop.getStopIdx(),
                            SeatType.BUSINESS,
                            true,
                            diffMinute * 60
                    ));
                }
            }
        }
        log.info("produce {} prices", prices.size());

        for (int i = 0; i < DAY_TO_GEN; i += 1) {
            LocalDate date = now.plusDays(i);
            if (priceMapper.existsByDate(date)) {
                continue;
            }

            for (PriceEntity entity : prices) {
                entity.setTrainDate(date);
            }
            int n = 3000;
            // 一天的所有分批插入放在同一个事务里，只提交一次，避免每批都刷盘
            transactionTemplate.executeWithoutResult(status -> {
                for (int j = 0; j < prices.size(); j += n) {
                    List<PriceEntity> sub = prices.subList(j, Math.min(j + n, prices.size()));
                    priceMapper.insertBatch(sub);
                }
            });
            log.info("import records on {} for prices", date);
        }
    }

    // ======================================================================
    // 基础数据：areas / stations / trains / cars
    // ======================================================================

    private void importBasicData() throws IOException {
        Path dir = dataProperties.getDir().toAbsolutePath().normalize();
        if (!Files.isDirectory(dir)) {
            throw new IllegalStateException("not a directory: " + dir + ", indicate with data.dir");
        }
        log.info("begin to import basic data in {}", dir);

        clearBasicTables();

        importAreas(dir.resolve("areas.csv"));
        importStations(dir.resolve("stations.csv"));
        importTrains(dir.resolve("trains.csv"));
        importCars(dir.resolve("cars.csv"));

        // 清空重建后刷新各 service 的内存缓存，否则同一次启动里用到的仍是旧缓存（空库时为空）
        areaService.reloadAll();
        stationService.reloadAll();
        trainService.reloadAll();
        carService.reloadAll();

        log.info("import basic data finish");
    }

    private void clearBasicTables() {
        for (String table : List.of("areas", "cars", "trains", "stations")) {
            jdbcTemplate.execute("TRUNCATE TABLE " + table);
        }
    }

    private void importAreas(Path path) throws IOException {
        List<AreaEntity> areas = new ArrayList<>();
        for (String[] row : readCsv(path)) {
            AreaEntity area = new AreaEntity();
            area.setId(Long.parseLong(row[0]));
            area.setName(row[1]);
            areas.add(area);
        }
        insertInBatches(areas, areaMapper::insertBatch, "areas");
    }

    private void importStations(Path path) throws IOException {
        List<StationEntity> stations = new ArrayList<>();
        for (String[] row : readCsv(path)) {
            StationEntity station = new StationEntity();
            station.setId(Long.parseLong(row[0]));
            station.setAreaId(Long.parseLong(row[1]));
            station.setTelecode(row[2]);
            station.setName(row[3]);
            stations.add(station);
        }
        insertInBatches(stations, stationMapper::insertBatch, "stations");
    }

    private void importTrains(Path path) throws IOException {
        List<TrainEntity> trains = new ArrayList<>();
        for (String[] row : readCsv(path)) {
            TrainEntity train = new TrainEntity();
            train.setId(Long.parseLong(row[0]));
            train.setNumber(row[1]);
            trains.add(train);
        }
        insertInBatches(trains, trainMapper::insertBatch, "trains");
    }

    private void importCars(Path path) throws IOException {
        List<CarEntity> cars = new ArrayList<>();
        for (String[] row : readCsv(path)) {
            CarEntity car = new CarEntity();
            car.setId(Long.parseLong(row[0]));
            car.setStyle(row[1]);
            car.setCode(row[2]);
            cars.add(car);
        }
        insertInBatches(cars, carMapper::insertBatch, "cars");
    }

    private <T> void insertInBatches(List<T> items, BatchInserter<T> inserter, String table) {
        for (int i = 0; i < items.size(); i += BASIC_BATCH_SIZE) {
            List<T> batch = items.subList(i, Math.min(i + BASIC_BATCH_SIZE, items.size()));
            int count = inserter.insert(batch);
            if (count != batch.size()) {
                throw new IllegalStateException(table + " insert error: expect " + batch.size() + " rows, got " + count + " rows");
            }
        }
        log.info("import {} rows for {}", items.size(), table);
    }

    @FunctionalInterface
    private interface BatchInserter<T> {
        int insert(List<T> items);
    }
}
