package org.jed.playground.auth.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Jorge España
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "users")
@IdClass(LoginUser.UserKey.class)
public class LoginUser implements Serializable {
    @Id
    private String username;
    @Id
    private String tenant;
    @Column(nullable = false)
    private String password = "";
    @Column
    private String email = "";
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name="authority")
    @CollectionTable(name="authorities", joinColumns= {@JoinColumn(name="username"),@JoinColumn(name="tenant")})
    private List<String> authorities;

    @EqualsAndHashCode
    public static class UserKey implements Serializable {
        protected String username;
        protected String tenant;

        public UserKey() {}

        public UserKey(String username, String tenant) {
            this.username = username;
            this.tenant = tenant;
        }
    }
}