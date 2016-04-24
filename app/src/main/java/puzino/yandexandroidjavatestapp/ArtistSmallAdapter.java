package puzino.yandexandroidjavatestapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;

/**
 * Created by YDeathYLORD on 23.04.2016.
 */
public class ArtistSmallAdapter extends ArrayAdapter<String> {

    private List<ArtistObject> data;
    private Context context;

    public ArtistSmallAdapter(Context context, List<ArtistObject> data) {
        super(context, R.layout.layout_artist_short);
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        // возвращаем количество элементов списка
        return data.size();
    }

    @Override
    public String getItem(int position) {
        // получение одного элемента по индексу
        return data.get(position).getNameOfArtist();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // заполнение элементов списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // задаем вид элемента списка, который мы создали высше
        View view = inflater.inflate(R.layout.layout_artist_short, parent, false);

        // проставляем данные для элементов
        TextView textArea = (TextView)view.findViewById(R.id.textSmall);
        ImageView smallImage = (ImageView)view.findViewById(R.id.imageViewSmall);

        // получаем элемент из списка
        ArtistObject ObjectOfArtist = data.get(position);

        String albums_count = "";   //в зависимости от количества, меняется падеж
        String tracks_count = "";   //в зависимости от количества, меняется падеж

        //формируем html строку для наглядности
        String htmlStr = "<b>" + ObjectOfArtist.getNameOfArtist() + "</b> <br />" + ObjectOfArtist.getGenresNames() + "<br />"
                + "<a href=\"" + ObjectOfArtist.getLink() + "\">" + ObjectOfArtist.getLink() + "</a>" + "<br />"
                + ObjectOfArtist.getAlbums().toString() + "" + ", "
                + ObjectOfArtist.getTracks().toString();

        // устанавливаем значения компонентам одного эелемента списка
        textArea.setText(Html.fromHtml(htmlStr));
        textArea.setMovementMethod (LinkMovementMethod.getInstance());

        Drawable downloadedImage = LoadImageFromWebOperations(ObjectOfArtist.getCover_small(),ObjectOfArtist.getNameOfArtist());
        smallImage.setImageDrawable(downloadedImage);

        return view;
    }

    //TODO: идём в цикле по слою и вставляем layouts_artist_short по числу исполнителей
    //TODO: Заготовка для скачивания изображений исполнителей и их добавление в layout
    //Drawable downloadedImage = ImageGet(this, url);
    //image.setImageDrawable(downloadedImage);

    public static Drawable LoadImageFromWebOperations(String url, String name) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, name);
            return d;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
