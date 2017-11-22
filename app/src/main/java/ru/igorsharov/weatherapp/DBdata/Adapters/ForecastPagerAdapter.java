package ru.igorsharov.weatherapp.DBdata.Adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;


public class ForecastPagerAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
