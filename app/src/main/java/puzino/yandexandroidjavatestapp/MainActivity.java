package puzino.yandexandroidjavatestapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //устанавливаем начальный вид
        new TestParse().execute();              //получаем данные из yandex json

        //
    }

    public static String LOG_TAG = "test_artist_log";

    //ассинхронный класс для загрузки файла
    //входные данные - нет, промежуточные - нет, возвращаемые - строка
    private class TestParse extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        //перед стартом "закачки" надо показать начальное состояние (Загрузка...)
        //TODO: написать protected void onPostExecute()

        @Override   // получаем данные с внешнего ресурса в фоне
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://download.cdn.yandex.net/mobilization-2016/artists.json");

                //соединяемся
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                //переводим в строку
                resultJson = buffer.toString();

                //закрываем соединение
                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        //после doInBackground запускаем распарсивание и заполнение UI
        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            // получаем json-массив
            JSONArray dataJsonObj = null;
            // список всех слоёв (карточек\блоков) с артистами
            ListView listOfViews = (ListView) findViewById(R.id.ListViewArtistSmall);

            try {
                dataJsonObj = new JSONArray(strJson);

                // идём по всем исполнителям
                for (int i = 0; i < dataJsonObj.length(); i++) {
                    JSONObject artist = dataJsonObj.getJSONObject(i);

                    //получаем строку с именем
                    String name = artist.getString("name");
                    //получаем маленькое и большое изображение, это JSONObject
                    JSONObject cover = artist.getJSONObject("cover");

                    String cover_small = cover.getString("small");
                    String cover_big = cover.getString("big");

                    //TODO: идём в цикле по слою и вставляем layouts_artist_short по числу исполнителей
                    //TODO: Заготовка для скачивания изображений исполнителей и их добавление в layout
                    //Drawable downloadedImage = ImageGet(this, url);
                    //image.setImageDrawable(downloadedImage);

                    Log.d(LOG_TAG, "Name: " + name + cover_big);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
