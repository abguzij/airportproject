package kg.airport.airportproject.service;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.entity.ApplicationUsersEntity;
import kg.airport.airportproject.entity.UserPositionsEntity;
import kg.airport.airportproject.entity.UserRolesEntity;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(value = "classpath:test.properties")
public class ApplicationUserServiceTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @Autowired
    private ApplicationUsersEntityRepository applicationUsersEntityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationUserService applicationUserService;

    @Test
    public void testDeleteAccountById_OK() {
        ApplicationUsersEntity applicationUsersEntity =
                this.applicationUsersEntityRepository.save(this.createDefaultClientEntity());
        try {
            ApplicationUserResponseDto applicationUserResponseDto =
                    this.applicationUserService.deleteAccountById(applicationUsersEntity.getId());

            Assertions.assertEquals(applicationUsersEntity.getId(), applicationUserResponseDto.getId());
            Assertions.assertEquals("test", applicationUserResponseDto.getUsername());
            Assertions.assertEquals("Test Fullname", applicationUserResponseDto.getFullName());
            Assertions.assertEquals("CLIENT", applicationUserResponseDto.getPositionTitle());
            Assertions.assertEquals(
                    applicationUsersEntity.getRegisteredAt(),
                    applicationUserResponseDto.getRegisteredAt()
            );
            Assertions.assertEquals(Boolean.FALSE, applicationUserResponseDto.getEnabled());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateUsersInformation_OK() {
        ApplicationUsersEntity applicationUsersEntity =
                this.applicationUsersEntityRepository.save(this.createDefaultClientEntity());
        try {
            ApplicationUserRequestDto applicationUserRequestDto = new ApplicationUserRequestDto();
            applicationUserRequestDto
                    .setFullName("New Test FullName")
                    .setUsername("New-Test-Username")
                    .setPositionId(9L)
                    .setPassword("password");

            ApplicationUserResponseDto applicationUserResponseDto =
                    this.applicationUserService.updateUsersInformation(
                            applicationUserRequestDto,
                            applicationUsersEntity.getId()
                    );

            Assertions.assertEquals("New Test FullName", applicationUserResponseDto.getFullName());
            Assertions.assertEquals("New-Test-Username", applicationUserResponseDto.getUsername());
            Assertions.assertEquals("STEWARD", applicationUserResponseDto.getPositionTitle());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllClients_OK() {
        List<ApplicationUsersEntity> applicationUsersEntityList = new ArrayList<>();
        applicationUsersEntityList.add(
                this.createClientEntityByParameters(
                        "first",
                        "firsts_fullname"
                )
        );
        applicationUsersEntityList.add(
                this.createClientEntityByParameters(
                        "second",
                        "seconds_fullname"
                )
        );
        applicationUsersEntityList.add(
                this.createClientEntityByParameters(
                        "third",
                        "thirds_fullname"
                )
        );
        applicationUsersEntityList.add(
                this.createStewardEntityByParameters(
                        "first_steward",
                        "first_steward_fullname"
                )
        );
        applicationUsersEntityList = this.applicationUsersEntityRepository.saveAll(applicationUsersEntityList);

        applicationUsersEntityList.get(0).setRegisteredAt(LocalDateTime.parse("2011-06-03T10:15:30", formatter));
        applicationUsersEntityList.get(0).setEnabled(Boolean.TRUE);

        applicationUsersEntityList.get(1).setRegisteredAt(LocalDateTime.parse("2015-06-03T10:15:30", formatter));
        applicationUsersEntityList.get(1).setEnabled(Boolean.TRUE);

        applicationUsersEntityList.get(2).setRegisteredAt(LocalDateTime.parse("2014-06-03T10:15:30", formatter));
        applicationUsersEntityList.get(2).setEnabled(Boolean.FALSE);

        applicationUsersEntityList.get(3).setRegisteredAt(LocalDateTime.parse("2014-07-12T10:15:30", formatter));
        applicationUsersEntityList.get(3).setEnabled(Boolean.FALSE);

        applicationUsersEntityList = this.applicationUsersEntityRepository.saveAll(applicationUsersEntityList);

        try {
            LocalDateTime startDateFilter = LocalDateTime.parse("2010-01-03T10:15:30", formatter);
            LocalDateTime endDateFilter = LocalDateTime.parse("2014-12-03T10:15:30", formatter);

            System.out.println(applicationUsersEntityList);
            List<ApplicationUserResponseDto> applicationUserResponseDtoList =
                    this.applicationUserService.getAllClients(
                            endDateFilter,
                            startDateFilter,
                            Boolean.FALSE
                    );

            Assertions.assertEquals(1, applicationUserResponseDtoList.size());
            Assertions.assertEquals("third", applicationUserResponseDtoList.get(0).getUsername());
            Assertions.assertEquals("thirds_fullname", applicationUserResponseDtoList.get(0).getFullName());
            Assertions.assertEquals("CLIENT", applicationUserResponseDtoList.get(0).getPositionTitle());

            Assertions.assertTrue(startDateFilter.isBefore(applicationUserResponseDtoList.get(0).getRegisteredAt()));
            Assertions.assertTrue(endDateFilter.isAfter(applicationUserResponseDtoList.get(0).getRegisteredAt()));
            Assertions.assertEquals(Boolean.FALSE, applicationUserResponseDtoList.get(0).getEnabled());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllEmployees_OK() {
        List<ApplicationUsersEntity> applicationUsersEntityList = new ArrayList<>();
        applicationUsersEntityList.add(
                this.createPilotEntityByParameters(
                        "pilot_1",
                        "pilot_1"
                )
        );
        applicationUsersEntityList.add(
                this.createStewardEntityByParameters(
                        "steward_1",
                        "steward_1"
                )
        );
        applicationUsersEntityList.add(
                this.createStewardEntityByParameters(
                        "steward_2",
                        "steward_2"
                )
        );
        applicationUsersEntityList.add(
                this.createChiefStewardEntityByParameters(
                        "chief_steward_1",
                        "chief_steward_1"
                )
        );
        applicationUsersEntityList.add(
                this.createClientEntityByParameters(
                        "client",
                        "client"
                )
        );

        applicationUsersEntityList = this.applicationUsersEntityRepository.saveAll(applicationUsersEntityList);

        applicationUsersEntityList.get(0).setRegisteredAt(LocalDateTime.parse("2011-06-03T10:15:30", formatter));
        applicationUsersEntityList.get(0).setEnabled(Boolean.TRUE);

        applicationUsersEntityList.get(1).setRegisteredAt(LocalDateTime.parse("2012-06-03T10:15:30", formatter));
        applicationUsersEntityList.get(1).setEnabled(Boolean.TRUE);

        applicationUsersEntityList.get(2).setRegisteredAt(LocalDateTime.parse("2013-06-03T10:15:30", formatter));
        applicationUsersEntityList.get(2).setEnabled(Boolean.FALSE);

        applicationUsersEntityList.get(3).setRegisteredAt(LocalDateTime.parse("2014-07-12T10:15:30", formatter));
        applicationUsersEntityList.get(3).setEnabled(Boolean.TRUE);

        applicationUsersEntityList.get(4).setRegisteredAt(LocalDateTime.parse("2015-07-12T10:15:30", formatter));
        applicationUsersEntityList.get(4).setEnabled(Boolean.FALSE);

        applicationUsersEntityList = this.applicationUsersEntityRepository.saveAll(applicationUsersEntityList);

        try {
            LocalDateTime startDateFilter = LocalDateTime.parse("2010-01-03T10:15:30", formatter);
            LocalDateTime endDateFilter = LocalDateTime.parse("2013-12-03T10:15:30", formatter);

            System.out.println(applicationUsersEntityList);
            List<ApplicationUserResponseDto> applicationUserResponseDtoList =
                    this.applicationUserService.getAllEmployees(
                            endDateFilter,
                            startDateFilter,
                            Boolean.TRUE,
                            List.of("PILOT", "STEWARD")
                    );

            Assertions.assertEquals(2, applicationUserResponseDtoList.size());

            Assertions.assertEquals("pilot_1", applicationUserResponseDtoList.get(0).getUsername());
            Assertions.assertEquals("pilot_1", applicationUserResponseDtoList.get(0).getFullName());
            Assertions.assertEquals("PILOT", applicationUserResponseDtoList.get(0).getPositionTitle());

            Assertions.assertTrue(startDateFilter.isBefore(applicationUserResponseDtoList.get(0).getRegisteredAt()));
            Assertions.assertTrue(endDateFilter.isAfter(applicationUserResponseDtoList.get(0).getRegisteredAt()));
            Assertions.assertEquals(Boolean.TRUE, applicationUserResponseDtoList.get(0).getEnabled());

            Assertions.assertEquals("steward_1", applicationUserResponseDtoList.get(1).getUsername());
            Assertions.assertEquals("steward_1", applicationUserResponseDtoList.get(1).getFullName());
            Assertions.assertEquals("STEWARD", applicationUserResponseDtoList.get(1).getPositionTitle());

            Assertions.assertTrue(startDateFilter.isBefore(applicationUserResponseDtoList.get(1).getRegisteredAt()));
            Assertions.assertTrue(endDateFilter.isAfter(applicationUserResponseDtoList.get(1).getRegisteredAt()));
            Assertions.assertEquals(Boolean.TRUE, applicationUserResponseDtoList.get(1).getEnabled());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    private ApplicationUsersEntity createDefaultClientEntity() {
        ApplicationUsersEntity applicationUsersEntity = new ApplicationUsersEntity()
                .setUsername("test")
                .setFullName("Test Fullname")
                .setPassword(this.passwordEncoder.encode("test"))
                .setEnabled(Boolean.TRUE)
                .setRegisteredAt(LocalDateTime.now())
                .setUserPosition(new UserPositionsEntity().setId(10L).setPositionTitle("CLIENT"));

        applicationUsersEntity.getUserRolesEntityList().add(new UserRolesEntity().setId(1L).setRoleTitle("CLIENT"));
        return applicationUsersEntity;
    }

    private ApplicationUsersEntity createClientEntityByParameters(
            String username,
            String fullName
    ) {
        ApplicationUsersEntity applicationUsersEntity = new ApplicationUsersEntity()
                .setUsername(username)
                .setFullName(fullName)
                .setPassword(this.passwordEncoder.encode("test"))
                .setUserPosition(new UserPositionsEntity().setId(10L).setPositionTitle("CLIENT"));

        applicationUsersEntity.getUserRolesEntityList().add(new UserRolesEntity().setId(1L).setRoleTitle("CLIENT"));

        return applicationUsersEntity;
    }

    private ApplicationUsersEntity createStewardEntityByParameters(
            String username,
            String fullName
    ) {
        ApplicationUsersEntity applicationUsersEntity = new ApplicationUsersEntity()
                .setUsername(username)
                .setFullName(fullName)
                .setPassword(this.passwordEncoder.encode("test"))
                .setUserPosition(new UserPositionsEntity().setId(9L).setPositionTitle("STEWARD"));

        applicationUsersEntity.getUserRolesEntityList().add(new UserRolesEntity().setId(10L).setRoleTitle("STEWARD"));

        return applicationUsersEntity;
    }

    private ApplicationUsersEntity createChiefStewardEntityByParameters(
            String username,
            String fullName
    ) {
        ApplicationUsersEntity applicationUsersEntity = new ApplicationUsersEntity()
                .setUsername(username)
                .setFullName(fullName)
                .setPassword(this.passwordEncoder.encode("test"))
                .setUserPosition(new UserPositionsEntity().setId(8L).setPositionTitle("CHIEF_STEWARD"));

        applicationUsersEntity.getUserRolesEntityList().add(
                new UserRolesEntity().setId(9L).setRoleTitle("CHIEF_STEWARD")
        );

        return applicationUsersEntity;
    }

    private ApplicationUsersEntity createPilotEntityByParameters(
            String username,
            String fullName
    ) {
        ApplicationUsersEntity applicationUsersEntity = new ApplicationUsersEntity()
                .setUsername(username)
                .setFullName(fullName)
                .setPassword(this.passwordEncoder.encode("test"))
                .setUserPosition(new UserPositionsEntity().setId(7L).setPositionTitle("PILOT"));

        applicationUsersEntity.getUserRolesEntityList().add(new UserRolesEntity().setId(8L).setRoleTitle("PILOT"));

        return applicationUsersEntity;
    }
}