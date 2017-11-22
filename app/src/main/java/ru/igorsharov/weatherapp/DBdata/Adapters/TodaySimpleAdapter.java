package ru.igorsharov.weatherapp.DBdata.Adapters;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ru.igorsharov.weatherapp.DataHandler.DataWeatherHandler;
import ru.igorsharov.weatherapp.R;


public class TodaySimpleAdapter extends SimpleCursorAdapter {

    private String[] from;

    public TodaySimpleAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.from = from;
    }

    // Метод положено переопределять в кастомных Simple адаптерах,
    // но здесь он не требуется
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        return inflater.inflate(layout, null);
//    }
    private String getCurrentColumn(Cursor cursor, String from) {
        return cursor.getString(cursor.getColumnIndexOrThrow(from));
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String[] mFrom = from;
        String cityName = getCurrentColumn(cursor, mFrom[0]);
        TextView tvCity = view.findViewById(R.id.tvCityName);
        setViewText(tvCity, cityName);


        String temperature = getCurrentColumn(cursor, mFrom[1]);
        if (temperature != null) {
            TextView tvTemperature = view.findViewById(R.id.tvTemperature);
            tvTemperature.setVisibility(View.VISIBLE);
            tvTemperature.setTextColor(DataWeatherHandler.colorOfTemp(context, temperature));
            tvTemperature.setText(DataWeatherHandler.addDegree(temperature));
        }

        String idIconWeatherToday = getCurrentColumn(cursor, mFrom[2]);
        if (idIconWeatherToday != null) {
            ImageView imgView = view.findViewById(R.id.imageView);
            // достаем путь к ресурсу для иконки
            String iconStr = String.valueOf(DataWeatherHandler.getIconId(idIconWeatherToday));
            imgView.setVisibility(View.VISIBLE);
            setViewImage(imgView, iconStr);
        }
    }

}
