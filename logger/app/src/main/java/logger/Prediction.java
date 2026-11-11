package logger;
public record Prediction(
// as learnt in IN2009 records helps us build our data records and to do so the data types below match what the json returns exactly. wen use this for parsing in parser.java
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