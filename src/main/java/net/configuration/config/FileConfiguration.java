package net.configuration.config;

import net.configuration.serializable.api.SerializableType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public abstract class FileConfiguration implements Configuration{

    @NotNull protected final File file;

    protected FileConfiguration(File file) throws IOException {
        if(!file.exists() && !file.createNewFile())
            throw new IOException("Configuration file could not be created.");

        this.file = file;
    }

    /**
     * Load a configuration on the given file based on this file's extension. If the given file does not exist then this
     * method will create the given file if possible.
     *
     * @param file The file to open.
     * @return A new {@link FileConfiguration} instance to modify this file.
     * @throws IllegalArgumentException If the configuration could not be loaded.
     * @see SerializableType#forFile(File)
     */
    @NotNull
    public static FileConfiguration loadConfig(File file){
        Configuration config = SerializableType.forFile(file);
        if(config instanceof FileConfiguration fc){
            return fc;
        }

        throw new IllegalArgumentException("Not a file config: " + config.getClass());
    }

    @Override
    @NotNull
    public String getName(){
        return file.getName();
    }


}
