package net.configuration.serializable.impl.types;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class SQLSerializedObject extends JsonSerializedObject {

    public SQLSerializedObject(@NotNull Class<?> clazz) {
        super(clazz);
    }

    public SQLSerializedObject(byte @NotNull [] data, @NotNull Class<?> forClass) {

    }

    public SQLSerializedObject(@NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);
    }

    @SuppressWarnings("unused")
    protected SQLSerializedObject() {
        super();
    }

}
