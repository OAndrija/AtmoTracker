package io.github.atmotracker.markers;

import io.github.atmotracker.utility.Geolocation;

public class WeatherMarker {
    public Geolocation location;
    public String name;
    public float temperature;
    public float windSpeed;
    public float windGusts;
    public float precipitation;


    public WeatherMarker(Geolocation location, String name, float temperature, float windSpeed, float windGusts,float precipitation ) {
        this.location = location;
        this.name = name;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windGusts = windGusts;
        this.precipitation=precipitation;
    }
}
