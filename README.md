
# Weather Forecast Microservice

## Overview

This microservice provides weather forecasts for a specified city and displays the high and low temperatures for the next 3 days. It also includes recommendations based on weather conditions such as temperature, rain, wind speed, and thunderstorms. The service is designed to be production-ready and can be accessed via a web browser or tools like Postman.

## Features

- Displays high and low temperatures for the next 3 days.
- Includes recommendations:
  - "Carry umbrella" if rain is predicted.
  - "Use sunscreen lotion" if temperature exceeds 40°C.
  - "It’s too windy, watch out!" if wind speed exceeds 10 mph.
  - "Don’t step out! A Storm is brewing!" if thunderstorms are predicted.
- Supports offline mode.
- Caches data to handle API unavailability.

## API Documentation

 Swagger Dashboard : {Base-url}swagger-ui/index.html

### Endpoints

#### Get Weather Forecast

- **URL**: `/weather-app/{city}r`
- **Method**: `GET`
- **Query Parameters**:
  - `city` (string): The city for which to get the weather forecast.
  - `days` (integer): Number of days to get the forecast for (default: 3).

- **Response**:
  ```json
 `{
    "status": "Success",
    "message": "Weather forecast fetched for: {cityName}",
    "cityName": "{cityName}",
    "forecast": [
        {
            "date": "{date}",
            "highTemp": {highTemp},
            "lowTemp": {lowTemp},
            "condition": "{condition}",
            "warnings": "{warnings}"
        }
       ]
   }`
