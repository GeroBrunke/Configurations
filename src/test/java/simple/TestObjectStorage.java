package simple;

import complex.TestObject;
import net.configuration.serializable.api.ObjectStorage;
import net.configuration.serializable.api.SerializableType;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestObjectStorage {

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Object Storage Serializable")
    void testStorage(@NotNull SerializableType type){
        TestObject a = new TestObject(1F);
        TestObject b = new TestObject(23);

        SerializedObject objA = ObjectStorage.serializeObject(a, type).orElseThrow();
        SerializedObject objB = ObjectStorage.serializeObject(b, type).orElseThrow();

        assertEquals(a, ObjectStorage.deserializeObject(objA, TestObject.class).orElse(null));
        assertEquals(b, ObjectStorage.deserializeObject(objB, TestObject.class).orElse(null));
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Object Storage Object")
    void testStorageObject(@NotNull SerializableType type){
        Object a = "Hello World!";

        SerializedObject objA = ObjectStorage.serializeObject(a, type).orElseThrow();
        SerializedObject objB = ObjectStorage.serializeObject(null, type).orElseThrow();

        assertEquals(a, ObjectStorage.deserializeObject(objA, Object.class).orElse(null));
        assertNull(ObjectStorage.deserializeObject(objB, Object.class).orElse(null));

    }

}
