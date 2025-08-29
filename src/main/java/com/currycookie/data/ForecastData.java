package com.currycookie.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class ForecastData {
    List<Forecast> list;

    @Data
    public static class Forecast {
        long dt;

        @JsonIgnore
        public ZonedDateTime getDateTime() {
            return Instant.ofEpochSecond(dt).atZone(ZoneId.of("Europe/Berlin"));
        }

        Main main;
        List<Weather> weather;
    }

    @Data
    public static class Main {
        double temp;
        double feels_like;
        double temp_min;
        double temp_max;
        int pressure;
        int sea_level;
        int grnd_level;
        int humidity;
    }

    @Data
    public static class Weather {
        int id;
        String main;
        String description;
        String icon;
    }
}
