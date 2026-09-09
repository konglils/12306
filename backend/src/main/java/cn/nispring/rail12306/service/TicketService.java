package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.PriceEntity;
import cn.nispring.rail12306.entity.StopEntity;
import cn.nispring.rail12306.mapper.PriceMapper;
import cn.nispring.rail12306.mapper.StopMapper;
import cn.nispring.rail12306.model.Seat;
import cn.nispring.rail12306.model.Ticket;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    private final PriceMapper priceMapper;
    private final StopMapper stopMapper;
    private final StationService stationService;

    public TicketService(PriceMapper priceMapper, StopMapper stopMapper, StationService stationService) {
        this.priceMapper = priceMapper;
        this.stopMapper = stopMapper;
        this.stationService = stationService;
    }

    public List<Ticket> getTickets(LocalDate date, long fromAreaId, long toAreaId) {
        List<PriceEntity> entities = priceMapper.select(date, fromAreaId, toAreaId);
        Map<TicketKey, List<PriceEntity>> ticketMap = new HashMap<>();
        for (PriceEntity entity : entities) {
            TicketKey key = new TicketKey(entity.getTrainId(), entity.getFromStationId(), entity.getToStationId());
            ticketMap.computeIfAbsent(key, k -> new ArrayList<>()).add(entity);
        }

        List<Ticket> tickets = new ArrayList<>();
        for (List<PriceEntity> prices : ticketMap.values()) {
            PriceEntity first = prices.getFirst();
            StopEntity fromStop = stopMapper.selectByStopIdx(date, first.getTrainId(), first.getFromStopIdx());
            StopEntity toStop = stopMapper.selectByStopIdx(date, first.getTrainId(), first.getToStopIdx());

            Ticket ticket = new Ticket(fromStop.getTrainCode(),
                    stationService.get(fromStop.getStationId()).telecode(),
                    stationService.get(toStop.getStationId()).telecode(),
                    fromStop.getStartTime(),
                    toStop.getArriveDay() - fromStop.getStartDay(),
                    toStop.getArriveTime(),
                    prices.stream().map(price -> new Seat(
                            price.getSeatType(),
                            price.getPrice(),
                            0 // TODO 查找 seats 表
                    )).toList());
            tickets.add(ticket);
        }
        return tickets;
    }

    record TicketKey(
            Long trainId,
            Long fromStationId,
            Long toStationId
    ) {
    }
}
