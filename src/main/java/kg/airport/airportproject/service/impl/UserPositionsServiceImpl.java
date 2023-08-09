package kg.airport.airportproject.service.impl;

import com.querydsl.core.BooleanBuilder;
import kg.airport.airportproject.dto.UserPositionResponseDto;
import kg.airport.airportproject.entity.QUserPositionsEntity;
import kg.airport.airportproject.entity.UserPositionsEntity;
import kg.airport.airportproject.mapper.ApplicationUsersMapper;
import kg.airport.airportproject.repository.UserPositionsEntityRepository;
import kg.airport.airportproject.service.UserPositionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPositionsServiceImpl implements UserPositionsService {
    private final UserPositionsEntityRepository userPositionsEntityRepository;

    @Autowired
    public UserPositionsServiceImpl(UserPositionsEntityRepository userPositionsEntityRepository) {
        this.userPositionsEntityRepository = userPositionsEntityRepository;
    }

    @Override
    public List<UserPositionResponseDto> getAllEmployeePositions() {
        List<UserPositionsEntity> userPositionsEntities =
                this.userPositionsEntityRepository.getUserPositionsEntitiesByPositionTitleNot("CLIENT");
        return ApplicationUsersMapper.mapToUserPositionResponseDtoList(userPositionsEntities);
    }
}
