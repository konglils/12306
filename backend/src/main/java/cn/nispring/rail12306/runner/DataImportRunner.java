package cn.nispring.rail12306.runner;

import cn.nispring.rail12306.entity.AreaEntity;
import cn.nispring.rail12306.entity.CarEntity;
import cn.nispring.rail12306.entity.StationEntity;
import cn.nispring.rail12306.entity.TrainEntity;
import cn.nispring.rail12306.mapper.AreaMapper;
import cn.nispring.rail12306.mapper.CarMapper;
import cn.nispring.rail12306.mapper.StationMapper;
import cn.nispring.rail12306.mapper.TrainMapper;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取 data/ 下的 csv 文件并写入数据库
 * 通过 --data.import.enabled=true 启用, --data.import.dir 指定数据目录
 * 启动命令示例: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--data.import.enabled=true"
 */
@Component
@ConditionalOnProperty(prefix = "data.import", name = "enabled", havingValue = "true")
public class DataImportRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataImportRunner.class);
    private static final int BATCH_SIZE = 1000;

    @Value("${data.import.dir:../data}")
    private String dataDir;

    private final AreaMapper areaMapper;
    private final CarMapper carMapper;
    private final TrainMapper trainMapper;
    private final StationMapper stationMapper;
    private final JdbcTemplate jdbcTemplate;

    public DataImportRunner(AreaMapper areaMapper, CarMapper carMapper,
                            TrainMapper trainMapper, StationMapper stationMapper,
                            JdbcTemplate jdbcTemplate) {
        this.areaMapper = areaMapper;
        this.carMapper = carMapper;
        this.trainMapper = trainMapper;
        this.stationMapper = stationMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String @NonNull ... args) throws IOException {
        Path dir = Path.of(dataDir).toAbsolutePath().normalize();
        if (!Files.isDirectory(dir)) {
            throw new IllegalStateException("not a directory: " + dir + ", indicate with --data.import.dir=/path/to/data");
        }
        log.info("begin to import basic data in {}", dir);

        clearTables();

        importAreas(dir.resolve("areas.csv"));
        importStations(dir.resolve("stations.csv"));
        importTrains(dir.resolve("trains.csv"));
        importCars(dir.resolve("cars.csv"));

        log.info("import finish");
    }

    private void clearTables() {
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

    private static List<String[]> readCsv(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException("not a file: " + path);
        }
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                rows.add(line.split(",", -1));
            }
        }
        return rows;
    }

    private <T> void insertInBatches(List<T> items, BatchInserter<T> inserter, String table) {
        for (int i = 0; i < items.size(); i += BATCH_SIZE) {
            List<T> batch = items.subList(i, Math.min(i + BATCH_SIZE, items.size()));
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
