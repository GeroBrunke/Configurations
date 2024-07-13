package complex;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationAPI;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.SimpleCreatorImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class TestGenericObject<T> implements SerializableObject {

    @SerializationAPI
    @SuppressWarnings({"unused", "rawtypes"})
    private static final Creator<TestGenericObject> CREATOR = new SimpleCreatorImpl<>(TestGenericObject.class);

    private T value;
    private String classOfT;

    public TestGenericObject(@NotNull T value){
        this.classOfT = value.getClass().getName();
        this.value = value;
    }

    @SuppressWarnings("unused")
    public TestGenericObject(){} //called via reflection

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString("class", classOfT);
        dest.setObject("value", value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull TestGenericObject<T> read(@NotNull SerializedObject src) {
        try{
            this.classOfT = src.getString("class").orElse(null);
            this.value = (T) src.getObject("value", Class.forName(classOfT)).orElse(null);
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        return this;
    }

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
