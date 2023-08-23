package kg.airport.airportproject.service.impl;

import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.exception.ApplicationUserNotFoundException;
import kg.airport.airportproject.exception.InvalidIdException;
import kg.airport.airportproject.exception.UserRolesNotFoundException;
import kg.airport.airportproject.mapper.RolesMapper;
import kg.airport.airportproject.repository.UserRolesEntityRepository;
import kg.airport.airportproject.service.ApplicationUserService;
import kg.airport.airportproject.service.UserRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserRolesServiceImpl implements UserRolesService {
    private final UserRolesEntityRepository userRolesEntityRepository;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public UserRolesServiceImpl(
            UserRolesEntityRepository userRolesEntityRepository,
            ApplicationUserService applicationUserService
    ) {
        this.userRolesEntityRepository = userRolesEntityRepository;
        this.applicationUserService = applicationUserService;
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

    @Override
    public List<UserRoleResponseDto> getUserRoles(Long userId)
            throws ApplicationUserNotFoundException,
            InvalidIdException,
            UserRolesNotFoundException
    {
        ApplicationUsersEntity user = this.applicationUserService.getApplicationUserById(userId);
        List<UserRolesEntity> userRolesEntities = user.getUserRolesEntityList();
        if(userRolesEntities.isEmpty()) {
            throw new UserRolesNotFoundException(
                    String.format("Для пользователя с ID[%d] не задано ни одной роли!", userId)
            );
        }
        return RolesMapper.mapEntityListToUserRoleResponseDtoList(userRolesEntities);
    }

    @Override
    public List<UserRoleResponseDto> updateUsersRoles(
            List<Long> rolesIdList,
            Long userId
    )
            throws ApplicationUserNotFoundException,
            InvalidIdException
    {
        if(Objects.isNull(rolesIdList) || rolesIdList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Список ID добавляемых пользователю ролей не может быть null или пустым!"
            );
        }

        ApplicationUsersEntity applicationUser = this.applicationUserService.getApplicationUserById(userId);
        this.excludeExistingUsersRolesIds(rolesIdList, applicationUser);

        if(!rolesIdList.isEmpty()) {
            List<UserRolesEntity> foundUserRolesByIdIn =
                    this.userRolesEntityRepository.getUserRolesEntitiesByIdIn(rolesIdList);
            if(foundUserRolesByIdIn.isEmpty()) {
                throw new UserRolesNotFoundException("По заданным ID не найдено ни одной роли!");
            }

            for (UserRolesEntity userRole : foundUserRolesByIdIn) {
                userRole.getApplicationUsersEntityList().add(applicationUser);
                applicationUser.getUserRolesEntityList().add(userRole);
            }

            this.userRolesEntityRepository.saveAll(foundUserRolesByIdIn);
        }
        return RolesMapper.mapEntityListToUserRoleResponseDtoList(applicationUser.getUserRolesEntityList());
    }

    private List<Long> excludeExistingUsersRolesIds(
            List<Long> searchedIdList,
            ApplicationUsersEntity applicationUser
    ) {
        Set<Long> userRolesIdList = applicationUser.getUserRolesEntityList()
                .stream()
                .map(UserRolesEntity::getId)
                .collect(Collectors.toSet());

        for (Long searchedID : searchedIdList) {
            if(userRolesIdList.contains(searchedID)) {
                searchedIdList.remove(searchedID);
            }
        }
        return searchedIdList;
    }
}
