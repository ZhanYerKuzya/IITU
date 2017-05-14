package com.nmn.app.iitu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.os.Handler;

import carbon.widget.ScrollView;


public class MainContentFragment extends Fragment{

    private View mainContentFragment;
    private TextView dateTextView, tempTextView, humidityTextView, windTextView, descTextView;
    private ImageView iconImageView;
    private ListView listView;
    private List<String> newsTitle = new ArrayList<String>();
    private List<String> newsShort = new ArrayList<String>();
    private List<String> newsLinks = new ArrayList<String>();
    private String currentLink = "";
    private ProgressDialog progressDialog;
    private Translate translate;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainContentFragment = inflater.inflate(R.layout.main_content, container, false);

        tempTextView = (TextView) mainContentFragment.findViewById(R.id.temp);
        humidityTextView = (TextView) mainContentFragment.findViewById(R.id.humidity);
        windTextView = (TextView) mainContentFragment.findViewById(R.id.wind);
        descTextView =(TextView) mainContentFragment.findViewById(R.id.desc);
        dateTextView = (TextView) mainContentFragment.findViewById(R.id.date);

        listView = (ListView) mainContentFragment.findViewById(R.id.newsList);

        iconImageView = (ImageView) mainContentFragment.findViewById(R.id.iconview);

        translate = new Translate();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Подождите пожалуйста...");
        progressDialog.show();

        new WeatherTask().execute();

        handler = new Handler();
        handler.post(runnableCode);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                currentLink = newsLinks.get(position);
                progressDialog.show();

                new FullNewsDL().execute();
            }
        });

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("EE");

        dateTextView.setText(dateFormat1.format(currentDate).toUpperCase()+ ", " + dateFormat.format(currentDate));

        return  mainContentFragment;
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {

            progressDialog.show();
            new WeatherTask().execute();

            new ParseDL().execute();

            handler.postDelayed(runnableCode, 3600000);
        }
    };

    //Выводим полностью новость
    private void alertDialog(String title, String fullNews){

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 20, 20);

        ScrollView scrollView = new ScrollView(getActivity());

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(20, 20, 5, 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(getActivity());
        textView.setText(title);
        textView.setTextSize(25);
        textView.setLayoutParams(params);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        TextView textView1 = new TextView(getActivity());
        textView1.setText(fullNews);
        textView1.setTextSize(20);
        textView1.setLayoutParams(params);

        layout.addView(textView);
        layout.addView(textView1);

        scrollView.addView(layout);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setView(scrollView);

        final AlertDialog alertDialog1 = alertDialog.create();

        alertDialog1.show();
        progressDialog.dismiss();

    }

    //Получаем данные из api.openweathermap.org и выводим на экран
    private class WeatherTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;
        BufferedReader reader = null;
        String resultJson = "";


        @Override
        protected String doInBackground(Void... params) {

            URL url = null;
            try {
                url = new URL("http://api.openweathermap.org/data/2.5/weather?q=Almaty%20ru&APPID=2be969dc73a8a863412039e8966fc9d9");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJson;

        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            JSONObject dataJsonObj = null;

            try {

                dataJsonObj = new JSONObject(strJson);

                JSONArray weather = dataJsonObj.getJSONArray("weather");

                String icon = "";

                for(int i = 0; i < weather.length(); i++){
                    JSONObject c = weather.getJSONObject(i);
                    icon = c.getString("icon");
                }

                JSONObject main = dataJsonObj.getJSONObject("main");

                int temp = main.getInt("temp");
                temp = (int) (temp - 272.15);

                double humidity = main.getDouble("humidity");

                JSONObject wind = dataJsonObj.getJSONObject("wind");
                double speed = wind.getDouble("speed");

                String iconURL = "http://openweathermap.org/img/w/" + icon + ".png";

                Glide.with(getActivity()).load(iconURL).centerCrop().into(iconImageView);

                tempTextView.setText(String.valueOf(temp) + " \u00B0C");
                descTextView.setText(translate.getRUWeather(icon));
                humidityTextView.setText("Влажность: " + String.valueOf(humidity));
                windTextView.setText("Скорость ветра: " + String.valueOf(speed) + " м/с");



            } catch (JSONException e) {
                Log.i("JSONEXCEPTION2", e.getMessage());
            }
        }
    }

    //Получаем зоголовок и короткое описание новости
    private class ParseDL extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Document doc = null;
            try {

                doc = Jsoup.connect("https://dl.iitu.kz/index.php")
                        .header("Accept-Encoding", "gzip, deflate")
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0)
                        .timeout(600000)
                        .get();

            } catch (IOException e) {

                e.printStackTrace();
            }

            Elements news_title = doc.select(".subject");
            Elements news_short = doc.select(".no-overflow");
            Elements link = doc.select(".commands");

            Log.i("SIZE", String.valueOf(news_title.size()));

            for(int i = 0; i < news_title.size()-3; i++) {

                    newsTitle.add(news_title.get(i).text());
                    newsShort.add(news_short.get(i).select("p").text());

                if(!link.get(i).select("a").attr("href").equals("")) {
                    newsLinks.add(link.get(i).select("a").attr("href"));
                    Log.i("Link", link.get(i).select("a").attr("href") + " " + String.valueOf(i));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            NewsAdapter adapter = new NewsAdapter(getActivity(), newsTitle, newsShort);
            listView.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }

    //Получаем новость полностью
    private class FullNewsDL extends AsyncTask<Void, Void, Void>{

        String title = "";
        String content = "";

        @Override
        protected Void doInBackground(Void... params) {
            Document doc = null;

            try {

                doc = Jsoup.connect(currentLink)
                        .header("Accept-Encoding", "gzip, deflate")
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0)
                        .timeout(600000)
                        .get();

            } catch (IOException e) {

                e.printStackTrace();
            }

            Elements news_title = doc.select(".subject");
            Elements full_news = doc.select(".no-overflow");

            if(full_news.size() > 0) {
                title = news_title.get(0).text();
                content = full_news.get(0).text();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog(title, content);
        }
    }
}
