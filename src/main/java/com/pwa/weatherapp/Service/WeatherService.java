package com.pwa.weatherapp.Service;


import ch.qos.logback.classic.Logger;
import com.pwa.weatherapp.DTO.WeatherForecastOutputDTO;
import com.pwa.weatherapp.DTO.WeatherResponseDTO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeatherService {

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.params}")
    private String urlParams;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(WeatherService.class);

    // Warning message constants
    private static final String WARN_SUNSCREEN = "Use sunscreen lotion. ";
    private static final String WARN_UMBRELLA = "Carry umbrella. ";
    private static final String WARN_WINDY = "It’s too windy, watch out! ";
    private static final String WARN_THUNDERSTORM = "Don’t step out! A Storm is brewing! ";

    // In-memory cache
    private final Map<String, CachedWeatherData> cache = new ConcurrentHashMap<>();

    @Cacheable(value = "weather", key = "#city")
    public WeatherForecastOutputDTO getCityWeather(String city) {
        String url = String.format(urlParams, apiUrl, city, apiKey);
        WeatherForecastOutputDTO outputDTO = new WeatherForecastOutputDTO();

        logger.info("Fetching weather for city: " + city + " from URL: " + url);

        // Check cache
        CachedWeatherData cachedData = cache.get(city);
        if (cachedData != null && cachedData.getTimestamp().isAfter(LocalDateTime.now().minusHours(1))) {
            logger.info("Returning cached weather data for city: " + city);
            return cachedData.getData();
        }

        try {
            ResponseEntity<WeatherResponseDTO> response = restTemplate.getForEntity(url, WeatherResponseDTO.class);
            WeatherResponseDTO weatherResponse = response.getBody();

            if (weatherResponse == null) {
                logger.warn("Received null response from weather API for city: " + city);
                outputDTO.setStatus("Failed");
                outputDTO.setMessage("Weather data unavailable for city: " + city);
                return outputDTO;
            }

            logger.info("Weather response fetched successfully for city: " + city);
            outputDTO = mapToOutputDTO(weatherResponse);

            // Cache the data
            cache.put(city, new CachedWeatherData(outputDTO, LocalDateTime.now()));

            return outputDTO;
        } catch (HttpClientErrorException.NotFound e) {
            outputDTO.setStatus("Failed");
            outputDTO.setMessage("City not found: " + city);
            logger.warn("City not found: " + city);
            return outputDTO;
        } catch (Exception e) {
            outputDTO.setStatus("Failed");
            outputDTO.setMessage("An error occurred while fetching weather data: " + e.getMessage());
            logger.error("Error fetching weather data for city: " + city, e);
            return outputDTO;
        }
    }

    private WeatherForecastOutputDTO mapToOutputDTO(WeatherResponseDTO weatherResponse) {
        WeatherForecastOutputDTO outputDTO = new WeatherForecastOutputDTO();
        List<WeatherForecastOutputDTO.DayForecast> forecastList = new ArrayList<>();

        if (weatherResponse.getCity() == null) {
            outputDTO.setStatus("Failed");
            outputDTO.setMessage("Invalid response from weather service");
            logger.warn("Invalid weather service response");
            return outputDTO;
        }

        outputDTO.setStatus("Success");
        outputDTO.setMessage("Weather forecast fetched for: " + weatherResponse.getCity().getName());
        outputDTO.setCityName(weatherResponse.getCity().getName());

        logger.info("Weather forecast fetched for: " + weatherResponse.getCity().getName());

        // Extract forecast for the next 3 days (taking data at every 8th interval, assuming 3-hour intervals)
        for (int i = 0; i < 3; i++) {
            WeatherResponseDTO.WeatherData dayData = weatherResponse.getList().get(i * 8);
            WeatherForecastOutputDTO.DayForecast dayForecast = new WeatherForecastOutputDTO.DayForecast();
            dayForecast.setDate(dayData.getDt_txt());
            dayForecast.setHighTemp(dayData.getMain().getTemp_max());
            dayForecast.setLowTemp(dayData.getMain().getTemp_min());
            dayForecast.setCondition(dayData.getWeather().get(0).getDescription());
            dayForecast.setIcon(dayData.getWeather().get(0).getIcon());



            StringBuilder warnings = new StringBuilder();
            if (dayData.getMain().getTemp_max() > 40) {
                warnings.append(WARN_SUNSCREEN);
            }
            if ("Rain".equalsIgnoreCase(dayForecast.getCondition())) {
                warnings.append(WARN_UMBRELLA);
            }
            if (dayData.getWind().getSpeed() > 10) {
                warnings.append(WARN_WINDY);
            }
            if ("Thunderstorm".equalsIgnoreCase(dayForecast.getCondition())) {
                warnings.append(WARN_THUNDERSTORM);
            }
            dayForecast.setWarnings(warnings.toString().trim());

            forecastList.add(dayForecast);
        }

        outputDTO.setForecast(forecastList);
        return outputDTO;
    }

    private static class CachedWeatherData {
        private final WeatherForecastOutputDTO data;
        private final LocalDateTime timestamp;

        public CachedWeatherData(WeatherForecastOutputDTO data, LocalDateTime timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }

        public WeatherForecastOutputDTO getData() {
            return data;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
