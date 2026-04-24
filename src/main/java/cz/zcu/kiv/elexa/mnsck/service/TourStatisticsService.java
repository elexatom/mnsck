package cz.zcu.kiv.elexa.mnsck.service;

import cz.zcu.kiv.elexa.mnsck.repository.BookingRepository;
import cz.zcu.kiv.elexa.mnsck.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourStatisticsService {
    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;

    public long getAverageOccupancy() {
        long tourCount = tourRepository.count();
        if (tourCount == 0) return 0;

        double totalOccupancyRatio = 0;
        for (var tour : tourRepository.findAll()) {
            if (tour.getCapacity() > 0) {
                totalOccupancyRatio += (double) bookingRepository.sumOccupiedCapacity(tour, "CANCELLED") / tour.getCapacity();
            }
        }

        return Math.round((totalOccupancyRatio / tourCount) * 100);
    }
}
