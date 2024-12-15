package com.example.lab4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Создание базы данных и таблицы
        WeatherDatabaseHelper dbHelper = new WeatherDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Добавление данных в таблицу
        insertWeatherData(db, "Saint Petersburg", "5", "Clear sky", "2024-12-09 12:00:00");

        // Чтение данных из таблицы
        readWeatherData(db);
    }

    private void insertWeatherData(SQLiteDatabase db, String city, String temperature, String description, String timestamp) {
        ContentValues values = new ContentValues();
        values.put("city", city);
        values.put("temperature", temperature);
        values.put("description", description);
        values.put("timestamp", timestamp);

        long rowId = db.insert("weather", null, values);
        if (rowId != -1) {
            Log.d(TAG, "Данные успешно добавлены в таблицу: ID = " + rowId);
        } else {
            Log.e(TAG, "Ошибка добавления данных в таблицу");
        }
    }

    private void readWeatherData(SQLiteDatabase db) {
        Cursor cursor = db.query("weather", null, null, null, null, null, "timestamp DESC");

        if (cursor != null) {
            int cityIndex = cursor.getColumnIndex("city");
            int tempIndex = cursor.getColumnIndex("temperature");
            int descIndex = cursor.getColumnIndex("description");
            int timeIndex = cursor.getColumnIndex("timestamp");

            if (cityIndex != -1 && tempIndex != -1 && descIndex != -1 && timeIndex != -1) {
                while (cursor.moveToNext()) {
                    String city = cursor.getString(cityIndex);
                    String temp = cursor.getString(tempIndex);
                    String desc = cursor.getString(descIndex);
                    String time = cursor.getString(timeIndex);

                    Log.d(TAG, String.format("City: %s, Temp: %s, Desc: %s, Time: %s", city, temp, desc, time));
                }
            } else {
                Log.e(TAG, "Один или несколько столбцов отсутствуют в таблице");
            }
            cursor.close();
        } else {
            Log.e(TAG, "Курсор пуст, данные не получены");
        }
    }

    // Вспомогательный класс для работы с базой данных
    static class WeatherDatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "weather.db";
        private static final int DATABASE_VERSION = 1;

        public WeatherDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE weather (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "city TEXT, temperature TEXT, description TEXT, timestamp TEXT)");
            Log.d(TAG, "Таблица weather создана");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS weather");
            onCreate(db);
        }
    }
}
