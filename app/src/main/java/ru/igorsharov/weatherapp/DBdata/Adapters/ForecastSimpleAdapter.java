package ru.igorsharov.weatherapp.DBdata.Adapters;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ru.igorsharov.weatherapp.DataHandler.DataWeatherHandler;
import ru.igorsharov.weatherapp.R;


public class ForecastSimpleAdapter extends SimpleCursorAdapter {

    private String[] from;

    public ForecastSimpleAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.from = from;
    }

    private String getCurrentColumn(Cursor cursor, String from) {
        return cursor.getString(cursor.getColumnIndexOrThrow(from));
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String[] mFrom = from;

        // инициализация view
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvTemperature = view.findViewById(R.id.tvTemperature);
        ImageView imgView = view.findViewById(R.id.imageView);

        // установка значений даты
        tvDate.setText(getCurrentColumn(cursor, mFrom[0]));

        // установка значений температуры
        String temperature = getCurrentColumn(cursor, mFrom[1]);
        if (temperature != null) {
            tvTemperature.setVisibility(View.VISIBLE);
            tvTemperature.setTextColor(DataWeatherHandler.colorOfTemp(context, temperature));
            tvTemperature.setText(DataWeatherHandler.addDegree(temperature));
        }

        // установка иконки погоды
        String idIconWeatherToday = getCurrentColumn(cursor, mFrom[3]);
        if (idIconWeatherToday != null) {
            String iconStr = DataWeatherHandler.getIconId(idIconWeatherToday);
            imgView.setVisibility(View.VISIBLE);
            setViewImage(imgView, iconStr);
        }
    }
}
