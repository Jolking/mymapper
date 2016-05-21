package com.mymapper.entity;

import com.mymapper.annotation.Table;

/**
 * Created by huang on 5/21/16.
 */

@Table(tableName = "t_user", id = "user_id")
public class User {
    private Integer userId;
    private String username;
    private boolean deleted;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "User{" +
            "userId=" + userId +
            ", username='" + username + '\'' +
            ", deleted=" + deleted +
            '}';
    }
}
