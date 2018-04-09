package kotov.vladislav.com.vk.weather.weather;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import kotov.vladislav.com.vk.weather.weather.model.CityModel;

public class WeatherDataLoader {

    private static final String OWM_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    private static final String KEY = "x-api-key";
    private static final String NEW_LINE = "\n";
    private static final int ALL_GOOD = 200;

    static CityModel getWeatherByCity(Context context, String city) {
        try {
            URL url = new URL(String.format(OWM_API, city));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty(KEY, context.getString(R.string.owm_app_id));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder rawData = new StringBuilder(1024);
            String tempVariable;
            while ((tempVariable = reader.readLine()) != null) {
                rawData.append(tempVariable).append(NEW_LINE);
            }
            reader.close();

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            CityModel model = gson.fromJson(rawData.toString(), CityModel.class);

            if (model.cod != ALL_GOOD) {
                return null;
            }
            return model;
        } catch (Exception e) {
            e.printStackTrace();
            return null; //FIXME Обработка ошибки
        }
    }
}
