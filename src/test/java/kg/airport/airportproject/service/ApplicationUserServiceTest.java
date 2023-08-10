package kg.airport.airportproject.service;

import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.dto.ApplicationUserRequestDto;
import kg.airport.airportproject.dto.ApplicationUserResponseDto;
import kg.airport.airportproject.entity.*;
import kg.airport.airportproject.entity.attributes.AircraftStatus;
import kg.airport.airportproject.entity.attributes.AircraftType;
import kg.airport.airportproject.entity.attributes.UserFlightsStatus;
import kg.airport.airportproject.repository.AircraftsEntityRepository;
import kg.airport.airportproject.repository.ApplicationUsersEntityRepository;
import kg.airport.airportproject.repository.UserFlightsEntityRepository;
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
    private UserFlightsEntityRepository userFlightsEntityRepository;
    @Autowired
    private AircraftsEntityRepository aircraftsEntityRepository;
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

    @Test
    public void testGetAllFreeCrewMembers_OK() {
        ApplicationUsersEntity pilot1 = this.createPilotEntityByParameters(
                "pilot_1",
                "pilot_1"
        );
        pilot1 = this.applicationUsersEntityRepository.save(pilot1);

        ApplicationUsersEntity pilot2 = this.createPilotEntityByParameters(
                "pilot_2",
                "pilot_2"
        );
        pilot2 = this.applicationUsersEntityRepository.save(pilot2);

        ApplicationUsersEntity steward1 = this.createStewardEntityByParameters(
                "steward_1",
                "steward_1"
        );
        steward1 = this.applicationUsersEntityRepository.save(steward1);

        ApplicationUsersEntity steward2 = this.createStewardEntityByParameters(
                "steward_2",
                "steward_2"
        );
        steward2 = this.applicationUsersEntityRepository.save(steward2);

        ApplicationUsersEntity chiefSteward1 = this.createChiefStewardEntityByParameters(
                "chief_steward_1",
                "chief_steward_1"
        );
        chiefSteward1 = this.applicationUsersEntityRepository.save(chiefSteward1);

        ApplicationUsersEntity client = this.createClientEntityByParameters(
                "client",
                "client"
        );
        client = this.applicationUsersEntityRepository.save(client);

        List<UserFlightsEntity> userFlightsEntities = new ArrayList<>();
        userFlightsEntities.add(
                new UserFlightsEntity()
                        .setUserStatus(UserFlightsStatus.ARRIVED)
                        .setApplicationUsersEntity(pilot1)
        );
        userFlightsEntities.add(
                new UserFlightsEntity()
                        .setUserStatus(UserFlightsStatus.CREW_MEMBER_READY)
                        .setApplicationUsersEntity(pilot2)
        );
        userFlightsEntities.add(
                new UserFlightsEntity()
                        .setUserStatus(UserFlightsStatus.CREW_MEMBER_REGISTERED_FOR_FLIGHT)
                        .setApplicationUsersEntity(steward2)
        );
        userFlightsEntities.add(
                new UserFlightsEntity()
                        .setUserStatus(UserFlightsStatus.ARRIVED)
                        .setApplicationUsersEntity(client)
        );

        this.userFlightsEntityRepository.saveAll(userFlightsEntities);

        try {
            List<ApplicationUserResponseDto> applicationUserResponseDtoList =
                    this.applicationUserService.getAllFreeCrewMembers();
            Assertions.assertEquals(3, applicationUserResponseDtoList.size());

            Assertions.assertEquals("PILOT", applicationUserResponseDtoList.get(0).getPositionTitle());
            Assertions.assertEquals("STEWARD", applicationUserResponseDtoList.get(1).getPositionTitle());
            Assertions.assertEquals("CHIEF_STEWARD", applicationUserResponseDtoList.get(2).getPositionTitle());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetAllFreeEngineers_OK() {
        ApplicationUsersEntity engineer1 =
                this.createEngineersEntityByParameters("engineer_1", "engineer_1");
        engineer1 = this.applicationUsersEntityRepository.save(engineer1);

        ApplicationUsersEntity engineer2 =
                this.createEngineersEntityByParameters("engineer_2", "engineer_2");
        engineer2 = this.applicationUsersEntityRepository.save(engineer2);

        AircraftsEntity aircraft = new AircraftsEntity();
        aircraft
                .setTitle("test")
                .setAircraftType(AircraftType.PLANE)
                .setStatus(AircraftStatus.NEEDS_INSPECTION);
        aircraft = this.aircraftsEntityRepository.save(aircraft);

        engineer2.setServicedAircraft(aircraft);
        engineer2 = this.applicationUsersEntityRepository.save(engineer2);

        aircraft.setServicedBy(engineer2);
        aircraft = this.aircraftsEntityRepository.save(aircraft);

        try {
            List<ApplicationUserResponseDto> applicationUserResponseDtoList =
                    this.applicationUserService.getAllFreeEngineers();

            Assertions.assertEquals(1, applicationUserResponseDtoList.size());
            Assertions.assertEquals(engineer1.getUsername(), applicationUserResponseDtoList.get(0).getUsername());
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

    private ApplicationUsersEntity createEngineersEntityByParameters(
            String username,
            String fullName
    ) {
        ApplicationUsersEntity applicationUsersEntity = new ApplicationUsersEntity()
                .setUsername(username)
                .setFullName(fullName)
                .setPassword(this.passwordEncoder.encode("test"))
                .setUserPosition(new UserPositionsEntity().setId(6L).setPositionTitle("ENGINEER"));

        applicationUsersEntity.getUserRolesEntityList().add(new UserRolesEntity().setId(7L).setRoleTitle("ENGINEER"));

        return applicationUsersEntity;
    }
}