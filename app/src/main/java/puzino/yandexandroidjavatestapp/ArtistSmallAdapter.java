package puzino.yandexandroidjavatestapp;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        String htmlStr = "";
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
                ArtistObject aObj = data.get(position);
                Intent i = DetailActivity.newIntent(context, aObj);
                context.startActivity(i);
            }
        }
        );
        //*/


        // получаем элемент из списка
        ArtistObject ObjectOfArtist = data.get(position);

        Integer album_num = ObjectOfArtist.getAlbums(); //чтоб каждый раз не вызывать
        Integer track_num = ObjectOfArtist.getTracks();

        //в зависимости от количества, меняется падеж (по умолчанию 1 шт)
        String albums_count_text = context.getResources().getString(R.string.album_1);
        albums_count_text = ArtistStrings.getFinalLetter(context.getResources(),album_num, albums_count_text);
        String tracks_count_text = context.getResources().getString(R.string.track_1);
        tracks_count_text = ArtistStrings.getFinalLetter(context.getResources(),track_num, tracks_count_text);

        //формируем html строку для наглядности
        htmlStr = "<h3>" + ObjectOfArtist.getNameOfArtist() + "</h3>"
                + ObjectOfArtist.getGenresNames() + "<br /> <br />"
                //+ "<a href=\"" + ObjectOfArtist.getLink() + "\">" + ObjectOfArtist.getLink() + "</a>" + "<br />"
                + album_num.toString() + " " + albums_count_text + ", "
                + track_num.toString() + " " + tracks_count_text;

        holder.txt.setText(Html.fromHtml(htmlStr));
        // устанавливаем значения компонентам одного эелемента списка
        // holder.txt.setMovementMethod (LinkMovementMethod.getInstance()); //отказался от ссылок в списке - много места на малых экранах

        if(!ObjectOfArtist.getCover_small().equals("")) {

                /* Хорошая шутка про jpeg CMYK, грузим прямо из интернета */

            holder.img = (NetworkImageView) convertView.findViewById(R.id.imageViewSmall);
            holder.img.setDefaultImageResId(R.drawable.image_view_small_blank); //устанавливаем картинку по-умолчанию
            holder.img.setErrorImageResId(R.drawable.image_view_small_error);   //устанавливаем картинку ошибки
            holder.img.setImageUrl(ObjectOfArtist.getCover_small(), VolleySingleton.getInstance().getImageLoader());

        }else{
            holder.img.setImageDrawable(context.getResources().getDrawable(R.drawable.image_view_small_blank));
        }

        return convertView;
    }

}
