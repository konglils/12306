package cn.nispring.rail12306.scheduled;

import cn.nispring.rail12306.config.DataProperties;
import cn.nispring.rail12306.entity.StopEntity;
import cn.nispring.rail12306.mapper.StopMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static cn.nispring.rail12306.util.Util.readCsv;

@Component
public class TrainImport {

    private static final Logger log = LoggerFactory.getLogger(TrainImport.class);

    private final StopMapper stopMapper;
    private final DataProperties dataProperties;

    public TrainImport(StopMapper stopMapper, DataProperties dataProperties) {
        this.stopMapper = stopMapper;
        this.dataProperties = dataProperties;
    }

    /**
     * 每天 12:00（北京时间）和应用启动时执行
     */
    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Shanghai")
    @PostConstruct
    public void importTrain() throws IOException {
        importStop();
    }

    private void importStop() throws IOException {
        stopMapper.deleteOld();
        log.info("delete old records for table stops");

        Path path = dataProperties.getDir().resolve("stops.csv");
        List<StopEntity> stops = new ArrayList<>();
        List<String[]> rows = readCsv(path);
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

        // 生成接下来15天的时刻
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 15; i += 1) {
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
    }
}
