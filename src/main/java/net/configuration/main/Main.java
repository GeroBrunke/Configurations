package net.configuration.main;

import net.configuration.network.SQLConnection;
import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableType;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;

public final class Main {

    /*
    TODO: Add configurations

    TODO: Add SQL Serializable and Config
     */

    private static SQLConnection defaultCon;

    public static void main(String[] args){
        //empty
    }

    @NotNull
    public static SQLConnection getDefaultConnection(){
        if(defaultCon != null)
            return defaultCon;

        try{
            Path dataFolderPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).toPath();
            File file = new File(dataFolderPath.toFile().getAbsolutePath() + File.separator + "defaultSQLConnection.properties");

            try(FileInputStream fis = new FileInputStream(file)){
                SerializedObject obj = SerializedObject.readFromStream(SerializableType.PROPERTIES, SQLConnection.class, fis);
                Creator<SQLConnection> creator = Creator.getCreator(SQLConnection.class);
                defaultCon = creator.read(obj);
                return defaultCon;
            }

        }catch(Exception e){
            throw new SerializationException(e);
        }
    }

}
