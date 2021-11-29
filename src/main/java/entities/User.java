package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name = "users")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "user_name", length = 25)
  private String userName;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "user_pass")
  private String userPass;
  @JoinTable(name = "user_roles", joinColumns = {
          @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
          @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
  @ManyToMany
  private List<Role> roleList = new ArrayList<>();


  @JoinTable(name = "user_genres", joinColumns = {
          @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
          @JoinColumn(name = "genre_name", referencedColumnName = "genre_name")})
  @ManyToMany
  private List<Genre> favouriteGenres = new ArrayList<>();

  @Column(name = "profileDescription")
  @Basic(optional = false)
  @Size( max = 255)
  private String profileDescription;


  public List<String> getPreSelectedGenres() {
    if (favouriteGenres.isEmpty()) {
      return null;
    }
    List<String> preselectedGenresAsString = new ArrayList<>();
    favouriteGenres.forEach((genre) -> {
      preselectedGenresAsString.add(genre.getName());
    });
    return preselectedGenresAsString;
  }

  public List<Genre> getFavouriteGenres() {
    return favouriteGenres;
  }

  public List<String> getRolesAsStrings() {
    if (roleList.isEmpty()) {
      return null;
    }
    List<String> rolesAsStrings = new ArrayList<>();
    roleList.forEach((role) -> {
      rolesAsStrings.add(role.getRoleName());
    });
    return rolesAsStrings;
  }

  public User() {}

  //TODO Change when password is hashed
  public boolean verifyPassword(String pw){
    return BCrypt.checkpw(pw, userPass);
  }

  public User(String userName, String userPass) {
    this.userName = userName;

    this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
  }

  // Hash a password for the first time
  //String hashed = BCrypt.hashpw(userPass, BCrypt.gensalt());

  public void addGenre(Genre genre){
    this.favouriteGenres.add(genre);

  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserPass() {
    return this.userPass;
  }

  public void setUserPass(String userPass) {
    this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
  }

  public List<Role> getRoleList() {
    return roleList;
  }

  public void setRoleList(List<Role> roleList) {
    this.roleList = roleList;
  }

  public void addRole(Role userRole) {
    roleList.add(userRole);
  }

  public String getProfileDescription() {
    return profileDescription;
  }

  public void setProfileDescription(String profileDescription) {
    this.profileDescription = profileDescription;
  }
}
