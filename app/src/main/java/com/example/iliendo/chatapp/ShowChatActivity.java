package com.example.iliendo.chatapp;

/**
 * Helps us get data from fire base to show all active users
 *
 * Created by iliendo on 5/20/19.
 */

public class ShowChatActivity {
    private String nickname;
    private String imageUrl;
    private String email;

    public ShowChatActivity() {
        // Empty constructor
    }

    public ShowChatActivity(String nickname, String imageUrl, String email) {
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
