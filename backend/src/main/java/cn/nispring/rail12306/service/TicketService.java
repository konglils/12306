package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.PriceEntity;
import cn.nispring.rail12306.entity.SeatEntity;
import cn.nispring.rail12306.entity.StopEntity;
import cn.nispring.rail12306.mapper.PriceMapper;
import cn.nispring.rail12306.mapper.SeatMapper;
import cn.nispring.rail12306.mapper.StopMapper;
import cn.nispring.rail12306.model.Seat;
import cn.nispring.rail12306.model.Ticket;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class TicketService {

    private final PriceMapper priceMapper;
    private final StopMapper stopMapper;
    private final StationService stationService;
    private final SeatMapper seatMapper;

    public TicketService(PriceMapper priceMapper, StopMapper stopMapper, StationService stationService,
                         SeatMapper seatMapper) {
        this.priceMapper = priceMapper;
        this.stopMapper = stopMapper;
        this.stationService = stationService;
        this.seatMapper = seatMapper;
    }

    public List<Ticket> getTickets(LocalDate date, long fromAreaId, long toAreaId) {
        List<PriceEntity> priceEntities = priceMapper.select(date, fromAreaId, toAreaId);
        Map<TicketKey, List<PriceEntity>> ticketMap = new HashMap<>();
        for (PriceEntity price : priceEntities) {
            TicketKey key = new TicketKey(price.getTrainId(), price.getFromStationId(), price.getToStationId());
            ticketMap.computeIfAbsent(key, k -> new ArrayList<>()).add(price);
        }

        List<Ticket> tickets = new ArrayList<>();
        for (List<PriceEntity> prices : ticketMap.values()) {
            PriceEntity first = prices.getFirst();
            StopEntity fromStop = stopMapper.selectByStopIdx(date, first.getTrainId(), first.getFromStopIdx());
            StopEntity toStop = stopMapper.selectByStopIdx(date, first.getTrainId(), first.getToStopIdx());
            if (fromStop == null || toStop == null) {
                continue;
            }

            List<Seat> seats = new ArrayList<>();
            for (PriceEntity price : prices) {
                List<SeatEntity> seatEntities = seatMapper.select(date, price.getTrainId(), price.getSeatType(),
                        price.getFromStopIdx(), price.getToStopIdx() - 1);

                if (seatEntities.isEmpty()) {
                    seats.add(new Seat(price.getSeatType(), price.getHasSeat(), price.getPrice(), 0));
                    continue;
                }
                byte[] firstGraph = seatEntities.getFirst().getGraph();

                int length = firstGraph.length;
                byte[] sumGraph = Arrays.copyOf(firstGraph, length);
                for (int i = 1; i < seatEntities.size(); i += 1) {
                    for (int j = 0; j < length; j += 1) {
                        sumGraph[j] &= seatEntities.get(i).getGraph()[j];
                    }
                }
                int numOne = 0;
                for (int i = 0; i < length; i += 1) {
                    numOne += Integer.bitCount(sumGraph[i]);
                }
                seats.add(new Seat(price.getSeatType(), price.getHasSeat(), price.getPrice(), numOne));
            }

            Ticket ticket = new Ticket(fromStop.getTrainCode(),
                    stationService.get(fromStop.getStationId()).telecode(),
                    stationService.get(toStop.getStationId()).telecode(),
                    fromStop.getStartTime(),
                    toStop.getArriveDay() - fromStop.getStartDay(),
                    toStop.getArriveTime(),
                    seats);
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
