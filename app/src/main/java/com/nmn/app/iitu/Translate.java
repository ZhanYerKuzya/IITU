package com.nmn.app.iitu;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 30.03.2017.
 */

public class Translate {

    Map<String, String> weather = new HashMap<>();

    public Translate(){
        weather.put("01d","Чистое небо");
        weather.put("02d","Малооблачно");
        weather.put("03d","Рассеянные облака");
        weather.put("04d","Облачность с просветами");
        weather.put("09d","Дождь");
        weather.put("10d","Дождь");
        weather.put("11d","Гроза");
        weather.put("13d","Снег");
        weather.put("50d","Туман");
        weather.put("01n","Чистое небо");
        weather.put("02n","Малооблачно");
        weather.put("03n","Рассеянные облака");
        weather.put("04n","Облачность с просветами");
        weather.put("09n","Дождь");
        weather.put("10n","Дождь");
        weather.put("11n","Гроза");
        weather.put("13n","Снег");
        weather.put("50n","Туман");
    }


    public String getRUWeather(String key){
        return weather.get(key);
    }


}
