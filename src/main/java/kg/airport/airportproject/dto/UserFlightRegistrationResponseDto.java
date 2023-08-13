package kg.airport.airportproject.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import kg.airport.airportproject.entity.UserPositionsEntity;
import kg.airport.airportproject.entity.attributes.UserFlightsStatus;

import java.time.LocalDateTime;

public class UserFlightRegistrationResponseDto {
    private Long id;
    private UserFlightsStatus userStatus;
    private LocalDateTime registeredAt;
    private Long employeeId;
    private String employeeFullName;
    private String employeePositionTitle;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer seatsRowNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer seatNumberInRow;
    private Long flightId;
    private String flightDestination;

    public UserFlightRegistrationResponseDto() {
        this.seatsRowNumber = null;
        this.seatNumberInRow = null;
    }

    public Long getId() {
        return id;
    }

    public UserFlightRegistrationResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public UserFlightsStatus getUserStatus() {
        return userStatus;
    }

    public UserFlightRegistrationResponseDto setUserStatus(UserFlightsStatus userStatus) {
        this.userStatus = userStatus;
        return this;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public UserFlightRegistrationResponseDto setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public UserFlightRegistrationResponseDto setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public UserFlightRegistrationResponseDto setEmployeeFullName(String employeeFullName) {
        this.employeeFullName = employeeFullName;
        return this;
    }

    public String getEmployeePositionTitle() {
        return employeePositionTitle;
    }

    public UserFlightRegistrationResponseDto setEmployeePositionTitle(String employeePositionTitle) {
        this.employeePositionTitle = employeePositionTitle;
        return this;
    }

    public Long getFlightId() {
        return flightId;
    }

    public UserFlightRegistrationResponseDto setFlightId(Long flightId) {
        this.flightId = flightId;
        return this;
    }

    public String getFlightDestination() {
        return flightDestination;
    }

    public UserFlightRegistrationResponseDto setFlightDestination(String flightDestination) {
        this.flightDestination = flightDestination;
        return this;
    }

    public Integer getSeatsRowNumber() {
        return seatsRowNumber;
    }

    public UserFlightRegistrationResponseDto setSeatsRowNumber(Integer seatsRowNumber) {
        this.seatsRowNumber = seatsRowNumber;
        return this;
    }

    public Integer getSeatNumberInRow() {
        return seatNumberInRow;
    }

    public UserFlightRegistrationResponseDto setSeatNumberInRow(Integer seatNumberInRow) {
        this.seatNumberInRow = seatNumberInRow;
        return this;
    }
}
