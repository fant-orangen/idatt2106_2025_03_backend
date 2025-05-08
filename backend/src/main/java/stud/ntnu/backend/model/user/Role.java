package stud.ntnu.backend.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Entity class representing a user role in the system.
 * Roles are used to define different access levels and permissions for users.
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<User> users;

    /**
     * Default constructor required by JPA.
     */
    public Role() {
    }

    /**
     * Constructs a new Role with the specified name.
     *
     * @param name the name of the role
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Gets the unique identifier of the role.
     *
     * @return the role ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the role.
     *
     * @param id the role ID to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the name of the role.
     *
     * @return the role name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the role.
     *
     * @param name the role name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of users associated with this role.
     *
     * @return the list of users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Sets the list of users associated with this role.
     *
     * @param users the list of users to set
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }
}
