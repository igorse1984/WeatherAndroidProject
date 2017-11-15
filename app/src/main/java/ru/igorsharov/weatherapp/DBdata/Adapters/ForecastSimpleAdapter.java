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
    private boolean showTemperature;
    private boolean showPressure;

    public ForecastSimpleAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, boolean[] showFlags) {
        super(context, layout, c, from, to, flags);
        this.from = from;
        showTemperature = showFlags[0];
        showPressure = showFlags[1];
    }

    private String getCurrentColumn(Cursor cursor, String from) {
        return cursor.getString(cursor.getColumnIndexOrThrow(from));
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String[] mFrom = from;

        // инициализация некоторых view
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvDate1 = view.findViewById(R.id.tvDate1);
        ImageView imgView = view.findViewById(R.id.imageView);

        // установка значений даты
        String date = getCurrentColumn(cursor, mFrom[0]);
        tvDate.setText(date.substring(0, 9));
        tvDate1.setText(date.substring(10, date.length()));

        // установка значений температуры
        if (showTemperature) {
            TextView tvTemperature = view.findViewById(R.id.tvTemperature);

            String temperature = getCurrentColumn(cursor, mFrom[1]);
            if (temperature != null) {
                tvTemperature.setVisibility(View.VISIBLE);
                tvTemperature.setTextColor(DataWeatherHandler.colorOfTemp(context, temperature));
                tvTemperature.setText(DataWeatherHandler.addDegree(temperature));
            }
        }

        // установка значений давления
        if (showPressure) {
            TextView tvPressure = view.findViewById(R.id.tvPressure);

            String pressure = getCurrentColumn(cursor, mFrom[2]);
            if (pressure != null) {
                tvPressure.setVisibility(View.VISIBLE);
                tvPressure.setText(pressure.concat(" ").concat(context.getResources().getString(R.string.pressure_amount)));
            }
        }

        // установка иконок погоды
        String idIconWeatherToday = getCurrentColumn(cursor, mFrom[3]);
        if (idIconWeatherToday != null) {
            String iconStr = String.valueOf(DataWeatherHandler.getIconId(idIconWeatherToday));
            imgView.setVisibility(View.VISIBLE);
            setViewImage(imgView, iconStr);
        }
    }
}
