package ru.igorsharov.weatherapp.DBdata;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ru.igorsharov.weatherapp.DataWeatherHandler;
import ru.igorsharov.weatherapp.R;


public class CustomSimpleAdapter extends SimpleCursorAdapter {

    //    private Context mContext;
//    private Context appContext;
//    private int layout;
    //    private Cursor cr;
//    private final LayoutInflater inflater;
    private String[] from;
//    private int[] to;

    public CustomSimpleAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.from = from;
//        this.to = to;
//        this.layout = layout;
//        this.mContext = context;
//        this.inflater = LayoutInflater.from(context);
//        this.cr = c;
    }

    // Метод положено переопределять в кастомных Simple адаптерах,
    // но здесь он не требуется
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        return inflater.inflate(layout, null);
//    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
//        super.bindView(view, context, cursor);
        final String[] mFrom = from;

        int indexCity = cursor.getColumnIndexOrThrow(mFrom[0]);
        String cityName = cursor.getString(indexCity);
        TextView tvCity = view.findViewById(R.id.tvCityName);
        setViewText(tvCity, cityName);

        TextView tvTemperature = view.findViewById(R.id.tvTemperature);

        // TODO неплохая идея для реализации в парсере вместо написания строки запроса
        // TODO неправильно что ключи ячеек задаются в адаптере
        int indexTemp = cursor.getColumnIndexOrThrow(mFrom[1]);
        String temperature = cursor.getString(indexTemp);
        if (temperature != null) {
            tvTemperature.setVisibility(View.VISIBLE);
            tvTemperature.setTextColor(DataWeatherHandler.colorOfTemp(context, temperature));
            tvTemperature.setText(DataWeatherHandler.weatherString(temperature));
        }
        ImageView imgView = view.findViewById(R.id.imageView);

        int indexIcon = cursor.getColumnIndexOrThrow(mFrom[2]);
        String idIconWeatherToday = cursor.getString(indexIcon);

        if (idIconWeatherToday != null) {

            String iconStr = DataWeatherHandler.DBData.getIconId(idIconWeatherToday);

            imgView.setVisibility(View.VISIBLE);
            setViewImage(imgView, iconStr);
        }

    }
}
