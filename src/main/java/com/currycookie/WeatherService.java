package com.currycookie;

import com.currycookie.data.DailySummary;
import com.currycookie.data.ForecastData;
import com.currycookie.data.WeatherData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.util.List;

@Service
public class WeatherService {

    private final String apiKey;

    private final ObjectMapper mapper;

    public WeatherService(@Value("${openweather.api.key}") String apiKey) {
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.apiKey = apiKey;
    }

//    public WeatherData getWeather() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        try {
//            return mapper.readValue(jsonString, WeatherData.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public WeatherData getCurrentWeather(String city) {
        try {
            JsonNode geoData = mapper.readTree(getCityCoordinates(city)).get(0);
            String lat = geoData.get("lat").asText();
            String lon = geoData.get("lon").asText();
            String response = sendRequest("data/2.5/weather?lat=" + lat + "&lon=" + lon).body().toString();
            return mapper.readValue(response, WeatherData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public ForecastData getForecast(String city) {
        try {
            JsonNode geoData = mapper.readTree(getCityCoordinates(city)).get(0);
            String lat = geoData.get("lat").asText();
            String lon = geoData.get("lon").asText();
            String response = sendRequest("data/2.5/forecast?lat=" + lat + "&lon=" + lon).body().toString();

            return mapper.readValue(response, ForecastData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCityCoordinates(String cityName) {
        return sendRequest("geo/1.0/direct?q=" + cityName).body().toString();
    }

    private HttpResponse sendRequest(String suffix) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            return client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://api.openweathermap.org/" + suffix + "&lang=de&units=metric" + "&appid=" + apiKey))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 0 für heute, 1 für morgen, 2 für übermorgen...
    public DailySummary getDailySummary(String city, int daysAhead) {
        ForecastData data = getForecast(city);

        // Determine zone (from API city.timezone if available, else Berlin fallback)
        ZoneId zone = ZoneId.of("Europe/Berlin");

        LocalDate targetDate = ZonedDateTime.now(zone).toLocalDate().plusDays(daysAhead);

        // Filter forecast entries for the target date
        List<ForecastData.Forecast> slices = data.getList().stream()
                .filter(f -> Instant.ofEpochSecond(f.getDt()).atZone(zone).toLocalDate().equals(targetDate))
                .toList();

        double min = slices.stream()
                .map(f -> f.getMain().getTemp_min())
                .min(Double::compare)
                .orElse(Double.NaN);

        double max = slices.stream()
                .map(f -> f.getMain().getTemp_max())
                .max(Double::compare).get();

        // most frequent condition of the day
        String condition = "unbekannt";
        long smallestDiff = Long.MAX_VALUE;

        for (ForecastData.Forecast forecast : slices) {
            LocalTime time = Instant.ofEpochSecond(forecast.getDt())
                    .atZone(zone)
                    .toLocalTime();

            long diff = Math.abs(Duration.between(time, LocalTime.NOON).toMinutes());

            if (diff < smallestDiff) {
                smallestDiff = diff;
                condition = forecast.getWeather().get(0).getDescription();
            }
        }

        DailySummary summary = new DailySummary();
        summary.setDate(targetDate);
        summary.setMinTemp(min);
        summary.setMaxTemp(max);
        summary.setDesription(condition);

        return summary;
    }
}