package dev.anuradha.weatherservice.exception;

public class PincodeNotFoundException extends RuntimeException{
    public PincodeNotFoundException(String pincode){
        super("Pincode " + pincode + " is not supported by OpenWeather API.");
    }
}
