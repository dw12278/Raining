package com.example.npc.myweather2.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.npc.myweather2.gson.Weather;
import com.example.npc.myweather2.model.City;
import com.example.npc.myweather2.model.County;
import com.example.npc.myweather2.model.Province;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by npc on 3-14 0014.
 */

public class MyUtil {
    private static Toast toast;
    private static final String TAG = "tagMyUtil";
    public static void showToast(Context context,String message){
        if(toast==null){
            toast=Toast.makeText(context,message,Toast.LENGTH_SHORT);
        }else{
            toast.setText(message);
        }
        if(!"".equals(message))
            toast.show();
    }
    public static void showToast(String message){
        if(toast==null){
            toast=Toast.makeText(MyApplication.getContext(),message,Toast.LENGTH_SHORT);
        }else{
            toast.setText(message);
        }
        if(!"".equals(message))
            toast.show();
    }
    public static void sendRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray provinces=new JSONArray(response);
                for(int i=0;i<provinces.length();i++){
                    JSONObject provinceObject=provinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    try {
                        province.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        return false;
    }
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray cityList=new JSONArray(response);
                for(int i=0;i<cityList.length();i++){
                    JSONObject cityObject=cityList.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    try {
                        city.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                return true;
            }catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray countyList=new JSONArray(response);
                for(int i=0;i<countyList.length();i++){
                    JSONObject countyObject=countyList.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    try {
                        county.save();
                    } catch (Exception e) {
                        continue;
                    }
                }
                return true;
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather5");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String myMD5(String mess) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(md5.digest(mess.getBytes()));
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMD5(String mess) {
        if (TextUtils.isEmpty(mess)) {
            return null;
        } else {
            String result = myMD5(mess);
            for (int i = 0; i < 1; i++) {
                result = myMD5(result);
            }
            return result;
        }
    }
//    //获取连接网络类型
//    public boolean getNetworkType(){
//        ConnectivityManager manager=(ConnectivityManager)MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info=manager.getActiveNetworkInfo();
//        if(info!=null){
//            if(info.getType()==ConnectivityManager.TYPE_WIFI){
//                return true;
//            }
//        }
//        return false;
//    }

}
