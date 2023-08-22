package kg.airport.airportproject.mapper;

import kg.airport.airportproject.dto.UserRoleResponseDto;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.entity.UserRolesTestEntityProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RolesMapperTest {
    @Test
    public void testMapEntityToUserRoleResponseDto_OK() {
        UserRolesEntity source = UserRolesTestEntityProvider
                .getTestClientRoleTestEntityByRoleTitle(UserRolesTestEntityProvider.TEST_CLIENT_ROLE_TITLE).get(0);

        UserRoleResponseDto result = RolesMapper.mapEntityToUserRoleResponseDto(source);

        Assertions.assertEquals(source.getId(), result.getId());
        Assertions.assertEquals(source.getRoleTitle(), result.getRoleTitle());
    }

    @Test
    public void testMapEntityListToUserRoleResponseDtoList_OK() {
        List<UserRolesEntity> sourceList = UserRolesTestEntityProvider
                .getTestClientRoleTestEntityByRoleTitle(UserRolesTestEntityProvider.TEST_CLIENT_ROLE_TITLE);

        List<UserRoleResponseDto> resultList =
                RolesMapper.mapEntityListToUserRoleResponseDtoList(sourceList);

        Assertions.assertEquals(sourceList.size(), resultList.size());
        Assertions.assertEquals(sourceList.get(0).getId(), sourceList.get(0).getId());
        Assertions.assertEquals(sourceList.get(0).getRoleTitle(), sourceList.get(0).getRoleTitle());
    }
}