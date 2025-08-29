package com.currycookie;

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
                System.out.println(">");
                String input = br.readLine();
                if (input == null) break;
                input = input.trim();
                if ("exit".equalsIgnoreCase(input)) {
                    running.set(false);
                    System.out.println("Exiting chatbot.");
                } else if (!input.isEmpty()) {
                    System.out.println("You said: " + input + ", the forecast is: " + weatherService.getDailySummary(input, 0));
                    System.out.println("You said: " + input + ", the forecast is: " + weatherService.getForecast(input));
                    System.out.println("You said: " + input + ", the weather is: " + weatherService.getCurrentWeather(input));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
