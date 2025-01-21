package io.github.atmotracker.markers;

import io.github.atmotracker.utility.Geolocation;

public class AirQualityMarker {
    public Geolocation location;
    public String name;
    public float pm10;
    public float pm25;
    public float so2;
    public float co;
    public float ozon;
    public float no2;
    public float benzen;


    public AirQualityMarker(Geolocation location, String name, float pm10, float pm25, float so2,float co,float ozon, float no2,float benzen ) {
        this.location = location;
        this.name = name;
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.so2 = so2;
        this.co=co;
        this.ozon=ozon;
        this.no2=no2;
        this.benzen=benzen;
    }
}
