package puzino.yandexandroidjavatestapp;

import android.content.res.Resources;

/**
 * Created by YDeathYLORD on 26.04.2016 at 20:41.
 * Класс для работы со строками (пока только окончания)
 */
public class ArtistStrings {

    //делаем правильные окончания в зависимости от количества (альбомов или треков)
    public static String getFinalLetter(Resources res, Integer in, String str){

        if(in % 10 == 0) {
            return str + res.getString(R.string.albums_tracks_many);
        }

        if(in % 10 == 1 && in != 11){
            return str;
        }

        if((in % 10 == 2 || in % 10 == 3 || in % 10 == 4) && (in != 12 && in != 13 && in != 14)){
            return str + res.getString(R.string.albums_tracks_2);
        }

        return str + res.getString(R.string.albums_tracks_many);
    }

}
