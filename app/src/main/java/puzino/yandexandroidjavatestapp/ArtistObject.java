package puzino.yandexandroidjavatestapp;

/**
 * Created by Yury on 23.04.2016.
 */
public class ArtistObject {

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

    public void setNameOfArtist(String name) {
        this.name = name;
    }

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
