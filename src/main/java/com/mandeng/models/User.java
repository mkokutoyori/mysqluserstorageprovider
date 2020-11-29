package com.mandeng.models;

import lombok.Data;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name="getUserByUsername", query="select u from User u where u.username = :username"),
        @NamedQuery(name="getUserByEmail", query="select u from User u where u.email = :email"),
        @NamedQuery(name="getUserCount", query="select count(u) from User u"),
        @NamedQuery(name="getAllUsers", query="select u from User u"),
        @NamedQuery(name="searchForUser", query="select u from User u where " +
                "( lower(u.username) like :search or u.email like :search ) order by u.username"),
        @NamedQuery(name = "getPassword",query = "select u.password from User u where (u.username= :search or u.email= :search) order by u.username")
})
@Entity
@Table(name = "UserEntity")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String jobTitle;
}
