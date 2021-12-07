package dtos;

import entities.Genre;
import entities.RenameMe;

import java.util.ArrayList;
import java.util.List;

public class GenreDTO {
    private String name;
    private List<GenreDTO> genreDTOList;

    public GenreDTO(List<GenreDTO>genreDTOList){
        this.genreDTOList=genreDTOList;
    }

    public static List<GenreDTO> getDtos(List<Genre> genres){
        List<GenreDTO> genreDtos = new ArrayList();
        genres.forEach(genre->genreDtos.add(new GenreDTO(genre)));
        return genreDtos;
    }
    public GenreDTO(Genre genre) {
        this.name = genre.getName();
    }
//hej


    public GenreDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
