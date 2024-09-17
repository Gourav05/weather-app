package com.pwa.weatherapp.Service;

import ch.qos.logback.classic.Logger;
import com.pwa.weatherapp.DTO.WeatherForecastOutputDTO;
import com.pwa.weatherapp.DTO.WeatherResponseDTO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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

    public WeatherForecastOutputDTO getCityWeather(String city) {
        String url = String.format(urlParams, apiUrl, city, apiKey);
        WeatherForecastOutputDTO outputDTO = new WeatherForecastOutputDTO();

        logger.info("Fetching weather for city: " + city + " from URL: " + url);

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
            return mapToOutputDTO(weatherResponse);
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

            dayForecast.setDate(dayData.getDt_txt().split(" ")[0]);
            dayForecast.setHighTemp(dayData.getMain().getTemp_max());
            dayForecast.setLowTemp(dayData.getMain().getTemp_min());
            dayForecast.setCondition(dayData.getWeather().get(0).getMain());

            // Generate warnings based on weather conditions
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
}
