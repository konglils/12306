package cn.nispring._12306.service;

import cn.nispring._12306.entity.PriceEntity;
import cn.nispring._12306.entity.StationEntity;
import cn.nispring._12306.entity.TrainEntity;
import cn.nispring._12306.entity.TrainStationEntity;
import cn.nispring._12306.mapper.PriceMapper;
import cn.nispring._12306.mapper.StationMapper;
import cn.nispring._12306.mapper.TrainMapper;
import cn.nispring._12306.mapper.TrainStationMapper;
import cn.nispring._12306.model.Seat;
import cn.nispring._12306.model.Station;
import cn.nispring._12306.model.Ticket;
import cn.nispring._12306.model.Train;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RailCache {

    private final StationMapper stationMapper;
    private final TrainMapper trainMapper;
    private final TrainStationMapper trainStationMapper;
    private final PriceMapper priceMapper;

    private Map<Long, String> telecodeById;
    private Map<String, String> nameByTelecode;
    private Map<String, Long> stationIdByTelecode;

    private Map<Long, String> trainCodesByTrainId;
    private Map<Long, String> styleByTrainId;

    private Map<Long, Map<Long, TrainStationEntity>> stopByTrainAndStation;

    private Map<Long, Map<Long, List<PriceEntity>>> pricesByRoute;

    private Map<String, Train> trainByAnyCode;

    public RailCache(StationMapper stationMapper, TrainMapper trainMapper,
                     TrainStationMapper trainStationMapper, PriceMapper priceMapper) {
        this.stationMapper = stationMapper;
        this.trainMapper = trainMapper;
        this.trainStationMapper = trainStationMapper;
        this.priceMapper = priceMapper;
    }

    @PostConstruct
    void init() {
        loadStations();
        loadTrains();
        loadTrainStations();
        loadPrices();
        buildTrains();
    }

    private void loadStations() {
        telecodeById = new HashMap<>();
        nameByTelecode = new HashMap<>();
        stationIdByTelecode = new HashMap<>();

        for (StationEntity s : stationMapper.selectAll()) {
            telecodeById.put(s.id(), s.telecode());
            nameByTelecode.put(s.telecode(), s.name());
            stationIdByTelecode.put(s.telecode(), s.id());
        }
    }

    private void loadTrains() {
        trainCodesByTrainId = new HashMap<>();
        styleByTrainId = new HashMap<>();

        for (TrainEntity t : trainMapper.selectAll()) {
            styleByTrainId.put(t.id(), t.style());
        }
    }

    private void loadTrainStations() {
        stopByTrainAndStation = new HashMap<>();
        var stopsByTrainId = new HashMap<Long, List<TrainStationEntity>>();

        for (TrainStationEntity ts : trainStationMapper.selectAll()) {
            stopsByTrainId
                    .computeIfAbsent(ts.trainId(), k -> new ArrayList<>())
                    .add(ts);
        }

        for (var entry : stopsByTrainId.entrySet()) {
            // LinkedHashMap 保持 sequence 顺序，否则 .values() 时站序错乱
            var map = new LinkedHashMap<Long, TrainStationEntity>();
            for (TrainStationEntity ts : entry.getValue()) {
                map.put(ts.stationId(), ts);
            }
            stopByTrainAndStation.put(entry.getKey(), map);
        }

        for (var entry : stopsByTrainId.entrySet()) {
            Long trainId = entry.getKey();
            var codes = new ArrayList<String>();
            for (TrainStationEntity ts : entry.getValue()) {
                if (codes.isEmpty() || !codes.getLast().equals(ts.trainCode())) {
                    codes.add(ts.trainCode());
                }
            }
            trainCodesByTrainId.put(trainId, String.join("/", codes));
        }
    }

    private void loadPrices() {
        pricesByRoute = new HashMap<>();

        for (PriceEntity p : priceMapper.selectAll()) {
            pricesByRoute
                    .computeIfAbsent(p.fromStationId(), k -> new HashMap<>())
                    .computeIfAbsent(p.toStationId(), k -> new ArrayList<>())
                    .add(p);
        }
    }

    private void buildTrains() {
        trainByAnyCode = new HashMap<>();

        for (Long trainId : stopByTrainAndStation.keySet()) {
            String codes = trainCodesByTrainId.get(trainId);
            String style = styleByTrainId.get(trainId);

            List<TrainStationEntity> stops = new ArrayList<>(
                    stopByTrainAndStation.get(trainId).values());

            List<Station> stations = new ArrayList<>();
            for (TrainStationEntity ts : stops) {
                stations.add(new Station(
                        telecodeById.get(ts.stationId()),
                        ts.trainCode(),
                        ts.arriveDay(),
                        ts.arriveTime(),
                        ts.startDay(),
                        ts.startTime()
                ));
            }

            Train train = new Train(codes, style, stations);

            for (String code : codes.split("/")) {
                trainByAnyCode.put(code, train);
            }
        }
    }

    public List<Ticket> queryTickets(String fromTelecode, String toTelecode) {
        Long fromId = stationIdByTelecode.get(fromTelecode);
        Long toId = stationIdByTelecode.get(toTelecode);
        if (fromId == null || toId == null) return List.of();

        Map<Long, List<PriceEntity>> toPrices = pricesByRoute.get(fromId);
        if (toPrices == null) return List.of();

        List<PriceEntity> prices = toPrices.get(toId);
        if (prices == null) return List.of();

        List<Ticket> result = new ArrayList<>();

        for (PriceEntity p : prices) {
            Map<Long, TrainStationEntity> stops = stopByTrainAndStation.get(p.trainId());
            if (stops == null) continue;

            TrainStationEntity fromStop = stops.get(fromId);
            TrainStationEntity toStop = stops.get(toId);
            if (fromStop == null || toStop == null) continue;

            result.add(new Ticket(
                    fromStop.trainCode(),
                    fromStop.startTime(),
                    toStop.arriveDay(),
                    toStop.arriveTime(),
                    parseSeats(p.priceRaw())
            ));
        }
        return result;
    }

    public Train getTrain(String trainCode) {
        return trainByAnyCode.get(trainCode);
    }

    public Map<String, String> getStations() {
        return nameByTelecode;
    }

    // 价格编码: 每7字符一组，格式 <类型(1)><价格_角(5)><标记(1)>
    // 标记位非 0 表示该组实际为无座，类型码忽略
    // 余票数量后续接入 Redis 实时库存
    private List<Seat> parseSeats(String priceRaw) {
        var seats = new ArrayList<Seat>();
        for (int i = 0; i + 6 < priceRaw.length(); i += 7) {
            var group = priceRaw.substring(i, i + 7);
            var typeCode = group.substring(0, 1);
            var price = Integer.parseInt(group.substring(1, 6));
            var noSeatFlag = group.charAt(6);
            var type = noSeatFlag != '0' ? "无座" : typeName(typeCode);
            seats.add(new Seat(type, price, 0));
        }
        return seats;
    }

    private String typeName(String code) {
        return switch (code) {
            case "1" -> "硬座";
            case "2" -> "软座";
            case "3" -> "硬卧";
            case "4" -> "软卧";
            case "6" -> "高软";
            case "7" -> "一等软座";
            case "8" -> "二等软座";
            case "9" -> "商务";
            case "M" -> "一等";
            case "O" -> "二等";
            case "P" -> "特等";
            case "A" -> "高级动卧";
            case "F" -> "动卧";
            case "H" -> "一人软包";
            case "I" -> "一等卧";
            case "J" -> "二等卧";
            case "D" -> "优选一等";
            case "Q" -> "多功能座";
            default -> "未知";
        };
    }
}
