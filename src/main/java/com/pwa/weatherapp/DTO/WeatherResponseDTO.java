package com.pwa.weatherapp.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponseDTO {
    private String cod;
    private int message;
    private int cnt;
    private List<WeatherData> list;
    private City city;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherData {
        private long dt;
        private Main main;
        private List<WeatherDescription> weather;
        private Clouds clouds;
        private Wind wind;
        private int visibility;
        private double pop;
        private Sys sys;
        private String dt_txt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Main {
        private double temp;
        private double feels_like;
        private double temp_min;
        private double temp_max;
        private int pressure;
        private int sea_level;
        private int grnd_level;
        private int humidity;
        private double temp_kf;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherDescription {
        private int id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Clouds {
        private int all;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Wind {
        private double speed;
        private int deg;
        private double gust;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sys {
        private String pod;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class City {
        private int id;
        private String name;
        private Coordinates coord;
        private String country;
        private int population;
        private int timezone;
        private long sunrise;
        private long sunset;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordinates {
        private double lat;
        private double lon;
    }
}