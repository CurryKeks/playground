package com.currycookie;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Testing {


    public static void main(String[] args) throws Exception {
        String jsonString = getJsonString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        WeatherResponse weatherResponse = mapper.readValue(jsonString, WeatherResponse.class);

        System.out.println();
    }

    public static String getJsonString() {
        try {
            String apiKey = "267628afc7a62e4a3bd2f758463d5d0e";
            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> res = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?q=Oberhausen&appid=" + apiKey + "&lang=de&units=metric"))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            return res.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    public static class WeatherResponse {
        public List<Weather> weather;
        public Main main;
        public String name; // Stadt-Name
    }

    @Data
    public static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    @Data
    public static class Main {
        public double temp;
        public double feels_like;
        public int pressure;
        public int humidity;
    }

}