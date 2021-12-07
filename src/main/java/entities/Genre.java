package entities;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Table(name = "genre")
@Entity
@NamedQueries({
        @NamedQuery(name = "Genre.deleteAllRows", query = "DELETE from Genre")
})
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(name, genre.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}