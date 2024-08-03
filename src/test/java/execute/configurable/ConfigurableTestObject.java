package execute.configurable;

import net.configuration.config.Configurable;
import net.configuration.config.Configuration;
import net.configuration.config.FileConfiguration;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;

class ConfigurableTestObject implements Configurable {

    private transient Configuration config;

    public ConfigurableTestObject(){

    }

    @Override
    public @NotNull Configuration getConfig() {
        if(this.config == null)
            throw new IllegalStateException("Config is not loaded");

        return config;
    }

    @Override
    public void loadConfig(@NotNull String path, boolean externalFile) {
        File file = new File(path);
        if(!file.exists())
            throw new IllegalStateException("Config file at " + path + " does not exist");

        this.config = FileConfiguration.loadConfig(file);
    }

    @Override
    public boolean isReadOnly(@NotNull String path) {
        return path.contains("read_only") || path.contains("readOnly");
    }

    @Override
    public void write(@NotNull SerializedObject dest) {

    }

    @Override
    public @NotNull ConfigurableTestObject read(@NotNull SerializedObject src) {
        return this;
    }
}
