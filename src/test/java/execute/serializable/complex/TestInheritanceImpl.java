package execute.serializable.complex;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializationAPI;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.SimpleCreatorImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class TestInheritanceImpl implements TestInheritanceInterface{

    @SerializationAPI
    @SuppressWarnings("unused")
    private static final Creator<TestInheritanceImpl> CREATOR = new SimpleCreatorImpl<>(TestInheritanceImpl.class);

    private String text;

    public TestInheritanceImpl(String text){
        this.text = text;
    }

    @SuppressWarnings("unused") //Called via reflection API
    public TestInheritanceImpl(){} //Hide implicit

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString(text);
    }

    @Override
    public @NotNull TestInheritanceImpl read(@NotNull SerializedObject src) {
        this.text = src.getString().orElse("");
        return this;
    }

    @Override
    public void test() {
        text += " Test Method invoked!";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestInheritanceImpl that)) return false;
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
