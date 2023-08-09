package kg.airport.airportproject.mapper;

import kg.airport.airportproject.dto.ClientFeedbackRequestDto;
import kg.airport.airportproject.dto.ClientFeedbackResponseDto;
import kg.airport.airportproject.entity.ClientFeedbacksEntity;

public class FeedbacksMapper {
    public static ClientFeedbacksEntity mapClientFeedbackRequestDtoToEntity(ClientFeedbackRequestDto source) {
        return new ClientFeedbacksEntity().setFeedbackText(source.getFeedbackText());
    }

    public static ClientFeedbackResponseDto mapToClientFeedbackResponseDto(ClientFeedbacksEntity source) {
        return new ClientFeedbackResponseDto()
                .setId(source.getId())
                .setClientId(source.getApplicationUsersEntity().getId())
                .setFlightId(source.getFlightsEntity().getId())
                .setFeedbackText(source.getFeedbackText())
                .setRegisteredAt(source.getRegisteredAt());
    }
}
