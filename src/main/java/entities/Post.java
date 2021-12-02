package entities;

import javax.persistence.*;

@Table(name = "post")
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Integer post_id;
    @ManyToOne
    private User user;
    private String trackID;
    private String trackName;
    private String artist;
    private String coverURL;
    private String description;
    private String spotifyLinkUrl;

    public Post() {
    }

    public Post(User user, String trackID, String trackName, String artist, String coverURL, String description, String spotifyLinkUrl) {
        this.user = user;
        this.trackID = trackID;
        this.trackName = trackName;
        this.artist = artist;
        this.coverURL = coverURL;
        this.description = description;
        this.spotifyLinkUrl = spotifyLinkUrl;
    }

    public String getSpotifyLinkUrl() {
        return spotifyLinkUrl;
    }

    public void setSpotifyLinkUrl(String spotifyLinkUrl) {
        this.spotifyLinkUrl = spotifyLinkUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTrackID() {
        return trackID;
    }

    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPost_id() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }
}