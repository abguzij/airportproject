package kg.airport.airportproject.exception;

public class TicketsForFlightNotOnSaleException extends Exception {
    public TicketsForFlightNotOnSaleException(String message) {
        super(message);
    }
}
