package ru.igorsharov.weatherapp;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import ru.igorsharov.weatherapp.Async.DownloadTask;
import ru.igorsharov.weatherapp.DBdata.Adapters.TodaySimpleAdapter;
import ru.igorsharov.weatherapp.DBdata.DBProvider;
import ru.igorsharov.weatherapp.DBdata.DBWeather;
import ru.igorsharov.weatherapp.DBdata.DBWeatherContract;

public class TodayFragment extends Fragment implements
        View.OnClickListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        TextWatcher,
        LoaderManager.LoaderCallbacks<Cursor> {


    // The adapter that binds our data to the ListView
    private TodaySimpleAdapter lvTodayAdapter;


    private final static String TAG = TodayFragment.class.getSimpleName();
    public final static String T_NAME = "weatherToday";
    private final static long MINTIME = 3000L;
    private final static float MINDIST = 1.0F;
    private CheckBox chBoxTemperature, chBoxPressure;
    private ListView listView;
    private EditText editTextCityAdd;
    private Button buttonAdd;
    private Button btnFindLoc;
    // TODO данная переменная здесь только для обновления состояние listView может убрать в DataWeatherHandler?
    private DBWeather db = AppDB.getDb();
    private LocationHelper lochlp;
    private Location loc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_lv, container, false);
        initViews(view);
        setListeners();
        initLoc();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDbListAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        lochlp.setLocListener(MINTIME, MINDIST);
    }

    @Override
    public void onPause() {
        lochlp.removeLocUpd();
        super.onPause();
    }

    private void initViews(View v) {
        chBoxTemperature = v.findViewById(R.id.checkBoxTemperature);
        chBoxPressure = v.findViewById(R.id.checkBoxPressure);
        listView = v.findViewById(R.id.listView);
        editTextCityAdd = v.findViewById(R.id.editTextCityAdd);
        buttonAdd = v.findViewById(R.id.buttonAdd);
        btnFindLoc = v.findViewById(R.id.btnFindLoc);
    }

    private void setListeners() {
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);
        buttonAdd.setOnClickListener(this);
        btnFindLoc.setOnClickListener(this);
        editTextCityAdd.addTextChangedListener(this);
    }

    private void setDbListAdapter() {
        //Create arrays of columns and UI elements
        String[] from = DBWeatherContract.DBKeys.keysForTodayAdapter;

        //Create simple Cursor adapter
        // null потому что адаптер знает про view элементы
        lvTodayAdapter = new TodaySimpleAdapter(getActivity(), R.layout.today_list_item,
                null, from, null, 0);

        listView.setAdapter(lvTodayAdapter);

        // Initialize the Loader with id '1' and callbacks 'mCallbacks'.
        // If the loader doesn't already exist, one is created. Otherwise,
        // the already created Loader is reused. In either case, the
        // LoaderManager will manage the Loader across the Activity/Fragment
        // lifecycle, will receive any new loads once they have completed,
        // and will report this new data back to the 'mCallbacks' object.
        getLoaderManager().initLoader(0, null, this);
    }

    void initLoc() {
        lochlp = new LocationHelper(getActivity());
        loc = lochlp.getLastLoc();
    }

    // TODO перенести чекбоксы во второй фрагмент
    // переход по второй фрагмент и передача настроек
    private void toNextFragment(String id) {
        Bundle b = new Bundle();

        b.putBoolean(ForecastFragment.TEMPERATURE_SHOW_KEY, chBoxTemperature.isChecked());
        b.putBoolean(ForecastFragment.PRESSURE_SHOW_KEY, chBoxPressure.isChecked());
        b.putString(ForecastFragment.ID_DB_CITY_KEY, id);


        // обратиться к активити можно либо через создание интерфейса фрагмента,
        // либо через каст с получением ссылки на активити
        ((MainActivity) getActivity()).onListViewSelected(b);
    }

    /**
     * ОБРАБОТКА КЛИКОВ
     */

    // TODO скрывать клавиатуру после нажатия кновки добавления
    // TODO добавить виджет
    // TODO доработать геолокацию

    //Клики по кнопкам
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAdd:
                String draftCity = String.valueOf(editTextCityAdd.getText());
                editTextCityAdd.setText("");

                // способ скрыть клавиатуру с экрана
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }

                // получение текущей погоды, запускаем в новом потоке
                new DownloadTask(this, T_NAME).execute(draftCity);
                break;

            case R.id.btnFindLoc:
                if (loc != null) {
                    String strLoc = lochlp.getAddressStr(loc);
                     /* Show address */
                    if (strLoc != null) {
                        editTextCityAdd.setText("");
                        editTextCityAdd.setText(strLoc);
                    }
                }
                break;
        }
    }

    //Клики по ListView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String lvCity = String.valueOf(((TextView) view.findViewById(R.id.tvCityName)).getText());
        // блокирование отправки серверу сообщения о загрузке вместо названия города
        if (!lvCity.equals(getResources().getString(R.string.load_message))) {
            // переход во второй фрагмент с передачей id города в базе
            toNextFragment(String.valueOf(id));
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String sId = String.valueOf(id);
        // удаление записи о городе
        db.delete(T_NAME, sId);
        // обновляем вид, для исчезновения item'а с listView
        getLoaderManager().getLoader(0).forceLoad();
        return true;
    }


    // задаем реакцию EditText
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        boolean isEmpty = (String.valueOf(editTextCityAdd.getText()).equals(""));
        btnFindLoc.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        buttonAdd.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }


    /**
     * реализация интерфейса LoaderManager
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("@@@@", "onCreateLoader");
        // Create a new CursorLoader with the following query parameters.
        return new CursorLoader(getActivity(), DBProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d("@@@@", "onLoaderFinished");
        lvTodayAdapter.swapCursor(cursor);
    }
    // The listview now displays the queried data.

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("@@@@", "onLoaderReset");
        // For whatever reason, the Loader's data is now unavailable.
        // Remove any references to the old data by replacing it with
        // a null Cursor.
        lvTodayAdapter.swapCursor(null);
    }
}
