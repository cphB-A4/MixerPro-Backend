package dtos;

import entities.Genre;
import entities.Post;
import entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private String username;
    private List<GenreDTO> favouriteGenres;
    private String profileDescription;
    private String profileGifUrl;
    private List<PostDTO> posts;

    //Json will not be displayed if value is null. So if value is null we set it to ""
    public UserDTO(User user) {
        this.username = user.getUserName();
        this.favouriteGenres = new ArrayList<>();
        for (Genre genre: user.getFavouriteGenres()) {
            this.favouriteGenres.add(new GenreDTO(genre));
        }
        this.profileDescription = user.getProfileDescription();
        if (profileDescription == null){
            this.profileDescription = "";
        }
        this.profileGifUrl = user.getProfileGifUrl();
        if (profileGifUrl == null){
            this.profileGifUrl = "";
        }
        this.posts = new ArrayList<>();
        for (Post post: user.getPosts()) {
            this.posts.add(new PostDTO(post));
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<GenreDTO> getFavouriteGenres() {
        return favouriteGenres;
    }

    public void setFavouriteGenres(List<GenreDTO> favouriteGenres) {
        this.favouriteGenres = favouriteGenres;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public String getProfileGifUrl() {
        return profileGifUrl;
    }

    public void setProfileGifUrl(String profileGifUrl) {
        this.profileGifUrl = profileGifUrl;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "username='" + username + '\'' +
                ", favouriteGenres=" + favouriteGenres +
                ", profileDescription='" + profileDescription + '\'' +
                ", profileGifUrl='" + profileGifUrl + '\'' +
                ", posts=" + posts +
                '}';
    }
}
