package simple;

import net.configuration.serializable.api.SerializableType;
import net.configuration.serializable.api.SerializedObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestSerializedObject {


    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Byte Serialization")
    void testByteSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();
        byte a = (byte) -4;
        byte b = (byte) 76;

        obj.setByte(a);
        obj.setByte("b", b);
        obj.setByte(a);

        obj.flush();

        //read data again
        assertEquals(a, obj.getByte().orElse((byte) -1));
        assertEquals(b, obj.getByte("b").orElse((byte) -1));
        assertEquals(a, obj.getByte().orElse((byte) -1));
    }

}
