package kotov.vladislav.com.vk.weather.weather.model;

import com.google.gson.annotations.SerializedName;

public class MainWeatherInfo {

    @SerializedName("temp")
    public Double tempBig;

    public long pressure;
    public long humidity;
    public long temp_min;
    public long temp_max;

}
