package com.currycookie.data;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class WeatherData {
    @Getter
    private List<Weather> weather;
    private Main main;
    private String name;

    @Data
    private static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    @Data
    private static class Main {
        public double temp;
        public double feels_like;
        public int pressure;
        public int humidity;
    }

    public String getCity() {
        return name;
    }

    public String getDescription() {
        return weather.get(0).description;
    }

    public String getIconUrl() {
        return "http://openweathermap.org/img/wn/" + weather.get(0).icon + "@2x.png";
    }

    public double getTemperature() {
        return main.temp;
    }

    public double getFeelsLike() {
        return main.feels_like;
    }

    public int getPressure() {
        return main.pressure;
    }

    public int getHumidity() {
        return main.humidity;
    }
}
