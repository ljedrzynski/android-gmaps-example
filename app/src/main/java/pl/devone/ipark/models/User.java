package pl.devone.ipark.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ljedrzynski on 22.05.2017.
 */

public class User implements Serializable {

    private Long id;
    private String nick;
    private String email;
    private String password;
    private Date createdAt;
    private Date updatedAt;

    @SerializedName("auth_token")
    private String authToken;

    public User() {
    }

    public User(Long id, String nick, String email, String password, Date createdAt, Date updatedAt, String authToken) {
        this.id = id;
        this.nick = nick;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.authToken = authToken;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String nick, String email, String password) {
        this.nick = nick;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
