package kg.airport.airportproject.service.impl;

import kg.airport.airportproject.dto.AircraftsRepairsStatisticsResponseDto;
import kg.airport.airportproject.dto.DestinationStatisticsResponseDto;
import kg.airport.airportproject.exception.FlightsNotFoundException;
import kg.airport.airportproject.exception.PartInspectionsNotFoundException;
import kg.airport.airportproject.repository.FlightsEntityRepository;
import kg.airport.airportproject.repository.PartInspectionsEntityRepository;
import kg.airport.airportproject.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final FlightsEntityRepository flightsEntityRepository;
    private final PartInspectionsEntityRepository partInspectionsEntityRepository;

    @Autowired
    public StatisticsServiceImpl(
            FlightsEntityRepository flightsEntityRepository,
            PartInspectionsEntityRepository partInspectionsEntityRepository
    ) {
        this.flightsEntityRepository = flightsEntityRepository;
        this.partInspectionsEntityRepository = partInspectionsEntityRepository;
    }

    @Override
    public List<DestinationStatisticsResponseDto> getDestinationStatistics() throws FlightsNotFoundException
    {
        List<String> distinctDestinations = this.flightsEntityRepository.getDistinctDestinationValues();
        if (distinctDestinations.isEmpty()) {
            throw new FlightsNotFoundException("В системе не было зарегистрировано ни одного рейса!");
        }

        List<Integer> flightsNumbers =
                this.flightsEntityRepository.getDestinationsFlightsNumbersByDestinationIn(distinctDestinations);

        List<DestinationStatisticsResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < distinctDestinations.size(); i++) {
            DestinationStatisticsResponseDto responseDto = new DestinationStatisticsResponseDto();
            responseDto.setDestination(distinctDestinations.get(i));
            responseDto.setNumberOfFlights(flightsNumbers.get(i));
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    @Override
    public List<AircraftsRepairsStatisticsResponseDto> getAircraftRepairsStatistics()
            throws PartInspectionsNotFoundException
    {
        List<String> distinctAircraftTitles =
                this.partInspectionsEntityRepository.getDistinctServicedAircraftsTitles();
        if(distinctAircraftTitles.isEmpty()) {
            throw new PartInspectionsNotFoundException("Самолетов проходивших ремонт не найдено!");
        }

        List<Integer> numberOfRepairedParts
                = this.partInspectionsEntityRepository.getNumbersOfRepairedPartsPerAircraft();

        List<AircraftsRepairsStatisticsResponseDto> aircraftsRepairsStatisticsResponseDtoList = new ArrayList<>();
        for (int i = 0; i < distinctAircraftTitles.size(); i++) {
            AircraftsRepairsStatisticsResponseDto responseDto = new AircraftsRepairsStatisticsResponseDto();
            responseDto.setAircraftTitle(distinctAircraftTitles.get(i));
            responseDto.setNumberOfRepairedParts(numberOfRepairedParts.get(i));
            aircraftsRepairsStatisticsResponseDtoList.add(responseDto);
        }
        return aircraftsRepairsStatisticsResponseDtoList;
    }
}
