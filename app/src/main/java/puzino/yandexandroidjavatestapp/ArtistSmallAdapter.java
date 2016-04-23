package puzino.yandexandroidjavatestapp;

import android.content.Context;
import android.widget.ArrayAdapter;

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

    //TODO: идём в цикле по слою и вставляем layouts_artist_short по числу исполнителей
    //TODO: Заготовка для скачивания изображений исполнителей и их добавление в layout
    //Drawable downloadedImage = ImageGet(this, url);
    //image.setImageDrawable(downloadedImage);
}
