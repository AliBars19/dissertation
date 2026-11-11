package logger;
public record Prediction(
// as learnt in IN2009 records helps us parse all of our collected data and to do so the data types below match what the json returns exactly.
    String vehicleId,
    String lineId,
    String naptanId,
    String stationName,
    String platformName,
    String direction,
    String destinationNaptanId,
    String destinationName,
    String towards,
    String currentLocation,
    String expectedArrival,
    String timestamp,
    Integer timeToStation
){}