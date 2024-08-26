package net.configuration.serializable.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class field that are annotated with this annotation will be ignored by the field-name finder for every
 * serialized object. Annotating a field with this annotation is equivalent to setting it to transient.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreSerialization {

    String value() default "Ignored";

}
