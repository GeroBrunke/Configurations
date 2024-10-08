package net.configuration.network;

import net.configuration.serializable.api.*;
import net.configuration.serializable.impl.SimpleCreatorImpl;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLConnection implements SerializableObject {

    @SuppressWarnings("unused")
    @SerializationAPI
    private static final Creator<SQLConnection> CREATOR = new SimpleCreatorImpl<>(SQLConnection.class);

    private String host;
    private int port;
    private String username;
    private String password;
    private String database;

    @IgnoreSerialization
    private transient Connection connection;

    /**
     * Create a new connection instance containing the relevant information to connect to a SQL server.
     *
     * @param host The SQL server's host.
     * @param port The server's port.
     * @param username The SQL username.
     * @param password The user's password.
     * @param database The database to work in.
     */
    public SQLConnection(@NotNull String host, int port, @NotNull String username, @NotNull String password, @NotNull String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    @SuppressWarnings("unused")
    private SQLConnection(){} //Hide implicit

    /**
     * Connect the current {@link SQLConnection} instance to the given server.
     *
     * @return True if the connection could be created, false if an error occurred.
     */
    public boolean connect(){
        try {
            //Class.forName(this.driverClass); //load the driver and then connect to the given sql server.
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" +
                    this.database + "", this.username, this.password); //?serverTimezone=Europe/Rome?autoReconnect=true
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Close the active sql connection. May throw a {@link NullPointerException} if the connection is closed.
     */
    public void disconnect(){
        try {
            this.connection.close();
            this.connection = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the current connection to a SQL server.
     *
     * @return The current connection to the SQL server, possibly null if not connected.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Check if the current sql connection is active.
     *
     * @return True, iff the connection is active, false otherwise.
     */
    public boolean isConnected(){
        return this.connection != null;
    }

    /**
     * Executes a SQL-Command on the current connection.
     *
     * @param sqlCommand The SQL-Statement
     */
    public void update(@NotNull String sqlCommand){
        try (PreparedStatement pst = connection.prepareStatement(sqlCommand)){
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString(this.host);
        dest.setInt(this.port);
        dest.setString(this.username);
        dest.setString(this.password);
        dest.setString(this.database);
    }

    @Override
    public @NotNull SQLConnection read(@NotNull SerializedObject src) {
        this.host = src.getString().orElse("");
        this.port = src.getInt().orElse(-1);
        this.username = src.getString().orElse("");
        this.password = src.getString().orElse("");
        this.database = src.getString().orElse("");

        return this;
    }
}
