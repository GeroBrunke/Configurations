package complex;

import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;

class TestInheritanceImpl implements TestInheritanceInterface{

    @Override
    public void write(@NotNull SerializedObject dest) {

    }

    @Override
    public @NotNull SerializableObject read(@NotNull SerializedObject src) {
        return null;
    }

    @Override
    public void test() {

    }
}
