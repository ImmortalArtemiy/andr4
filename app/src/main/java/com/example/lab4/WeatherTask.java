package com.example.lab4;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherTask extends AsyncTask<Void, Void, String> {
    private Context context;

    public WeatherTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            // Новый URL для получения данных о погоде
            URL url = new URL("https://api.weatherapi.com/v1/current.json?key=YOUR_API_KEY&q=59.9343,30.3351");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                JSONObject json = new JSONObject(result);
                JSONObject currentWeather = json.getJSONObject("current");
                String temperature = currentWeather.getString("temp_c");
                String description = currentWeather.getJSONObject("condition").getString("text");

                // Сохранение данных в базу
                WeatherDatabaseHelper dbHelper = new WeatherDatabaseHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("city", "Saint Petersburg");
                values.put("temperature", temperature);
                values.put("description", description);
                values.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

                db.insert("weather", null, values);
                db.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
