package kg.airport.airportproject.controller.v1;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import kg.airport.airportproject.configuration.SecurityConfigurationTest;
import kg.airport.airportproject.date.RegistrationDateTestFiltersProvider;
import kg.airport.airportproject.dto.*;
import kg.airport.airportproject.entity.AircraftsTestEntityProvider;
import kg.airport.airportproject.entity.PartsEntity;
import kg.airport.airportproject.entity.PartsTestEntityProvider;
import kg.airport.airportproject.entity.QPartsEntity;
import kg.airport.airportproject.entity.attributes.PartType;
import kg.airport.airportproject.repository.PartsEntityRepository;
import kg.airport.airportproject.response.ErrorResponse;
import kg.airport.airportproject.security.JwtTokenAuthenticationFactory;
import kg.airport.airportproject.service.PartsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
@ContextConfiguration(classes = SecurityConfigurationTest.class)
@TestPropertySource(value = "classpath:test.properties")
class PartsControllerTest {
    @MockBean
    private PartsEntityRepository partsEntityRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JwtTokenAuthenticationFactory jwtTokenAuthenticationFactory;
    @Autowired
    private PartsService partsService;

    @LocalServerPort
    private int port;

    @Test
    void testRegisterNewPart_OK() {
        try {
            PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
            PartsEntity partsEntity = PartsTestEntityProvider.getTestPartsEntity();

            Mockito
                    .when(this.partsEntityRepository.save(Mockito.eq(partsEntity)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/register");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<PartResponseDto> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDto, httpHeaders),
                            PartResponseDto.class
                    );

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(requestDto.getTitle(), response.getBody().getTitle());
            Assertions.assertEquals(requestDto.getAircraftType(), response.getBody().getAircraftType());
            Assertions.assertEquals(requestDto.getPartType(), response.getBody().getPartType());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testRegisterNewPart_InvalidAircraftType() {
        try {
            PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
            requestDto.setAircraftType(null);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/register");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDto, httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Тип создаваемого самолета не может быть null!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testRegisterNewPart_InvalidPartType() {
        try {
            PartRequestDto requestDto = PartsTestDtoProvider.getTestPartRequestDto();
            requestDto.setPartType(null);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/register");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDto, httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Тип создаваемой детали не может быть null!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testRegisterNewParts_OK() {
        try {
            List<PartRequestDto> requestDtoList = PartsTestDtoProvider.getListOfTestPartRequestDto();
            List<PartsEntity> partsEntities = PartsTestEntityProvider.getListOfTestPartsEntities();

            Mockito
                    .when(this.partsEntityRepository.saveAll(Mockito.eq(partsEntities)))
                    .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/register-all");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<List<PartResponseDto>> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDtoList, httpHeaders),
                            new ParameterizedTypeReference<List<PartResponseDto>>() {}
                    );

            Assertions.assertEquals(requestDtoList.size(), response.getBody().size());
            for (int i = 0; i < response.getBody().size(); i++) {
                Assertions.assertEquals(requestDtoList.get(i).getPartType(), response.getBody().get(i).getPartType());
                Assertions.assertEquals(requestDtoList.get(i).getTitle(), response.getBody().get(i).getTitle());
                Assertions.assertEquals(
                        requestDtoList.get(i).getAircraftType(),
                        response.getBody().get(i).getAircraftType()
                );
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testRegisterNewParts_InvalidAircraftType() {
        try {
            List<PartRequestDto> requestDtoList = PartsTestDtoProvider.getListOfTestPartRequestDto();
            requestDtoList.get(1).setAircraftType(null);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/register-all");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDtoList, httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Тип создаваемого самолета не может быть null!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testRegisterNewParts_InvalidPartType() {
        try {
            List<PartRequestDto> requestDtoList = PartsTestDtoProvider.getListOfTestPartRequestDto();
            requestDtoList.get(1).setPartType(null);

            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/register-all");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.POST,
                            new HttpEntity<>(requestDtoList, httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Тип создаваемой детали не может быть null!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testGetAllParts_OK() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            BooleanBuilder booleanBuilder = new BooleanBuilder();
            QPartsEntity root = QPartsEntity.partsEntity;
            booleanBuilder.and(root.aircraftType.eq(AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE));
            booleanBuilder.and(root.partType.eq(PartsTestEntityProvider.TEST_PART_TYPE));
            booleanBuilder.and(root.aircraftsEntities.any().id.eq(AircraftsTestEntityProvider.TEST_AIRCRAFT_ID));
            booleanBuilder.and(root.id.eq(PartsTestEntityProvider.TEST_PART_ID));
            booleanBuilder.and(root.registeredAt.goe(RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER));
            booleanBuilder.and(root.registeredAt.loe(RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER));

            PartsEntity foundPartsEntity = PartsTestEntityProvider.getTestPartsEntity();
            foundPartsEntity.setRegisteredAt(RegistrationDateTestFiltersProvider.TEST_REGISTRATION_DATE);
            Mockito
                    .when(this.partsEntityRepository.findAll(Mockito.eq(booleanBuilder.getValue())))
                    .thenAnswer(invocationOnMock -> List.of(foundPartsEntity));

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/all");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE)
                    .queryParam("partType", PartsTestEntityProvider.TEST_PART_TYPE)
                    .queryParam("aircraftId", AircraftsTestEntityProvider.TEST_AIRCRAFT_ID)
                    .queryParam("partId", PartsTestEntityProvider.TEST_PART_ID)
                    .queryParam("registeredBefore", RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER)
                    .queryParam("registeredAfter", RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<List<PartResponseDto>> response =
                    this.testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            new ParameterizedTypeReference<List<PartResponseDto>>() {}
                    );

            Assertions.assertEquals(1, response.getBody().size());
            Assertions.assertEquals(foundPartsEntity.getTitle(), response.getBody().get(0).getTitle());
            Assertions.assertEquals(foundPartsEntity.getPartType(), response.getBody().get(0).getPartType());
            Assertions.assertEquals(foundPartsEntity.getAircraftType(), response.getBody().get(0).getAircraftType());
            Assertions.assertEquals(foundPartsEntity.getId(), response.getBody().get(0).getId());
            Assertions.assertEquals(foundPartsEntity.getRegisteredAt(), response.getBody().get(0).getRegisteredAt());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testGetAllParts_PartsNotFound() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            Mockito
                    .when(this.partsEntityRepository.findAll(Mockito.any(Predicate.class)))
                    .thenAnswer(invocationOnMock -> new ArrayList<>());

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/all");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE)
                    .queryParam("partType", PartsTestEntityProvider.TEST_PART_TYPE)
                    .queryParam("aircraftId", AircraftsTestEntityProvider.TEST_AIRCRAFT_ID)
                    .queryParam("partId", PartsTestEntityProvider.TEST_PART_ID)
                    .queryParam("registeredBefore", RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER)
                    .queryParam("registeredAfter", RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "Деталей по заданным параметрам не найдено!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testGetAllParts_InvalidPartId() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/all");
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParam("aircraftType", AircraftsTestEntityProvider.TEST_AIRCRAFT_TYPE)
                    .queryParam("partType", PartsTestEntityProvider.TEST_PART_TYPE)
                    .queryParam("aircraftId", AircraftsTestEntityProvider.TEST_AIRCRAFT_ID)
                    .queryParam("partId", 0L)
                    .queryParam("registeredBefore", RegistrationDateTestFiltersProvider.TEST_END_DATE_FILTER)
                    .queryParam("registeredAfter", RegistrationDateTestFiltersProvider.TEST_START_DATE_FILTER);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<ErrorResponse> response =
                    this.testRestTemplate.exchange(
                            uriComponentsBuilder.build().encode().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            ErrorResponse.class
                    );

            Assertions.assertEquals(
                    "ID детали не может быть меньше 1!",
                    response.getBody().getMessage()
            );
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void testGetPartTypes_OK() {
        try {
            String jwtToken = this.jwtTokenAuthenticationFactory.getJwtTokenForDefaultUserWithSpecifiedRoleTitle(
                    "DISPATCHER"
            );

            URI uri = new URI( "http://localhost:" + port + "/v1/parts/part-types");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", jwtToken);

            ResponseEntity<PartTypesResponseDto> response =
                    this.testRestTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            new HttpEntity<>(httpHeaders),
                            PartTypesResponseDto.class
                    );

            Assertions.assertEquals(List.of(PartType.values()), response.getBody().getPartTypeList());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}