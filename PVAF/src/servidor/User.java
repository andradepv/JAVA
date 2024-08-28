package servidor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String login;
    private String password;
    private boolean online;
    private List<String> unreadMessages;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.online = false;
        this.unreadMessages = new ArrayList<>();
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public List<String> getUnreadMessages() {
        return unreadMessages;
    }

    public void addUnreadMessage(String message) {
        unreadMessages.add(message);
    }

    public void clearUnreadMessages() {
        unreadMessages.clear();
    }

    @Override
    public String toString() {
        return "Login: " + login + ", Status: " + (online ? "Online" : "Offline");
    }
}
