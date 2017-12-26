import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.util.Scanner;

public class JsonParser {

    private String address;
    private URL url;
    private Scanner scanner;
    private JSONObject jsonObject;

    public JsonParser(String location, String type) throws Exception {
        if (type.equals("actual")) {
            type = "weather";
        }

        this.address = "http://api.openweathermap.org/data/2.5/" + type + "?q=" + location +
                    "&units=metric&appid=80fc7e80b77747ecf59857c3c0562263"; // Free API Key

        this.url = new URL(address);
        this.scanner = new Scanner(url.openStream());

        String rawData = "";
        while (scanner.hasNext()){
            rawData += scanner.nextLine();
        }
        scanner.close();

        // Build JSON Object
        jsonObject = new JSONObject(rawData);
    }

    public String getAddress() {
        return address;
    }

    public String getJsonObject() {
        return jsonObject.toString(3);
    }

    public boolean isInteger(Object object) {
        return object.getClass().getName().equals("java.lang.Integer");
    }

    public Integer getIntegerValue(String key, String value) {
        //Retrieve JSON Value as Object, then cast that value to Integer
        Object valueRetrieved = jsonObject.getJSONObject(key).get(value);

        if(isInteger(valueRetrieved)){
            return (Integer) valueRetrieved;
        } else {
            return ((Double) valueRetrieved).intValue();
        }
    }

    public Integer getIntegerValue(String key) {
        Object valueRetrieved = jsonObject.get(key);

        if(isInteger(valueRetrieved)){
            return (Integer) valueRetrieved;
        } else {
            return ((Double) valueRetrieved).intValue();
        }
    }

    public Integer getIntegerValueFromArray(String key, String value, int index) {
        JSONArray arr = jsonObject.getJSONArray("list");
        Object valueRetrieved = arr.getJSONObject(index).getJSONObject(key).get(value);

        if(isInteger(valueRetrieved)){
            return (Integer) valueRetrieved;
        } else {
            return ((Double) valueRetrieved).intValue();
        }

        //index is global list index
    }

    public Integer getIntegerValueFromArray(String value, int index) {
        JSONArray arr = jsonObject.getJSONArray("list");
        Object valueRetrieved = arr.getJSONObject(index).get(value);

        if(isInteger(valueRetrieved)){
            return (Integer) valueRetrieved;
        } else {
            return ((Double) valueRetrieved).intValue();
        }
    }

    public String getStringValue(String key) {
        return jsonObject.getString(key);
    }

    public String getStringValue(String key, String value) {
        return jsonObject.getJSONObject(key).getString(value);
    }

    public String getStringFromArray(String value) {
        JSONArray arr = jsonObject.getJSONArray("weather");

        return arr.getJSONObject(0).getString(value);

        //Key will always be "weather" on this program, due API response structure
        //Use getJsonObject() to print JSON response
    }

    public String getStringFromNestedArray(String key, String value, int index) {
        JSONArray arr = jsonObject.getJSONArray("list");
        JSONArray asd = arr.getJSONObject(index).getJSONArray(key);

        return asd.getJSONObject(0).get(value).toString();

        //int index is global list index
        //index 0 = unique index from nested array
        //Key will always be "list" on this program, due API response structure
        //Use getJsonObject() to print JSON response
    }

}
