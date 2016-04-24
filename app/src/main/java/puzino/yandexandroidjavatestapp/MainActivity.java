package puzino.yandexandroidjavatestapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        new ParseJSON().execute();              //получаем данные из yandex json в отдельном потоке

        //находим ListView
        listView = (ListView) findViewById(R.id.ListViewArtistSmall);

        //инициализация адаптера
        adapter = new ArtistSmallAdapter(this, listOfArtistsObjects);
        listView.setAdapter(adapter);
    }

    //ассинхронный класс для загрузки файла (новый поток обязателен)
    //входные данные - нет, промежуточные (прогресс) - нет, возвращаемые - строка
    private class ParseJSON extends AsyncTask<Void, Integer, Void> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        Integer progressInt = 0;

        //перед стартом "закачки" надо показать начальное состояние (Загрузка...)
        //TODO: написать protected void onPreExecute()

        @Override   // получаем данные с внешнего ресурса в фоне
        protected Void doInBackground(Void... params) {
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

            // объявляем json-массив
            JSONArray dataJsonObj = null;

            try {
                dataJsonObj = new JSONArray(resultJson);

                // идём по всем исполнителям
                for (int i = 0; i < dataJsonObj.length(); i++) {
                    JSONObject artist = dataJsonObj.getJSONObject(i);

                    int art_id = -1;
                    if(artist.has("id")){
                        art_id = artist.getInt("id");
                    };

                    String art_name = "["+getResources().getString(R.string.unknown)+"]";
                    if(artist.has("name")){
                        art_name = artist.getString("name"); //получаем строку с именем
                    };

                    String art_genresNames = "["+getResources().getString(R.string.unknown)+"]";
                    if(artist.has("genres")){
                        JSONArray art_genresJSON = artist.getJSONArray("genres");   //получаем массив названий жанров
                        if(art_genresJSON.length() > 0){
                            art_genresNames = art_genresJSON.getString(0); //перезаписываем жанры в строку
                            for(int j = 1 ; j < art_genresJSON.length(); j++) { // с 1, т.к. первый уже записан
                                art_genresNames = art_genresNames + ", " + art_genresJSON.getString(j);
                            }
                        }
                    }

                    int art_tracks = 0;
                    if(artist.has("tracks")){
                        art_tracks = artist.getInt("tracks");
                    }

                    int art_albums = 0;
                    if(artist.has("albums")){
                        art_albums = artist.getInt("albums");
                    }

                    String art_link = "";   //ссылка остаётся пустой
                    if(artist.has("link")){
                        art_link = artist.getString("link"); //получаем ссылку
                    }

                    String art_description = "["+getResources().getString(R.string.unknown)+"]";
                    if(artist.has("description")){
                        art_description = artist.getString("description"); //получаем описание
                    }

                    //получаем маленькое и большое изображение, это JSONObject
                    String art_cover_small = "";
                    String art_cover_big = "";
                    if(artist.has("cover")){
                        JSONObject coverJSON = artist.getJSONObject("cover");

                        if(coverJSON.has("small")){
                            art_cover_small = coverJSON.getString("small");
                        }

                        if(coverJSON.has("big")){
                            art_cover_big = coverJSON.getString("big");
                        }
                    }


                    //помещаем данные в объект для исполнителей
                    ArtistObject ArtObject = new ArtistObject(art_id, art_name, art_genresNames, art_tracks, art_albums, art_link, art_description, art_cover_small, art_cover_big);

                    //помещаем объект в общий список, для занесения в ListView
                    listOfArtistsObjects.add(ArtObject);
                    progressInt = i;
                    publishProgress(progressInt);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //return resultJson;
            return null;
        }

        /*
        @Override   //после doInBackground запускаем заполнение UI
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);



        }
        //*/

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //super.onProgressUpdate(progress);
            //check log if paths and pix are updated
            Log.d("arr_size", String.valueOf(listOfArtistsObjects.size()));
            //Log.d("pix_size", String.valueOf(pix.size());
            adapter.notifyDataSetChanged();
            listView.requestLayout();
            //if notifyDataSetChanged() fails try the ff:
            //lstView.setAdapter(null);
            //lstView.setAdapter(new ImageAdapter(this, pix, paths);
            //super.onProgressUpdate(progress);
        }
    }

}
