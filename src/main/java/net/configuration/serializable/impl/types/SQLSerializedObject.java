package net.configuration.serializable.impl.types;

import com.google.gson.JsonObject;
import net.configuration.main.Main;
import net.configuration.network.SQLConnection;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class SQLSerializedObject extends JsonSerializedObject {

    private static final String TABLE_PREFIX = "serializable_";
    @NotNull private final String tableName;
    @NotNull private final SQLConnection connection;

    public SQLSerializedObject(@NotNull SQLConnection con, @NotNull Class<?> clazz) {
        super(clazz);
        this.tableName = TABLE_PREFIX + clazz.getSimpleName();
        this.connection = con;
    }

    public SQLSerializedObject(@NotNull SQLConnection con, @NotNull String tableName, @NotNull Class<?> forClass) {
        super(forClass);
        this.tableName = tableName;
        this.connection = con;
        this.data = this.readFromTable();
    }

    public SQLSerializedObject(@NotNull SQLConnection con, @NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);
        this.tableName = TABLE_PREFIX + clazz.getSimpleName();
        this.connection = con;
    }

    @SuppressWarnings("unused")
    protected SQLSerializedObject(){
        super();
        this.connection = Main.getDefaultConnection();
        this.tableName = TABLE_PREFIX + "dummy";
    }

    @Override
    public void flush() {
        super.flush();

        //write to table
        if(!this.connection.isConnected())
            this.connection.connect();

        int z = 9;
    }

    @Override
    public String toString() {
        return tableName;
    }

    @NotNull
    private JsonObject readFromTable(){
        if(!this.connection.isConnected())
            this.connection.connect();


        return null;
    }

    private String get(@NotNull String column, int key){
        return null;
    }

    private void createTable(){

    }
}
