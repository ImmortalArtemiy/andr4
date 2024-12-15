package com.example.lab4;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        WeatherDatabaseHelper dbHelper = new WeatherDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("weather", null, null, null, null, null, "timestamp DESC");
        StringBuilder history = new StringBuilder();

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

                    history.append(String.format("City: %s\nTemp: %s\nDesc: %s\nTime: %s\n\n", city, temp, desc, time));
                }
            } else {
                Log.e(TAG, "Один или несколько столбцов отсутствуют в таблице");
            }
            cursor.close();
        } else {
            Log.e(TAG, "Курсор пуст, данные не получены");
        }

        db.close();

        TextView historyTextView = findViewById(R.id.historyTextView);
        historyTextView.setText(history.toString());
    }
}
