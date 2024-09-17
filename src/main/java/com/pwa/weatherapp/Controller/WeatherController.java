package com.pwa.weatherapp.Controller;

import ch.qos.logback.classic.Logger;
import com.pwa.weatherapp.DTO.WeatherForecastOutputDTO;
import com.pwa.weatherapp.Service.WeatherService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController

public class WeatherController {

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Autowired
    private WeatherService weatherService;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(WeatherService.class);


    @GetMapping("/{city}")
    public ResponseEntity<WeatherForecastOutputDTO> getWeather(@PathVariable String city) {
        String url = String.format("%s?q=%s&appid=%s&cnt=50&units=metric", apiUrl, city, apiKey);

        logger.info("Forecast weather API invoked");

        return ResponseEntity.ok(weatherService.getCityWeather(city));
    }
}
