package ru.igorsharov.weatherapp.Async;

import android.app.Fragment;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import ru.igorsharov.weatherapp.DBdata.DBWeatherContract;
import ru.igorsharov.weatherapp.DataHandler.DataWeatherHandler;
import ru.igorsharov.weatherapp.DataHandler.DbUtils;
import ru.igorsharov.weatherapp.DataHandler.NetUtils;
import ru.igorsharov.weatherapp.DataHandler.ParsingUtils;
import ru.igorsharov.weatherapp.R;

/**
 * реализация фонового запроса названия города от GoogleGeo по введенным данным пользователя
 * описание к происходящему есть во втором фрагменте
 */
public class DownloadTask extends AsyncTask<String, Void, String[]> {

    private WeakReference<Fragment> fragmentReference;
    private String id;
    private final String T_NAME;

    public DownloadTask(Fragment fragment, final String T_NAME) {
        fragmentReference = new WeakReference<>(fragment);
        this.T_NAME = T_NAME;
    }


    @Override
    protected void onPreExecute() {

        // получаем слабую ссылку на фрагмент для доступа к его методам
        Fragment todayFragment = fragmentReference.get();
        // добавление пустой строки с информацией о загрузке
        // и получение id этой строки
        id = String.valueOf(
                DbUtils.putWeatherDbLine(
                        T_NAME,
                        new String[]{DBWeatherContract.DBKeys.C_CITY},
                        new String[]{todayFragment.getResources().getString(R.string.load_message)}));
        todayFragment.getLoaderManager().getLoader(0).forceLoad();
    }

    /**
     * @param params подается "черновой" город введеный пользователем
     * @return город от Гугла и статус попытки записи его в базу
     * для последующего выбора сообщения пользователю
     */
    @Override
    protected String[] doInBackground(String... params) {
        String draftCity = params[0];
        // TODO DbUtil и тому подобное спрядать в один метод внутрь DataHandler
        // возвращает массив с названием города от Гугл и статусом запроса
        String[] cityAndStat = NetUtils.loadCityOfGoogleAndPutInDB(T_NAME, draftCity, id);
        String lng = DbUtils.getLongitude(id);
        String lat = DbUtils.getLatitude(id);
        JSONObject jo = NetUtils.loadWeather(lng, lat, false);
        ParsingUtils.setWeatherJSONParser(jo);
        DbUtils.updWeatherDbLine(
                T_NAME,
                id,
                DBWeatherContract.DBKeys.keysTodayArr,
                ParsingUtils.parseWeatherToday());

        return cityAndStat;
    }

    @Override
    protected void onPostExecute(String[] params) {
        Fragment todayFragment = fragmentReference.get();

        // вывод сообщений пользователю о результате добавления города
        // 0 - название города
        // 1 - результат добавления
        if (Integer.parseInt(params[1]) == 0) {
            DataWeatherHandler.printMessage(todayFragment.getActivity(), params[0] + " " + todayFragment.getResources().getString(R.string.error_load_message));
        } else if (Integer.parseInt(params[1]) == 1) {
            DataWeatherHandler.printMessage(todayFragment.getActivity(), params[0] + " " + todayFragment.getResources().getString(R.string.ok_load_message));
        }
        todayFragment.getLoaderManager().getLoader(0).forceLoad();
    }
}
