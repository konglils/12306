package cn.nispring.rail12306.scheduled;

import cn.nispring.rail12306.config.DataProperties;
import cn.nispring.rail12306.entity.CarLayoutEntity;
import cn.nispring.rail12306.entity.PriceEntity;
import cn.nispring.rail12306.entity.StopEntity;
import cn.nispring.rail12306.mapper.CarLayoutMapper;
import cn.nispring.rail12306.mapper.PriceMapper;
import cn.nispring.rail12306.mapper.StopMapper;
import cn.nispring.rail12306.model.SeatType;
import cn.nispring.rail12306.model.layout.Layout;
import cn.nispring.rail12306.service.StationService;
import tools.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static cn.nispring.rail12306.util.Util.readCsv;

@Component
public class TrainImport {

    private static final Logger log = LoggerFactory.getLogger(TrainImport.class);

    private final StopMapper stopMapper;
    private final DataProperties dataProperties;
    private final ObjectMapper objectMapper;
    private final CarLayoutMapper carLayoutMapper;
    private final StationService stationService;
    private final PriceMapper priceMapper;
    private final TransactionTemplate transactionTemplate;

    public TrainImport(StopMapper stopMapper, DataProperties dataProperties, ObjectMapper objectMapper,
                       CarLayoutMapper carLayoutMapper, StationService stationService, PriceMapper priceMapper,
                       PlatformTransactionManager transactionManager) {
        this.stopMapper = stopMapper;
        this.dataProperties = dataProperties;
        this.objectMapper = objectMapper;
        this.carLayoutMapper = carLayoutMapper;
        this.stationService = stationService;
        this.priceMapper = priceMapper;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * 每天 12:00（北京时间）和应用启动时执行
     */
    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Shanghai")
    @PostConstruct
    public void importTrain() throws IOException {
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
        int dayToGen = 15;

        for (int i = 0; i < dayToGen; i += 1) {
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

        for (int i = 0; i < dayToGen; i += 1) {
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

        for (int i = 0; i < dayToGen; i += 1) {
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
}
