package com.pwa.weatherapp;

import com.pwa.weatherapp.DTO.WeatherForecastOutputDTO;
import com.pwa.weatherapp.DTO.WeatherResponseDTO;
import com.pwa.weatherapp.Service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(weatherService, "apiUrl", "http://api.openweathermap.org/data/2.5/forecast");
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(weatherService, "urlParams", "%s?q=%s&appid=%s&units=metric");
    }



    @Test
    void getCityWeather_APIError() {
        
        when(restTemplate.getForEntity(anyString(), any())).thenThrow(new RuntimeException("API Error"));

   
        WeatherForecastOutputDTO result = weatherService.getCityWeather("ErrorCity");

      
        assertNotNull(result);
        assertEquals("Failed", result.getStatus());
        assertTrue(result.getMessage().startsWith("An error occurred while fetching weather data"));
    }

    @Test
    void getCityWeather_NullResponse() {
       
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        
        WeatherForecastOutputDTO result = weatherService.getCityWeather("NullCity");

       
        assertNotNull(result);
        assertEquals("Failed", result.getStatus());
        assertEquals("Weather data unavailable for city: NullCity", result.getMessage());
    }

    private WeatherResponseDTO createMockWeatherResponse() {
        WeatherResponseDTO response = new WeatherResponseDTO();
        WeatherResponseDTO.City city = new WeatherResponseDTO.City();
        city.setName("TestCity");
        response.setCity(city);

        WeatherResponseDTO.WeatherData weatherData = new WeatherResponseDTO.WeatherData();
        weatherData.setDt_txt("2023-05-01 12:00:00");
        WeatherResponseDTO.Main mainData = new WeatherResponseDTO.Main();
        mainData.setTemp_max(25.0);
        mainData.setTemp_min(15.0);
        weatherData.setMain(mainData);
        WeatherResponseDTO.WeatherDescription weather = new WeatherResponseDTO.WeatherDescription();
        weather.setMain("Sunny");
        weatherData.setWeather(Arrays.asList(weather));
        WeatherResponseDTO.Wind wind = new WeatherResponseDTO.Wind();
        wind.setSpeed(5.0);
        weatherData.setWind(wind);

        response.setList(Arrays.asList(weatherData, weatherData, weatherData));
        return response;
    }
}
