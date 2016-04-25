package puzino.yandexandroidjavatestapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity implements OnItemClickListener{

    boolean error_connect = false;  //предполагаем наличие соединения и успешно загрузки
    boolean error_parse = false;  //предполагаем успешную разделку json файла

    ListView listView;
    LinearLayout relativeLayout;
    ArtistSmallAdapter adapter;
    ProgressDialog pDialog;
    String LOG_TAG = "myLogs";

    final Activity main_context = MainActivity.this;
    ArrayList<ArtistObject> listOfArtistsObjects = new ArrayList<ArtistObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //устанавливаем начальный вид

        //находим ListView (список всех)
        listView = (ListView) findViewById(R.id.ListViewArtistSmall);
        relativeLayout = (LinearLayout) findViewById(R.id.layout_artist_small);

        //инициализация адаптера
        adapter = new ArtistSmallAdapter(this, listOfArtistsObjects);
        listView.setAdapter(adapter);


        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             Log.d(LOG_TAG, "itemSelect: position = " + position + ", id = " + id);
            }
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(LOG_TAG, "itemSelect: nothing");
            }
        });
        //*/

        new ParseJSON().execute();              //получаем данные из yandex json в отдельном потоке
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // arg2 = the id of the item in our view (List/Grid) that we clicked
        // arg3 = the id of the item that we have clicked
        // if we didn't assign any id for the Object (Book) the arg3 value is 0
        // That means if we comment, aBookDetail.setBookIsbn(i); arg3 value become 0
        Toast.makeText(getApplicationContext(), "You clicked on position : " + arg2 + " and id : " + arg3, Toast.LENGTH_LONG).show();
    }

    //ассинхронный класс для загрузки файла (новый поток обязателен)
    //входные данные - нет, промежуточные (прогресс) - строка с данными, возвращаемые - нет
    private class ParseJSON extends AsyncTask<Void, String, Void> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        Integer progressInt = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //диалог прогресса слишком долгий (виснет)...
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading JSON file ...");
            pDialog.show();

        }

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
                error_connect = true;
                publishProgress("-1");
                e.printStackTrace();
            }

            // объявляем json-массив
            JSONArray dataJsonObj = null;
            //если успешно скачали, то начинаем парсить
            if(!error_connect) {
                try {
                    publishProgress("-3");
                    dataJsonObj = new JSONArray(resultJson);

                    // идём по всем исполнителям
                    for (int i = 0; i < dataJsonObj.length(); i++) {
                        JSONObject artist = dataJsonObj.getJSONObject(i);

                        Integer art_id = -i;    //чтобы не повторялись
                        if (artist.has("id")) {
                            art_id = artist.getInt("id");
                        }

                        String art_name = "[" + getResources().getString(R.string.unknown) + "]";
                        if (artist.has("name")) {
                            art_name = artist.getString("name"); //получаем строку с именем
                        }

                        String art_genresNames = "[" + getResources().getString(R.string.unknown) + "]";
                        if (artist.has("genres")) {
                            JSONArray art_genresJSON = artist.getJSONArray("genres");   //получаем массив названий жанров
                            if (art_genresJSON.length() > 0) {
                                art_genresNames = art_genresJSON.getString(0); //перезаписываем жанры в строку
                                for (int j = 1; j < art_genresJSON.length(); j++) { // с 1, т.к. первый уже записан
                                    art_genresNames = art_genresNames + ", " + art_genresJSON.getString(j);
                                }
                            }
                        }

                        Integer art_tracks = 0;
                        if (artist.has("tracks")) {
                            art_tracks = artist.getInt("tracks");
                        }

                        Integer art_albums = 0;
                        if (artist.has("albums")) {
                            art_albums = artist.getInt("albums");
                        }

                        String art_link = "";   //ссылка остаётся пустой
                        if (artist.has("link")) {
                            art_link = artist.getString("link"); //получаем ссылку
                        }

                        String art_description = "[" + getResources().getString(R.string.unknown) + "]";
                        if (artist.has("description")) {
                            art_description = artist.getString("description"); //получаем описание
                        }

                        //получаем маленькое и большое изображение, это JSONObject
                        String art_cover_small = "";
                        String art_cover_big = "";
                        if (artist.has("cover")) {
                            JSONObject coverJSON = artist.getJSONObject("cover");

                            if (coverJSON.has("small")) {
                                art_cover_small = coverJSON.getString("small");
                            }

                            if (coverJSON.has("big")) {
                                art_cover_big = coverJSON.getString("big");
                            }
                        }

                        String[] strToAddAndUpdate = {art_id.toString(), art_name, art_genresNames, art_tracks.toString(), art_albums.toString(), art_link, art_description, art_cover_small, art_cover_big};
                        publishProgress(strToAddAndUpdate);

                    }

                } catch (JSONException e) {
                    error_parse = true;
                    publishProgress("-2");
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void... params) {
            pDialog.dismiss();

        }

        @Override
        protected void onProgressUpdate(String... progress) {

            //прячем диалог (слишком долго)
            pDialog.hide();

            if(progress[0].equals("-1")){
                Toast.makeText(main_context, getResources().getString(R.string.error_connect), Toast.LENGTH_LONG).show();
                return;
            }

            if(progress[0].equals("-2")){
                Toast.makeText(main_context, getResources().getString(R.string.error_parse), Toast.LENGTH_LONG).show();
                return;
            }

            if(progress[0].equals("-3")){
                //pDialog.setMessage("Parsing JSON file ...");
                return;
            }

            //помещаем данные в объект для исполнителей
            ArtistObject ArtObject = new ArtistObject(Integer.parseInt(progress[0]), progress[1], progress[2], Integer.parseInt(progress[3]), Integer.parseInt(progress[4]), progress[5], progress[6], progress[7], progress[8]);
            //помещаем объект в общий список, для занесения в ListView
            listOfArtistsObjects.add(ArtObject);
            adapter.notifyDataSetChanged();
            //listView.requestLayout();
            listView.invalidate();
        }
    }

}
