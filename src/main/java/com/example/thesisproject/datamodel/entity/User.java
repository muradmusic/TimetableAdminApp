package com.example.thesisproject.datamodel.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.*;


@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
public  class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;



    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role> roles = new HashSet<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserSubject> userSubjects = new HashSet<>();


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

//    public void assignRoleToUser(Role role) {
//        this.roles.add(role);
//        role.getUsers().add(this);
//    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User other = (User) obj;
        return Objects.equals(id, other.id);
    }

}
