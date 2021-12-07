package dtos;

import entities.Post;

public class PostDTO {
    String artist;
    String coverUrl;
    String description;
    String spotifyLinkUrl;
    String track;


    public PostDTO(String artist, String coverUrl, String description, String spotifyLinkUrl, String track) {
        this.artist = artist;
        this.coverUrl = coverUrl;
        this.description = description;
        this.spotifyLinkUrl = spotifyLinkUrl;
        this.track = track;
    }
    public PostDTO(Post post){
        this.artist = post.getArtist();
        this.coverUrl = post.getCoverURL();
        this.description = post.getDescription();
        this.spotifyLinkUrl = post.getSpotifyLinkUrl();
        this.track = post.getTrackName();
    }
}
