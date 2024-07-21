package execute.complex;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializationAPI;
import net.configuration.serializable.impl.GenericSerializable;
import net.configuration.serializable.impl.SimpleCreatorImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class TestGenericObject<T> extends GenericSerializable<T> {

    @SerializationAPI
    @SuppressWarnings({"unused", "rawtypes"})
    private static final Creator<TestGenericObject> CREATOR = new SimpleCreatorImpl<>(TestGenericObject.class);

    public TestGenericObject(@NotNull T value){
        super(value);
    }

    @SuppressWarnings("unused")
    public TestGenericObject(){} //called via reflection

    public T getValue(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestGenericObject<?> that)) return false;
        return Objects.equals(value, that.value) && Objects.equals(classOfT, that.classOfT);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, classOfT);
    }
}
