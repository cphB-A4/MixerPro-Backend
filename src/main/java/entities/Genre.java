package entities;

import javax.persistence.*;
import java.util.List;

@Table(name = "genre")
@Entity
public class Genre {
    @Id
    @Column(name = "genre_name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "favouriteGenres")
    private List<User> userList;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Genre() {
    }

    public Genre(String name) {
        this.name = name;
    }
}