package puzino.yandexandroidjavatestapp;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by YDeathYLORD on 25.04.2016.
 */

public class DetailActivity extends AppCompatActivity {

    private ArtistObject data;
    private static final String EXTRA_OBJECT = "puzino.yandexandroidjavatestapp.ArtistObject";

    private TextView detailedGenresAlbumsTracksView;
    private TextView detailedInfoView;
    private NetworkImageView detailedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Intent i = getIntent();
        ArtistObject artObject = (ArtistObject) i.getParcelableExtra(EXTRA_OBJECT);

        setTitle(artObject.getNameOfArtist());  //установка имени исполнителя

        detailedGenresAlbumsTracksView = (TextView) findViewById(R.id.textBigMiddle);
        detailedInfoView = (TextView) findViewById(R.id.textBigBottom);
        detailedImageView = (NetworkImageView) findViewById(R.id.imageViewBig);

        Integer album_num = artObject.getAlbums(); //чтоб каждый раз не вызывать
        Integer track_num = artObject.getTracks();

        //в зависимости от количества, меняется падеж (по умолчанию 1 шт)
        String albums_count_text = getResources().getString(R.string.album_1);
        albums_count_text = ArtistStrings.getFinalLetter(getResources(),album_num, albums_count_text);
        String tracks_count_text = getResources().getString(R.string.track_1);
        tracks_count_text = ArtistStrings.getFinalLetter(getResources(),track_num, tracks_count_text);


        //формируем html строку для наглядности
        String htmlStr = artObject.getGenresNames() + "<br />"
                + "<a href=\"" + artObject.getLink() + "\">" + artObject.getLink() + "</a>" + "<br />"
                + album_num.toString() + " " + albums_count_text + ", "
                + track_num.toString() + " " + tracks_count_text;

        detailedGenresAlbumsTracksView.setText(Html.fromHtml(htmlStr));
        // устанавливаем значения компонентам одного эелемента списка
        detailedGenresAlbumsTracksView.setMovementMethod (LinkMovementMethod.getInstance());

        detailedInfoView.setText(artObject.getDescription());

        //проверяем наличие БОЛЬШОЙ картинки
        if(!artObject.getCover_big().equals("")) {

                /* Хорошая шутка про jpeg CMYK */

            detailedImageView = (NetworkImageView) findViewById(R.id.imageViewSmall);
            detailedImageView.setDefaultImageResId(R.drawable.image_view_big_blank); //устанавливаем картинку по-умолчанию
            detailedImageView.setErrorImageResId(R.drawable.image_view_big_error);   //устанавливаем картинку ошибки
            detailedImageView.setImageUrl(artObject.getCover_big(), VolleySingleton.getInstance().getImageLoader());

        }else{
            detailedImageView.setImageDrawable(getResources().getDrawable(R.drawable.image_view_big_blank));
        }

    }

    public static Intent newIntent(Context packageContext, ArtistObject detail) {
        Intent i = new Intent(packageContext, DetailActivity.class);
        i.putExtra(EXTRA_OBJECT, (Parcelable) detail); //получаем не ID исполителя, а его позицию
        return i;
    }
}
