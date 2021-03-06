package com.example.npc.myweather2.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by npc on 3-14 0014.
 */

public class Weather {
    public String status;
    public Aqi aqi;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecasts;
    @SerializedName("hourly_forecast")
    public List<HourlyForecast> hourlyForecasts;
    public Now now;
    public Suggestion suggestion;
}
