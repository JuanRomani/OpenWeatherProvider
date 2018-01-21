package UI;

import application.WeatherRetriever;
import com.neovisionaries.i18n.CountryCode;
import java.util.Scanner;

public class UI {

    private Scanner reader;

    public UI() {
        this.reader = new Scanner(System.in);
    }

    public void run() throws Exception {
        while (true) {
            String location;
            String cityName;
            String countryName;
            String actualOrForecast;
            WeatherRetriever weatherRetriever;

            System.out.print("\n- Welcome to OpenWeatherMap weather-retriever!" +
                    "\n\n- Please, type the city name to show the weather, or type \"quit\" to exit: ");
            cityName = userInputFormatter(reader.nextLine().toLowerCase());

            if (cityName.equals("Quit")) {
                System.out.println("\n☺ Good bye!");
                break;
            } else if (cityName.equals("")) {
                continue;
            }

            while (true) {
                System.out.print("\n- Which country?: ");
                countryName = userInputFormatter(reader.nextLine().toLowerCase());

                if (searchCountryCode(countryName) == null) {
                    System.out.println("\n! Country not found please, try again");
                } else {
                    break;
                }
            }

            location = cityName + "," + searchCountryCode(countryName);

            while (true) {
                System.out.print("\n- Please, type \"actual\" to print the actual weather, or \"forecast\" to print " +
                        "the forecasted weather for the next 24 hours: ");
                actualOrForecast = reader.nextLine().toLowerCase();

                if (actualOrForecast.equals("actual") || actualOrForecast.equals("forecast")){
                    break;
                } else {
                    System.out.println("\n! Error, please try again...");
                }
            }

            try {
                weatherRetriever = new WeatherRetriever(location, actualOrForecast);
                System.out.println(weatherRetriever.toString());
            } catch (NullPointerException x) {
                continue;
            }
        }
    }

    public String searchCountryCode(String countryName) {
        if (CountryCode.findByName(countryName).size() > 0) { // Check if country is contained in the CountryCode list
            return CountryCode.findByName(countryName).get(0).getAlpha2(); // Gets the ISO 3166-1 Alpha-2 code
        }

        return null;
    }

    public String userInputFormatter(String str) {
        try {
            str = str.substring(0, 1).toUpperCase() + str.substring(1); // First letter to UpperCase
        } catch (StringIndexOutOfBoundsException e) {
            return str;
        }

        String toCaps = "";
        for (int i = 0; i < str.length(); i++) { //To capitalize every word from String

            if (("" + str.charAt(i)).equals(" ")) {
                toCaps += (" " + str.charAt(i+1)).toUpperCase();
                i++;
            } else {
                toCaps += str.charAt(i);
            }
        }

        return toCaps;
    }

}
