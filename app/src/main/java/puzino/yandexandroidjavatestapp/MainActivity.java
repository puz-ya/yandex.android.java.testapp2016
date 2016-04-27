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
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity{

    //главный урл всея приложения
    public String mainYandexURL = "http://download.cdn.yandex.net/mobilization-2016/artists.json";

    boolean error_connect = false;  //предполагаем наличие соединения и успешно загрузки
    boolean error_parse = false;  //предполагаем успешную разделку json файла

    ParseJSON parseJSON = null;
    Button bt = null;

    ListView listView;
    LinearLayout relativeLayout;
    ArtistSmallAdapter adapter;
    ProgressDialog pDialog;

    final Activity main_context = MainActivity.this;
    ArrayList<ArtistObject> listOfArtistsObjects = new ArrayList<ArtistObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //устанавливаем начальный вид

        //находим ListView (список всех)
        bt = (Button) findViewById(R.id.button1);
        listView = (ListView) findViewById(R.id.ListViewArtistSmall);
        relativeLayout = (LinearLayout) findViewById(R.id.layout_artist_small);

        //инициализация адаптера
        adapter = new ArtistSmallAdapter(this, listOfArtistsObjects);
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(true);

        parseJSON = new ParseJSON();
        parseJSON.execute();              //получаем данные из yandex json в отдельном потоке

    }

    public void refreshMessage(View view) {
        bt.setVisibility(View.INVISIBLE);
        parseJSON = new ParseJSON();
        parseJSON.execute();
    }

    //ассинхронный класс для загрузки файла (новый поток обязателен)
    //входные данные - нет, промежуточные (прогресс) - строка с данными, возвращаемые - нет
    private class ParseJSON extends AsyncTask<Void, String, Void> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //диалог прогресса слишком долгий (виснет)...
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage(getResources().getString(R.string.async_0_1_start));
            pDialog.show();

        }

        @Override   // получаем данные с внешнего ресурса в фоне
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(mainYandexURL);

                //обнуляем
                error_connect = false;
                error_parse = false;

                //соединяемся
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);  //5 секунд
                urlConnection.setReadTimeout(5000);  //5 секунд
                urlConnection.connect();

                publishProgress("1.1"); //1.1 - загрузка, 2.1 - парсинг

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                //переводим в строку
                resultJson = buffer.toString();

                //отсылаем обновление о переводе в строку
                publishProgress("1.2");

                //закрываем соединение
                urlConnection.disconnect();

            } catch (Exception e) {
                error_connect = true;
                publishProgress("-1");
            }

            // объявляем json-массив
            JSONArray dataJsonObj = null;
            //если успешно скачали, то начинаем парсить
            if(!error_connect) {
                try {

                    dataJsonObj = new JSONArray(resultJson);

                    publishProgress("2.1"); //начали разделовать json

                    // идём по всем исполнителям
                    int len = dataJsonObj.length(); //чуть ускоряем работу
                    String unknownData = getResources().getString(R.string.unknown);    //пишем вместо "данных нет"

                    for (int i = 0; i < len; i++) {
                        JSONObject artist = dataJsonObj.getJSONObject(i);

                        Integer art_id = -i;    //чтобы не повторялись, вдруг пригодится
                        if (artist.has("id")) {
                            art_id = artist.getInt("id");
                        }

                        String art_name = unknownData;
                        if (artist.has("name")) {
                            art_name = artist.getString("name"); //получаем строку с именем
                        }

                        String art_genresNames = unknownData;
                        if (artist.has("genres")) {
                            JSONArray art_genresJSON = artist.getJSONArray("genres");   //получаем массив названий жанров
                            int artGenLength = art_genresJSON.length();     //чтоб чуть побыстрее
                            if (artGenLength > 0) {
                                art_genresNames = art_genresJSON.getString(0); //перезаписываем жанры в строку
                                for (int j = 1; j < artGenLength; j++) { // с 1, т.к. первый уже записан
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

                        String art_description = unknownData;
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

                        if(i == len/4 + 1){
                            publishProgress("2.2"); //четверть прошли
                        }

                        if(i == len/2 + 1){
                            publishProgress("2.3"); //половину прошли
                        }

                        if(i == 3*len/4 + 1){
                            publishProgress("2.4"); // 3\4 прошли
                        }

                        String[] strToAddAndUpdate = {art_id.toString(), art_name, art_genresNames, art_tracks.toString(), art_albums.toString(), art_link, art_description, art_cover_small, art_cover_big};
                        publishProgress(strToAddAndUpdate); //отсылаем и обновляем

                    }

                } catch (JSONException e) {
                    error_parse = true;
                    publishProgress("-2");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param){
            super.onPostExecute(param);

            //в любом случае закрываем ширму диалога
            pDialog.dismiss();

            if(error_connect || error_parse){
                bt.setVisibility(View.VISIBLE);
            }else{
                bt.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        protected void onProgressUpdate(String... progress) {

            if(progress[0].equals("1.1")){
                pDialog.setMessage( getResources().getString(R.string.async_1_1_connect) );
                return;
            }

            if(progress[0].equals("1.2")){
                pDialog.setMessage( getResources().getString(R.string.async_1_2_convert));
                return;
            }

            if(progress[0].equals("-1")){

                pDialog.hide(); //прячем диалог
                Toast.makeText(main_context, getResources().getString(R.string.error_connect), Toast.LENGTH_LONG).show();
                return;
            }

            if(progress[0].equals("2.1")){
                pDialog.setMessage( getResources().getString(R.string.async_2_1_parsing) );
                return;
            }

            if(progress[0].equals("2.2")){
                pDialog.setMessage( getResources().getString(R.string.async_2_2_parsing) );
                return;
            }

            if(progress[0].equals("2.3")){
                pDialog.setMessage( getResources().getString(R.string.async_2_3_parsing)  );
                return;
            }

            if(progress[0].equals("2.4")){
                pDialog.setMessage( getResources().getString(R.string.async_2_4_parsing) );
                return;
            }

            if(progress[0].equals("-2")){
                pDialog.hide(); //прячем диалог
                Toast.makeText(main_context, getResources().getString(R.string.error_parse), Toast.LENGTH_LONG).show();
                return;
            }

            //прячем диалог
            //pDialog.hide();
            //прячем кнопку обновления
            //bt.setVisibility(View.INVISIBLE);

            //помещаем данные в объект для исполнителей
            ArtistObject ArtObject = new ArtistObject(Integer.parseInt(progress[0]), progress[1], progress[2], Integer.parseInt(progress[3]), Integer.parseInt(progress[4]), progress[5], progress[6], progress[7], progress[8]);
            //помещаем объект в общий список, для занесения в ListView
            listOfArtistsObjects.add(ArtObject);
            adapter.notifyDataSetChanged();
            //listView.requestLayout(); //более "глубокая" перезагрузка
            listView.invalidate();
        }
    }

}
