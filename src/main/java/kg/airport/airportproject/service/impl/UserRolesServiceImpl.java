package kg.airport.airportproject.service.impl;

import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import kg.airport.airportproject.mapper.RolesMapper;
import kg.airport.airportproject.repository.UserRolesEntityRepository;
import kg.airport.airportproject.service.UserRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRolesServiceImpl implements UserRolesService {
    private final UserRolesEntityRepository userRolesEntityRepository;

    @Autowired
    public UserRolesServiceImpl(UserRolesEntityRepository userRolesEntityRepository) {
        this.userRolesEntityRepository = userRolesEntityRepository;
    }

    @Override
    public List<UserRoleResponseDto> getAllUserRoles()
            throws UserRolesNotFoundException
    {
        List<UserRolesEntity> userRolesEntityList = this.userRolesEntityRepository.findAll();
        if(userRolesEntityList.isEmpty()) {
            throw new UserRolesNotFoundException("В системе не было создано ни одной роли!");
        }
        return RolesMapper.mapEntityListToUserRoleResponseDtoList(userRolesEntityList);
    }
}
