package org.example.database.config;

public class ConnectionSettings {
    public final String namespace;
    public final String url;
    public final String user;
    public final String password;

    public ConnectionSettings(String url, String user, String password) {
        this(url, user, password, null);
    }

    public ConnectionSettings(String url, String user, String password, String namespace) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.namespace = namespace;
    }
}
