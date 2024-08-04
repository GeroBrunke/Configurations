package execute.serializable.complex;

import net.configuration.serializable.api.*;
import net.configuration.serializable.impl.SimpleCreatorImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.EnumSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestObject implements SerializableObject {

    @SerializationAPI
    @SuppressWarnings("unused")
    private static final Creator<TestObject> CREATOR = new SimpleCreatorImpl<>(TestObject.class);

    private int intValue;

    @IgnoreSerialization
    @SuppressWarnings("unused")
    private final int ignoreValue;

    private float e;
    private boolean boolValue;
    private EnumSelector selector;

    private List<String> list;
    private TestObject complex;
    private Object object;

    @SuppressWarnings("unused")
    private TestObject(){
        this.ignoreValue = -3;
    }

    public TestObject(float uu){
        this.intValue = (int) uu;
        this.ignoreValue = -3;
        this.e = 2.78F;
        this.boolValue = true;
        this.selector = EnumSelector.ORDINAL;
        this.list = List.of("I", "am", "a", "list", "of", "strings");
        this.complex = new TestObject(2);
        this.object = 9;
    }

    public TestObject(int z){
        this.intValue = 9*z;
        this.ignoreValue = -3;
        this.e = 2.78F;
        this.boolValue = true;
        this.selector = EnumSelector.CUSTOM_FIELD;
        this.list = List.of("I", "am", "a", "list", "of", "strings", "in a tree");
        this.complex = null;
        this.object = "Hallo";
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setInt(intValue);
        dest.setFloat(e);
        dest.setBoolean("boolValue", boolValue);
        dest.setEnum(selector);
        dest.setStringList(list);
        dest.setObject("execute", complex);
        dest.setObject("object", object);
    }

    @Override
    public @NotNull TestObject read(@NotNull SerializedObject src) {
        intValue = src.getInt().orElse(-1);
        e = src.getFloat().orElse(-1F);
        boolValue = src.getBoolean("boolValue").orElse(false);
        selector = (EnumSelector) src.getEnum(EnumSelector.class).orElse(null);
        list = (List<String>) src.getStringList().orElse(new ArrayList<>());
        object = src.getObject("object", Object.class).orElse(null);
        complex = src.getObject("execute", TestObject.class).orElse(null);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestObject that)) return false;
        return intValue == that.intValue && ignoreValue == that.ignoreValue && Float.compare(that.e, e) == 0 &&
                boolValue == that.boolValue && selector == that.selector && list.equals(that.list) &&
                Objects.equals(complex, that.complex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intValue, ignoreValue, e, boolValue, selector, list, complex);
    }
}
