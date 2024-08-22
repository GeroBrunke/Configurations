package execute.serializable.complex;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableType;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.types.SQLSerializedObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestSpecialObjects {


    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Multiple Inheritance Serialization")
    void testMultipleInheritance(@NotNull SerializableType type){
        TestInheritanceInterface inherit = new TestInheritanceImpl("This is a test.");
        inherit.test();
        SerializedObject obj = type.createEmpty(TestInheritanceInterface.class);
        inherit.write(obj);
        obj.flush();


        Creator<TestInheritanceImpl> creator = Creator.getCreator(TestInheritanceImpl.class);
        TestInheritanceInterface read = creator.read(obj);

        assertEquals(inherit, read);

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }

    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Generic Serialization")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void testGenerics(@NotNull SerializableType type){
        TestGenericObject<String> value = new TestGenericObject<>("Hallo World!");
        SerializedObject obj = type.createEmpty(TestInheritanceInterface.class);
        value.write(obj);
        obj.flush();


        Creator<TestGenericObject> creator = Creator.getCreator(TestGenericObject.class);
        TestGenericObject<String> read = (TestGenericObject<String>) creator.read(obj);

        assertEquals(value.getValue(), read.getValue());
        assertEquals(value, read);

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }

    }


}
