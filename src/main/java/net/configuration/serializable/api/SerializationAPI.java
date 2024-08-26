package net.configuration.serializable.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Necessary for the creator field valiable of a concrete implementation of a {@link SerializableObject}.<br>
 * For example:<br>
 * <pre>
 * {@code
 *      class MyClass implements SerializableObject {
 *
 *          @SerializationAPI
 *          private static final Creator< MyClass> CREATOR = new Creator< MyClass> {...}
 *
 *          .
 *          .
 *          .
 *
 *      }
 * }
 * </pre>
 *
 * Note that every implementation of the {@link SerializableObject} has to have such a field variable in order
 * for the serialization to work properly.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializationAPI {
}
