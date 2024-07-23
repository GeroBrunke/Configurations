package net.configuration.config;

public class ConfigurationException extends RuntimeException{

    /**
     * Create a new instance of an error explaining what went wrong during config access.
     *
     * @param msg The detailed error message.
     */
    public ConfigurationException(String msg){
        super(msg);
    }

    /**
     * Create a new instance of containing a different error that occurred at runtime.
     *
     * @param other The thrown exception.
     */
    public ConfigurationException(Exception other){
        super(other);
    }

}
