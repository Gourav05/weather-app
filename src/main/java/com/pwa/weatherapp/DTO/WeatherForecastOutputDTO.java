package com.pwa.weatherapp.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastOutputDTO {


    private String status;
    private String message;
    private String cityName;
    private List<DayForecast> forecast;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayForecast {
        private String date;
        private double highTemp;
        private double lowTemp;
        private String condition;
        private String warnings;
        private String icon;
    }
}

