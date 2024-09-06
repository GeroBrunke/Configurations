package net.configuration.serializable.impl;

import net.configuration.serializable.api.*;
import org.jetbrains.annotations.NotNull;

public class NullSerializable implements SerializableObject {

    public static final String CODON = "null";

    @SerializationAPI
    @SuppressWarnings("unused")
    private static final Creator<NullSerializable> CREATOR = new SimpleCreatorImpl<>(NullSerializable.class);

    private String value;
    private String fieldName;

    public NullSerializable(@NotNull String fieldName){
        this.fieldName = fieldName;
        this.value = CODON;
    }

    @SuppressWarnings("unused") //called via reflection API
    private NullSerializable(){} //Hide implicit

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString(this.fieldName, this.value);
    }

    @Override
    public @NotNull NullSerializable read(@NotNull SerializedObject src) {
        this.value = src.getString(this.fieldName).orElse("INVALID");
        return this;
    }

    /**
     * Check if the mapped value is a placeholder for a null object. If the value is anything different from
     * the specific null placeholder, then an exception will be thrown.
     *
     * @return null if the value is the actual null placeholder.
     */
    public Object getValue(){
        if(this.value.equals("INVALID"))
            return null;

        throw new SerializationException("Invalid null code found.");
    }
}
