package execute.serializable.complex;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableType;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.TupleSerializable;
import net.configuration.serializable.impl.types.SQLSerializedObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestSerializedObject {

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Simple Serializable Serialization")
    void testSimpleSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        TestObject a = new TestObject(1F);
        TestObject b = new TestObject(23);

        obj.setSerializable(a);
        obj.setSerializable("b", b);
        obj.setSerializable(a);

        obj.flush();

        //read again
        assertEquals(a, obj.getSerializable(TestObject.class).orElse(null));
        assertEquals(b, obj.getSerializable("b", TestObject.class).orElse(null));
        assertEquals(a, obj.getSerializable(TestObject.class).orElse(null));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), SQLSerializedObject.getTableName(TestObject.class)));
        }

    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Class Serialization")
    void testClassSerialization(SerializableType type){
        TestObject a = new TestObject(1F);
        SerializedObject obj = type.createEmpty(TestObject.class);
        a.write(obj);

        obj.flush();


        //read again
        assertEquals(TestObject.class, obj.getForClass().orElse(null));
        TestObject read = Creator.readDefault(obj, TestObject.class).orElse(null);
        assertEquals(a, read);
        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }


    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Serialized Object Serialization")
    void testSerializedSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();
        Creator<TestObject> creator = Creator.getCreator(TestObject.class);

        TestObject atest = new TestObject(1F);
        SerializedObject a = type.createEmpty(TestObject.class);
        atest.write(a);
        obj.set(a);

        TestObject btest = new TestObject(8F);
        SerializedObject b = type.createEmpty(TestObject.class);
        btest.write(b);
        obj.set("b", b);

        obj.set(a);
        obj.flush();

        //read again
        Optional<SerializedObject> aOpt1 = obj.get();
        assertTrue(aOpt1.isPresent());
        SerializedObject as = aOpt1.get();
        as.setForClass(TestObject.class);
        assertEquals(atest, creator.read(as));

        Optional<SerializedObject> bOpt = obj.get("b");
        assertTrue(bOpt.isPresent());
        SerializedObject ab = bOpt.get();
        ab.setForClass(TestObject.class);
        assertEquals(btest, creator.read(ab));


        Optional<SerializedObject> aOpt2 = obj.get();
        assertTrue(aOpt2.isPresent());
        SerializedObject as2 = aOpt2.get();
        as2.setForClass(TestObject.class);
        assertEquals(atest, creator.read(as2));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), SQLSerializedObject.getTableName(TestObject.class)));
        }

    }


    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test List Serialization")
    void testListSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();
        //write a list of Serializable objects
        TestObject one = new TestObject(22);
        TestObject two = new TestObject(-91);
        TestObject three = new TestObject(2);
        List<TestObject> a = List.of(one, two, three);

        TestObject oneb = new TestObject(32);
        TestObject twob = new TestObject(-52);
        TestObject threeb = new TestObject(6);
        List<TestObject> b = List.of(oneb, twob, threeb);

        obj.setList(a);
        obj.setList("b", b);
        obj.setList(a);

        obj.flush();

        //read again
        assertArrayEquals(a.toArray(), obj.getList(TestObject.class).orElse(List.of()).toArray());
        assertArrayEquals(b.toArray(), obj.getList("b", TestObject.class).orElse(List.of()).toArray());
        assertArrayEquals(a.toArray(), obj.getList(TestObject.class).orElse(List.of()).toArray());


        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), SQLSerializedObject.getTableName(TestObject.class)));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Map Serialization")
    void testMapSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        Map<TestObject, TestObject> map = new HashMap<>();
        TestObject key1 = new TestObject(1);
        TestObject value1 = new TestObject(-62);
        map.put(key1, value1);

        TestObject key2 = new TestObject(5);
        TestObject value2 = new TestObject(99);
        map.put(key2, value2);

        TestObject key3 = new TestObject(13);
        TestObject value3 = new TestObject(-8);
        map.put(key3, value3);

        obj.setMap(map);
        obj.setMap("b", map);

        obj.flush();

        //read again
        Optional<Map<TestObject, TestObject>> aopt = obj.getMap(TestObject.class, TestObject.class);
        assertTrue(aopt.isPresent());
        for(var e : aopt.get().entrySet()){
            assertEquals(e.getValue(), map.get(e.getKey()));
        }

        Optional<Map<TestObject, TestObject>> bopt = obj.getMap("b", TestObject.class, TestObject.class);
        assertTrue(bopt.isPresent());
        for(var e : bopt.get().entrySet()){
            assertEquals(e.getValue(), map.get(e.getKey()));
        }

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), SQLSerializedObject.getTableName(TestObject.class)));
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), SQLSerializedObject.getTableName(TupleSerializable.class)));
        }

    }

}
