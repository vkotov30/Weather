package kotov.vladislav.com.vk.weather.weather;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import kotov.vladislav.com.vk.weather.weather.model.CityModel;

public class MainActivity extends AppCompatActivity implements AddCityDialogListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String FONT_FILENAME = "fonts/weather.ttf";

    private final Handler handler = new Handler();

    private Typeface weatherFont;
    private TextView cityTextView;
    private TextView updatedTextView;
    private TextView detailsTextView;
    private TextView currentTemperatureTextView;
    private TextView weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        cityTextView = (TextView) findViewById(R.id.city_field);
        updatedTextView = (TextView) findViewById(R.id.updated_field);
        detailsTextView = (TextView) findViewById(R.id.details_field);
        currentTemperatureTextView = (TextView) findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getAssets(), FONT_FILENAME);
        weatherIcon.setTypeface(weatherFont);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_city) {
            showInputDialog();
            return true;
        }
        return false;
    }

    private void showInputDialog() {
        new AddCityDialog().show(getSupportFragmentManager(), "branch_filter_mode_dialog");
    }

    private void updateWeatherData(final String city) {
        new Thread() {
            public void run() {
                final CityModel model = WeatherDataLoader.getWeatherByCity(getApplicationContext(), city);
                if (model == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(model);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(CityModel model) {
        try {
            cityTextView.setText(model.name.toUpperCase(Locale.US) + ", " + model.sys.country);

            String description = "";
            long id = 0;

            if(model.weather.size() != 0){
                description = model.weather.get(0).description.toUpperCase(Locale.US);
                id =model.weather.get(0).id;
            }
            detailsTextView.setText(description + "\n" + "Humidity: "
                    + model.main.humidity + "%" + "\n" + "Pressure: " + model.main.pressure + " hPa");

            currentTemperatureTextView.setText(String.format("%.2f", model.main.tempBig) + " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(model.dt * 1000));
            updatedTextView.setText("Last update: " + updatedOn);

            setWeatherIcon(id, model.sys.sunrise * 1000,
                    model.sys.sunset * 1000);

        } catch (Exception e) {
            Log.d(LOG_TAG, "One or more fields not found in the JSON data");//FIXME Обработка ошибки
        }
    }

    private void setWeatherIcon(long actualId, long sunrise, long sunset) {
        long id = actualId / 100;
        String icon = "";
        RelativeLayout view = (RelativeLayout) findViewById(R.id.main_layout);
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getString(R.string.weather_sunny);
                view.setBackgroundResource(R.drawable.sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
                view.setBackgroundResource(R.drawable.clear_night);
            }
        } else {
            Log.d(LOG_TAG, "id " + id);
            switch ((int)id) {
                case 2:
                    icon = getString(R.string.weather_thunder);
                    view.setBackgroundResource(R.drawable.thunder);
                    break;
                case 3:
                    icon = getString(R.string.weather_drizzle);
                    view.setBackgroundResource(R.drawable.drizzle);
                    break;
                case 5:
                    icon = getString(R.string.weather_rainy);
                    view.setBackgroundResource(R.drawable.rainy);
                    break;
                case 6:
                    icon = getString(R.string.weather_snowy);
                    view.setBackgroundResource(R.drawable.snowy);
                    break;
                case 7:
                    icon = getString(R.string.weather_foggy);
                    view.setBackgroundResource(R.drawable.foggy);
                    break;
                case 8:
                    icon = getString(R.string.weather_cloudy);
                    view.setBackgroundResource(R.drawable.cloudy);
                    break;
                default:
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    @Override
    public void onChangeCity(String city) {
        updateWeatherData(city);
    }
}
