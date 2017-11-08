package ru.igorsharov.weatherapp.DBdata;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ru.igorsharov.weatherapp.DataWeatherHandler;
import ru.igorsharov.weatherapp.R;


public class ForecastSimpleAdapter extends SimpleCursorAdapter {

    private String[] from;

    public ForecastSimpleAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.from = from;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String[] mFrom = from;
        int index;

        TextView tvTime = view.findViewById(R.id.tvTime);
        index = cursor.getColumnIndexOrThrow(mFrom[0]);
        String time = cursor.getString(index);
        tvTime.setText(time);

        TextView tvTemperature = view.findViewById(R.id.tvTemperature);
        index = cursor.getColumnIndexOrThrow(mFrom[1]);
        String temperature = cursor.getString(index);

        if (temperature != null) {
            tvTemperature.setVisibility(View.VISIBLE);
            tvTemperature.setTextColor(DataWeatherHandler.colorOfTemp(context, temperature));
            tvTemperature.setText(DataWeatherHandler.weatherString(temperature));
        }

        ImageView imgView = view.findViewById(R.id.imageView);
        index = cursor.getColumnIndexOrThrow(mFrom[3]);
        String idIconWeatherToday = cursor.getString(index);

        if (idIconWeatherToday != null) {
            String iconStr = DataWeatherHandler.getIconId(idIconWeatherToday);
            imgView.setVisibility(View.VISIBLE);
            setViewImage(imgView, iconStr);
        }
    }
}
