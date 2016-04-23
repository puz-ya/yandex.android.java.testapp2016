package puzino.yandexandroidjavatestapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArtistSmallAdapter adapter;

    ArrayList<ArtistObject> listOfArtistsObjects = new ArrayList<ArtistObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //устанавливаем начальный вид
        new ParseJSON().execute();              //получаем данные из yandex json

        //заполняем ListView
        listView = (ListView) findViewById(R.id.ListViewArtistSmall);

        //инициализация адаптера
        adapter = new ArtistSmallAdapter(this, listOfArtistsObjects);
        listView.setAdapter(adapter);
    }

    //ассинхронный класс для загрузки файла
    //входные данные - нет, промежуточные - нет, возвращаемые - строка
    private class ParseJSON extends AsyncTask<Void, Void, String> {

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

        @Override   //после doInBackground запускаем распарсивание и заполнение UI
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            // получаем json-массив
            JSONArray dataJsonObj = null;

            try {
                dataJsonObj = new JSONArray(strJson);

                // идём по всем исполнителям
                for (int i = 0; i < dataJsonObj.length(); i++) {
                    JSONObject artist = dataJsonObj.getJSONObject(i);

                    int art_id = artist.getInt("id");
                    String art_name = artist.getString("name"); //получаем строку с именем

                    JSONArray art_genresJSON = artist.getJSONArray("genres");   //получаем массив названий жанров
                    String art_genresNames = art_genresJSON.getString(0); //перезаписываем жанры в строку
                    for(int j = 1 ; j < art_genresJSON.length(); j++) { // с 1, т.к. первый уже записан
                        art_genresNames = art_genresNames + ", " + art_genresJSON.getString(j);
                    }

                    int art_tracks = artist.getInt("tracks");
                    int art_albums = artist.getInt("albums");
                    String art_link = artist.getString("link"); //получаем ссылку
                    String art_description = artist.getString("description"); //получаем описание

                    //получаем маленькое и большое изображение, это JSONObject
                    JSONObject coverJSON = artist.getJSONObject("cover");
                    String art_cover_small = coverJSON.getString("small");
                    String art_cover_big = coverJSON.getString("big");

                    //помещаем данные в класс для исполнителей
                    ArtistObject ArtObject = new ArtistObject(art_id, art_name, art_genresNames, art_tracks, art_albums, art_link, art_description, art_cover_small, art_cover_big);

                    //помещаем его в общий список, для занесения в ListView
                    listOfArtistsObjects.add(ArtObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
