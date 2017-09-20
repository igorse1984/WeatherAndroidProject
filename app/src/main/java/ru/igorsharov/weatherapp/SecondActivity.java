package ru.igorsharov.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {


    public final static String RECEIVE_WEATHER_INFO = "receive_weather_info";

    TextView textView;
    String city;
    int weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        final Intent intent = getIntent();
        if (intent != null) {
            textView = (TextView) findViewById(R.id.textView);
            city = intent.getStringExtra(RECEIVE_WEATHER_INFO);
//            weather = WeatherSelector.getTemperature(city);
            textView.setText(weather);
        }

        Button button = (Button) findViewById(R.id.button2);

        // передача неявного интента
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                intent1.putExtra(Intent.EXTRA_TEXT, "Погода в городе " + city + ": " + weather);
                if (intent1.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent1);
                }
            }
        });
    }
}
