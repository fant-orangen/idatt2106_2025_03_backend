package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", nullable = false, unique = true, length = 50)
  private String name;

  @OneToMany(mappedBy = "role")
  private List<User> users;

  // Constructors
  public Role() {
  }

  public Role(String name) {
    this.name = name;
  }

  // Getters and Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }
}
