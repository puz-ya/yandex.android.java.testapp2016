package puzino.yandexandroidjavatestapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YDeathYLORD on 23.04.2016.
 */
public class ArtistObject implements Parcelable{

    //9 полей из JSON файла
    private Integer id;
    private String name;
    private String genresNames; //список жанров через ","

    private Integer tracks;
    private Integer albums;
    private String link;
    private String description;

    private String cover_small; //храним только текст ссылок, подгружаем по мере надобности и возможности
    private String cover_big;

    public ArtistObject(
            Integer id,
            String name,
            String genresNames,
            Integer tracks,
            Integer albums,
            String link,
            String description,
            String cover_small,
            String cover_big
    ) {
        this.id = id;
        this.name = name;
        this.genresNames = genresNames;

        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;

        this.cover_small = cover_small;
        this.cover_big = cover_big;
    }
    //*/

    // 99.9% можем игнорировать, хотя...
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeString(genresNames);

        out.writeInt(tracks);
        out.writeInt(albums);
        out.writeString(link);
        out.writeString(description);

        out.writeString(cover_small);
        out.writeString(cover_big);
    }

    // для пересоздания объекта
    // все Parcelables должны CREATOR that implements these two methods
    public static final Parcelable.Creator<ArtistObject> CREATOR = new Parcelable.Creator<ArtistObject>() {
        public ArtistObject createFromParcel(Parcel in) {
            return new ArtistObject(in);
        }

        public ArtistObject[] newArray(int size) {
            return new ArtistObject[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private ArtistObject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        genresNames = in.readString();;

        tracks = in.readInt();
        albums = in.readInt();
        link = in.readString();
        description = in.readString();;

        cover_small = in.readString();
        cover_big = in.readString();
    }

    //------------- ID исполнителя
    public Integer getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    //------------- Имя исполнителя
    public String getNameOfArtist() {
        return name;
    }
    public void setNameOfArtist(String name) { this.name = name; }

    //------------ Жанры
    public String getGenresNames() {
        return genresNames;
    }
    public void setGenresNames(String genresNames) {
        this.genresNames = genresNames;
    }

    //-------------- Кол-во треков
    public Integer getTracks() {
        return tracks;
    }
    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    //-------------- Кол-во альбомов
    public Integer getAlbums() {
        return albums;
    }
    public void setAlbums(int albums) {
        this.albums = albums;
    }

    //------------- Ссылка
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

    //------------- Описание
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    //------------- Картинка мини
    public String getCover_small() {
        return cover_small;
    }
    public void setCover_small(String cover_small) {
        this.cover_small = cover_small;
    }

    //------------- Картинка макси
    public String getCover_big() {
        return cover_big;
    }
    public void setCover_big(String cover_big) {
        this.cover_big = cover_big;
    }
}
