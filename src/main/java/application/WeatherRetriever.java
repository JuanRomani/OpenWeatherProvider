package application;

import org.json.JSONException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class WeatherRetriever {

    private JsonParser jsonParser;
    private String actualOrForecast;
    private int index;

    public WeatherRetriever(String location, String actualOrForecast) {
        try {
            this.actualOrForecast = actualOrForecast;
            this.jsonParser = new JsonParser(location, actualOrForecast);
        } catch (Exception e) {
            System.out.println("\n! Location not found, please try again...");
        }
    }

    private String doubleFormatter(Double dou) {
        return String.format("%.1f", dou).replace(",", ".");
    }

    private String stringFormatter(String str) {
        try {
            return str.substring(0, 1).toUpperCase() + str.substring(1); // First letter to UpperCase
        } catch (StringIndexOutOfBoundsException e) {
            return str;
        }
    }

    private String location() {
        if (actualOrForecast.equals("actual")) {
            return jsonParser.getStringValue("name") + ", " + jsonParser.getStringValue("sys", "country");
        } else {
            return jsonParser.getStringValue("city", "name") + ", " + jsonParser.getStringValue("city", "country");
        }
    }

    private String timeStamp() {
        Integer unixTimeStamp;
        LocalDateTime dateTime;

        if (actualOrForecast.equals("actual")) {
            unixTimeStamp = jsonParser.getIntegerValue("dt");
        } else {
            unixTimeStamp = jsonParser.getIntegerValueFromArray("dt", index);
        }

        dateTime = Instant.ofEpochSecond(unixTimeStamp).atZone(ZoneId.systemDefault()).toLocalDateTime();

        return dateTime.format(DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm")) + "hs";
    }

    private String condition() {
        if (actualOrForecast.equals("actual")) {
            return stringFormatter(jsonParser.getStringFromArray("description"));
        } else {
            return stringFormatter(jsonParser.getStringFromNestedArray("weather", "description", index));
        }
    }

    private String temperature() {
        Integer temperature;

        if (actualOrForecast.equals("actual")) {
            temperature = Math.round(jsonParser.getIntegerValue("main", "temp"));
        } else {
            temperature = Math.round(jsonParser.getIntegerValueFromArray("main", "temp", index));
        }

        return temperature + " ºC | " + doubleFormatter((temperature * 1.8) + 32) + " ºF";
    }

    private String windSpeed() {
        Integer windSpeedMs;

        if (actualOrForecast.equals("actual")) {
            windSpeedMs = jsonParser.getIntegerValue("wind", "speed");
        } else {
            windSpeedMs = jsonParser.getIntegerValueFromArray("wind", "speed", index);
        }

        double windSpeedKmh = Math.round(windSpeedMs * 3.6);
        double windSpeedMph = windSpeedKmh * 2.23;

        if (windSpeedKmh == 0) {
            return "<1 kmh | <1 mph";
        } else {
            return Math.round(windSpeedKmh) + " kmh | " + doubleFormatter(windSpeedMph) + " mph"; //2.23 m/s to km/h
        }
    }

    private String windDirection() {
        if (actualOrForecast.equals("actual")) {
            return windDegreesToCardinal(jsonParser.getIntegerValue("wind", "deg"));
        } else {
            return windDegreesToCardinal(jsonParser.getIntegerValueFromArray("wind", "deg", index));
        }
    }

    private String windDegreesToCardinal(Integer degrees) {
        String cardinalDirection = "";

        if (degrees >= 0 && degrees <= 22 || degrees >= 337 && degrees <= 360) {
            cardinalDirection = "North";
        } else if (degrees >= 22 && degrees <= 67) {
            cardinalDirection = "Northeast";
        } else if (degrees >= 67 && degrees <= 112) {
            cardinalDirection = "East";
        } else if (degrees >= 112 && degrees <= 157) {
            cardinalDirection = "Southeast";
        } else if (degrees >= 157 && degrees <= 202) {
            cardinalDirection = "South";
        } else if (degrees >= 202 && degrees <= 247) {
            cardinalDirection = "Southwest";
        } else if (degrees >= 247 && degrees <= 292) {
            cardinalDirection = "West";
        } else if (degrees >= 292 && degrees <= 337) {
            cardinalDirection = "Northwest";
        }

        return cardinalDirection;
    }

    private String rain() {
        Integer rainAmountInMillimeters;

        try {
            if (actualOrForecast.equals("actual")) {
                rainAmountInMillimeters = jsonParser.getIntegerValue("rain", "3h");
            } else {
                rainAmountInMillimeters = jsonParser.getIntegerValueFromArray("rain", "3h", index);
            }

            if (rainAmountInMillimeters < 1) {
                return "<1mm";
            } else {
                return rainAmountInMillimeters + " mm";
            }
        } catch (JSONException e) {
            return "n/a";
        }
    }

    private String snow() {
        Integer snowAmountInMillimeters;

        try {
            if (actualOrForecast.equals("actual")) {
                snowAmountInMillimeters = jsonParser.getIntegerValue("snow", "3h");
            } else {
                snowAmountInMillimeters = jsonParser.getIntegerValueFromArray("snow", "3h", 0);
            }

            if (snowAmountInMillimeters < 1) {
                return "<1mm";
            } else {
                return snowAmountInMillimeters + " mm";
            }
        } catch (JSONException e) {
            return "n/a";
        }
    }

    private String cloudiness() {
        return jsonParser.getIntegerValue("clouds", "all") + "%";
    }

    private String atmosphericPressure() {
        Integer atmosphericPressure = Math.round(jsonParser.getIntegerValue("main", "pressure"));

        return atmosphericPressure + " hPa | "
                    + doubleFormatter(atmosphericPressure * 0.029) + " inHg";
    }

    private String humidity() {
        return jsonParser.getIntegerValue("main", "humidity") + "%";
    }

    private String visibility() {
        try {
            Integer visibilityInMeters = jsonParser.getIntegerValue("visibility");
            double visibilityInKilometers = visibilityInMeters / 1000;

            return Math.round(visibilityInKilometers) + " Kilometers | "
                        + doubleFormatter(visibilityInKilometers * 0.62) + " Miles";
        } catch (JSONException e) {
            return "Data not available";
        }
    }

    public String toString() {
        String output = "\nWeather for " + location() + "\n";

        if (actualOrForecast.equals("forecast")) {
            while (index <= 8) {
                output += "\nTime: " + timeStamp() + "\n"
                        + "Condition: " + condition() + "\n"
                        + "Temperature: " + temperature() + "\n"
                        + "Wind speed: " + windSpeed() + "\n"
                        + "Wind direction: " + windDirection() + "\n"
                        + "Precipitation: Rain - " + rain() + " | Snow - " + snow() + "\n";
                index++;
            }
        } else {
            output += "\nTime: " + timeStamp() + "\n"
                    + "Condition: " + condition() + "\n"
                    + "Temperature: " + temperature() + "\n"
                    + "Wind speed: " + windSpeed() + "\n"
                    + "Wind direction: " + windDirection() + "\n"
                    + "Precipitation: Rain - " + rain() + " | Snow - " + snow() + "\n"
                    + "Cloudiness: " + cloudiness() + "\n"
                    + "Atm. pressure: " + atmosphericPressure() + "\n"
                    + "Humidity: " + humidity() + "\n"
                    + "Visibility: " + visibility();
        }

        index = 0;
        return output;

        }

}
