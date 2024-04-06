package com.example.thesisproject.datamodel.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
public class Role {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name ="role_id", nullable = false)
        private Long roleId;

        @Basic
        @Column(name = "name", nullable = false, length = 45, unique = true)
        private String name;

        @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
        private Set<com.example.thesisproject.datamodel.entity.User> users = new HashSet<>();

        @Override
        public String toString() {
                return "Role{" +
                        "roleId=" + roleId +
                        ", name='" + name + '\'' +
                        '}';
        }
        public Role(String name) {
                this.name = name;
        }



        @Override
        public int hashCode() {
                return Objects.hash(roleId);
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null || getClass() != obj.getClass()) return false;
                Role other = (Role) obj;
                return Objects.equals(roleId, other.roleId);
        }

}
