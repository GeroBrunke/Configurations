package net.configuration.serializable.api;

public class SerializationException extends RuntimeException{

    /**
     * Create a new instance of an error explaining what went wrong during serialization.
     *
     * @param msg The detailed error message.
     */
    public SerializationException(String msg){
        super(msg);
    }

    /**
     * Create a new instance of containing a different error that occurred at runtime.
     *
     * @param other The thrown exception.
     */
    public SerializationException(Exception other){
        super(other);
    }

}
