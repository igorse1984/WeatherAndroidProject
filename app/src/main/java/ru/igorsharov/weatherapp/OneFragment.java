package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract.WeatherEntry;

public class OneFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private final static String TAG = OneFragment.class.getSimpleName();
    private CheckBox chBoxTemperature, chBoxPressure, chBoxForecast;
    private ListView listView;
    private EditText editTextCityAdd;
    private Button buttonAdd;
    private SimpleCursorAdapter lvAdapter;
    private DBWeather db = AppDB.getDb();
    private Cursor cursorForList;


    /**
     * Private field for store a link to the Location Listener object
     */
    private LocListener locListener = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        initView(view);
        initLoc();
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);
        buttonAdd.setOnClickListener(this);
        cursorForList = db.getReadableCursor(WeatherEntry.T_NAME);
        return view;
    }

    /* обновление listView по новой технологии взамен устаревшего метода requery */
    private void cursorReNew() {
        cursorForList = db.getReadableCursor(WeatherEntry.T_NAME);
        lvAdapter.swapCursor(cursorForList);
    }

    private void initView(View v) {
        chBoxTemperature = v.findViewById(R.id.checkBoxTemperature);
        chBoxPressure = v.findViewById(R.id.checkBoxPressure);
        chBoxForecast = v.findViewById(R.id.checkBoxWeatherForecast);
        listView = v.findViewById(R.id.listView);
        editTextCityAdd = v.findViewById(R.id.editTextCityAdd);
        buttonAdd = v.findViewById(R.id.buttonAdd);

        //Create arrays of columns and UI elements
        String[] from = {WeatherEntry.C_CITY};
        int[] to = {R.id.tvName};
        //Create simple Cursor adapter
        lvAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item, cursorForList, from, to, 1);

        listView.setAdapter(lvAdapter);
    }

    void initLoc() {
         /* Get a link to the Location Manager */
        LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

		/* Get information from Network location provider */
        Location loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc != null) {
            String str = getAddressStr(loc);
            /* Show address */
            if (str != null) {
                editTextCityAdd.setText("");
                editTextCityAdd.setText(str);
            }
        }
    }


    /**
     * Get address string by location value
     */
    private String getAddressStr(Location loc) {

		/* Define variable for store result */
        String str = "";

		/* Create Geocoder object */
        Geocoder geo = new Geocoder(getActivity());

		/* Get addresses list by location and prepare result */
        try {

			/* Get addresses list by location and prepare result */
            List<Address> aList = geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

			/* Get address */
            if (aList.size() > 0) {

				/* Get first element from List */
                Address a = aList.get(0);

				/* Get a Postal Code */
//                int maxAddrLine = a.getMaxAddressLineIndex();
//                if (maxAddrLine >= 0) {
//                    str = a.getAddressLine(maxAddrLine);
//                    if (!str.isEmpty())
//                        str += ", ";
//                }

				/* Prepare a result */
//                str += a.getCountryName() + ", " + a.getAdminArea() + ", " + a.getThoroughfare() + " "
//                        + a.getSubThoroughfare();
                str = a.getAdminArea();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }

		/* Return a value */
        return str;
    }


    private void showWeatherInfo(String city) {
        Bundle b = new Bundle();

        b.putBoolean(SecondFragment.SHOW_TEMPERATURE, chBoxTemperature.isChecked());
        b.putBoolean(SecondFragment.SHOW_PRESSURE, chBoxPressure.isChecked());
        b.putBoolean(SecondFragment.SHOW_FORECAST, chBoxForecast.isChecked());

        b.putString(SecondFragment.CITY, city);


        // обратиться к активити можно либо через создание интерфейса фрагмента,
        // либо через каст с получением ссылки на активити
        ((MainActivity) getActivity()).onListViewSelected(b);
    }

    /* обработка кликов */
    @Override
    public void onClick(View v) {
        String city = String.valueOf(editTextCityAdd.getText());
        editTextCityAdd.setText("");
        WeatherBox.getInstance().addCity(city);
        cursorReNew();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        db.delete(id);
        cursorReNew();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showWeatherInfo(String.valueOf(((TextView) view).getText()));
    }

    /**
     * Class that implements Location Listener interface
     */
    private class LocListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

			/* Show current location */
//            tvGPS.setText(getLocStr(location));

			/* Get Address by location */
            String str = getAddressStr(location);

			/* Show address */
            if (str != null) {
//                tvAdr.setText(str);
                editTextCityAdd.setText(str);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}
