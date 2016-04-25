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
public class ArtistSmallAdapter extends ArrayAdapter<ArtistObject> {

    private List<ArtistObject> data;
    private Context context;
    public LayoutInflater inflater;

    public ArtistSmallAdapter(Context context, List<ArtistObject> data) {
        super(context, R.layout.layout_artist_short);
        this.data = data;
        this.context = context;

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // возвращаем количество элементов списка
        return data.size();
    }


    @Override
    public ArtistObject getItem(int position) {
        // получение одного элемента по индексу
        return data.get(position);
    } //*/

    /*
    @Override
    public ArtistObject getItem(int position) {
        return data.get(position);
    } */

    @Override
    public long getItemId(int position) {
        return position;
    }

    //паттерн viewholder для ускорения
    public static class ViewHolder
    {
        NetworkImageView img;  //картинка слева
        TextView txt;   //текстовый блок справа
    }

    // заполнение элементов списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String htmlStr = "";
        NetworkImageView ivCover = null;

        ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            // задаем вид элемента списка, который мы создали высше
            convertView = inflater.inflate(R.layout.layout_artist_short, parent, false);

            // проставляем данные для элементов
            holder.txt = (TextView)convertView.findViewById(R.id.textSmall);
            holder.img = (NetworkImageView) convertView.findViewById(R.id.imageViewSmall);

            convertView.setTag(holder);

        }else{
            holder=(ViewHolder)convertView.getTag();
        }


        convertView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                int i = 1;
                if(i==0)
                {
                    i=1;
                }
                else
                {
                    i=0;
                }

            }
        });


        // получаем элемент из списка
        ArtistObject ObjectOfArtist = data.get(position);

        Integer album_num = ObjectOfArtist.getAlbums(); //чтоб каждый раз не вызывать
        Integer track_num = ObjectOfArtist.getTracks();

        //в зависимости от количества, меняется падеж (по умолчанию 1 шт)
        String albums_count_text = context.getResources().getString(R.string.album_1);
        albums_count_text = getFinalLetter(album_num, albums_count_text);
        String tracks_count_text = context.getResources().getString(R.string.track_1);
        tracks_count_text = getFinalLetter(track_num, tracks_count_text);

        //формируем html строку для наглядности
        htmlStr = "<b>" + ObjectOfArtist.getNameOfArtist() + "</b> <br />" + ObjectOfArtist.getGenresNames() + "<br />"
                + "<a href=\"" + ObjectOfArtist.getLink() + "\">" + ObjectOfArtist.getLink() + "</a>" + "<br />"
                + album_num.toString() + " " + albums_count_text + ", "
                + track_num.toString() + " " + tracks_count_text;

        holder.txt.setText(Html.fromHtml(htmlStr));
        // устанавливаем значения компонентам одного эелемента списка
        holder.txt.setMovementMethod (LinkMovementMethod.getInstance());

        if(!ObjectOfArtist.getCover_small().equals("")) {

                /* Хорошая шутка про jpeg CMYK */

            holder.img = (NetworkImageView) convertView.findViewById(R.id.imageViewSmall);
            holder.img.setImageUrl(ObjectOfArtist.getCover_small(), VolleySingleton.getInstance().getImageLoader());

        }else{
            holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.image_view_small_blank));
        }

        return convertView;
    }



    //делаем правильные окончания в зависимости от количества (альбомов или треков)
    public String getFinalLetter(Integer in, String str){

        if(in % 10 == 0) {
            return str + context.getResources().getString(R.string.albums_tracks_many);
        }

        if(in % 10 == 1 && in != 11){
            return str;
        }

        if((in % 10 == 2 || in % 10 == 3 || in % 10 == 4) && (in != 12 && in != 13 && in != 14)){
            return str + context.getResources().getString(R.string.albums_tracks_2);
        }

        return str + context.getResources().getString(R.string.albums_tracks_many);
    }
}
