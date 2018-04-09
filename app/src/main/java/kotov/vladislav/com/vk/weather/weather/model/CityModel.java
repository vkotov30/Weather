package kotov.vladislav.com.vk.weather.weather.model;

import java.util.ArrayList;
import java.util.List;

public class CityModel {
    public CoordModel coordModel;
    public List<WeatherModel> weather = new ArrayList<WeatherModel>();
    public String base;
    public Long id;
    public String name;
    public int cod;
    public MainWeatherInfo main;
    public SystemModel sys;
    public long dt;
}
