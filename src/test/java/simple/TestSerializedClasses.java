package simple;

import net.configuration.serializable.api.SerializableType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TestSerializedClasses {

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test")
    void testSerializableClass(@NotNull SerializableType type){

    }

}
