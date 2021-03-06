package com.example.npc.myweather2.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.gson.Weather;
import com.example.npc.myweather2.model.CountyList;
import com.example.npc.myweather2.util.MyUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateWeatherService extends Service {
    private List<CountyList> lists;
    private SharedPreferences preferences;
    public UpdateWeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        lists= DataSupport.findAll(CountyList.class);
        preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean updateMode=preferences.getBoolean("updateMode",true);
        if(lists!=null){
            for(CountyList list:lists){
                if (updateMode) {
                    if(list.isMainCity()){
                        //只更新默认城市天气
                        updateWeather(list.getWeatherId());
                        break;
                    }
                } else {
                    updateWeather(list.getWeatherId());
                }

            }
            //定时更新天气
          boolean autoUpdate=preferences.getBoolean("autoUpdate",false);
            AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
            Intent intent1=new Intent(getApplicationContext(),UpdateWeatherService.class);
            PendingIntent pi=PendingIntent.getService(getApplicationContext(),0,intent1,0);
            if(autoUpdate){
                String updateFre=preferences.getString("updateFre","3");
                long triggerAtTime= SystemClock.elapsedRealtime()+Integer.parseInt(updateFre)*60*60*1000;
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
            }else{
                alarmManager.cancel(pi);
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    //更新天气
    public void updateWeather(final String weatherId){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //夜间不更新
        if(preferences.getBoolean("nightUpdate",true)){
            if(calendar.get(Calendar.HOUR_OF_DAY)>22||calendar.get(Calendar.HOUR_OF_DAY)<5){
                return;
            }
        }
        String weatherAddress = getResources().getString(R.string.weatherAddress);
        String weatherKey =  getResources().getString(R.string.weatherKey);
        String weatherUrl = weatherAddress + weatherId + "&" + weatherKey;
        MyUtil.sendRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = MyUtil.handleWeatherResponse(responseText);
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                            editor.putString("weather" + weatherId, responseText);
                            editor.apply();
                        }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        if(preferences.getBoolean("autoUpdate",false)){
            Intent intent=new Intent(UpdateWeatherService.this,UpdateWeatherService.class);
            startService(intent);
        }else{
            super.onDestroy();
        }
    }
}
