package com.currycookie;

import com.currycookie.data.DailySummary;
import com.currycookie.data.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ConsoleChatbot {

    private final AtomicBoolean running = new AtomicBoolean(false);
    @Autowired
    WeatherService weatherService;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void startListeningToConsole() {
        running.set(true);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Chatbot is ready. Type 'exit' to quit.");
            while (running.get()) {
                System.out.println("Enter valid city name:");
                System.out.print("> ");
                String input = br.readLine();
                if (input == null) break;
                input = input.trim();
                if ("exit".equalsIgnoreCase(input)) {
                    running.set(false);
                    System.out.println("Exiting chatbot.");
                } else if (!input.isEmpty()) {
                    WeatherData current = weatherService.getCurrentWeather(input);
                    System.out.println("Current weather for " + input + ":");
                    System.out.println("  Description: " + current.getDescription());
                    System.out.println("  Temperature: " + current.getTemperature() + "°C");

                    DailySummary forecast = weatherService.getDailySummary(input, 1);
                    System.out.println("Forecast for tomorrow:");
                    System.out.println("  Description: " + forecast.getDesription());
                    System.out.println("  Min: " + forecast.getMinTemp() + "°C, Max: " + forecast.getMaxTemp() + "°C");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
