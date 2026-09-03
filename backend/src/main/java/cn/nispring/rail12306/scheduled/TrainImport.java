package cn.nispring.rail12306.scheduled;

import cn.nispring.rail12306.config.DataProperties;
import cn.nispring.rail12306.entity.CarLayoutEntity;
import cn.nispring.rail12306.entity.StopEntity;
import cn.nispring.rail12306.mapper.CarLayoutMapper;
import cn.nispring.rail12306.mapper.StopMapper;
import cn.nispring.rail12306.model.layout.Layout;
import tools.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    public TrainImport(StopMapper stopMapper, DataProperties dataProperties, ObjectMapper objectMapper,
                       CarLayoutMapper carLayoutMapper) {
        this.stopMapper = stopMapper;
        this.dataProperties = dataProperties;
        this.objectMapper = objectMapper;
        this.carLayoutMapper = carLayoutMapper;
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

        // 生成接下来15天的时刻
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

        stopMapper.deleteOld();
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

        // 生成接下来15天的座位布局
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
    }
}
