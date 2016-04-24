package puzino.yandexandroidjavatestapp;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by YDeathYLORD on 23.04.2016.
 */
public class ArtistSmallAdapter extends ArrayAdapter<String> {

    private List<ArtistObject> data;
    private Context context;
    ImageView smallImage;

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
        smallImage = (ImageView)view.findViewById(R.id.imageViewSmall);

        // получаем элемент из списка
        ArtistObject ObjectOfArtist = data.get(position);

        //в зависимости от количества, меняется падеж (по умолчанию 1 шт)
        String albums_count_text = context.getResources().getString(R.string.album_1);
        albums_count_text = getFinalLetter(ObjectOfArtist.getAlbums(), albums_count_text);
        String tracks_count_text = context.getResources().getString(R.string.track_1);
        tracks_count_text = getFinalLetter(ObjectOfArtist.getTracks(), tracks_count_text);

        //формируем html строку для наглядности
        String htmlStr = "<b>" + ObjectOfArtist.getNameOfArtist() + "</b> <br />" + ObjectOfArtist.getGenresNames() + "<br />"
                + "<a href=\"" + ObjectOfArtist.getLink() + "\">" + ObjectOfArtist.getLink() + "</a>" + "<br />"
                + ObjectOfArtist.getAlbums().toString() + " " + albums_count_text + ", "
                + ObjectOfArtist.getTracks().toString() + " " + tracks_count_text;

        // устанавливаем значения компонентам одного эелемента списка
        textArea.setText(Html.fromHtml(htmlStr));
        textArea.setMovementMethod (LinkMovementMethod.getInstance());

        if(ObjectOfArtist.getCover_small() != "") {

            /* Хорошая шутка про jpeg CMYK */

            NetworkImageView ivCover = (NetworkImageView) view.findViewById(R.id.imageViewSmall);
            ivCover.setImageUrl(ObjectOfArtist.getCover_small(), VolleySingleton.getInstance().getImageLoader());

        }else{
            smallImage.setImageDrawable(context.getResources().getDrawable(R.drawable.image_view_small_blank));
        }
        return view;
    }

    //делаем правильные окончания в зависимости от количества (альбомов или треков)
    public String getFinalLetter(Integer in, String str){

        if(in % 10 == 0) {
            return str + context.getResources().getString(R.string.albums_tracks_many);
        }

        if(in % 10 == 1 && in != 11){
            return str;
        }

        if((in % 10 == 2 || in % 10 == 3 || in % 10 == 4) && (in != 12 || in != 13 || in != 14)){
            return str + context.getResources().getString(R.string.albums_tracks_2);
        }

        return str + context.getResources().getString(R.string.albums_tracks_many);
    }
}
