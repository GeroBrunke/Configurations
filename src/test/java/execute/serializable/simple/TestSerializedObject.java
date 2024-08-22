package execute.serializable.simple;

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

import static org.junit.jupiter.api.Assertions.*;

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

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Short Serialization")
    void testShortSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        short a = (short) -7163;
        short b = (short) 2312;

        obj.setShort(a);
        obj.setShort("b", b);
        obj.setShort(a);

        obj.flush();

        //read data again
        assertEquals(a, obj.getShort().orElse((short) -1));
        assertEquals(b, obj.getShort("b").orElse((short) -1));
        assertEquals(a, obj.getShort().orElse((short) -1));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Int Serialization")
    void testIntSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        int a = 82171;
        int b = -81682;

        obj.setInt(a);
        obj.setInt("b", b);
        obj.setInt(a);

        obj.flush();

        //read data again
        assertEquals(a, obj.getInt().orElse(-1));
        assertEquals(b, obj.getInt("b").orElse(-1));
        assertEquals(a, obj.getInt().orElse(-1));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Long Serialization")
    void testLongSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        long a = -4L;
        long b = System.currentTimeMillis();

        obj.setLong(a);
        obj.setLong("b", b);
        obj.setLong(a);

        obj.flush();

        //read data again
        assertEquals(a, obj.getLong().orElse(-1L));
        assertEquals(b, obj.getLong("b").orElse(-1L));
        assertEquals(a, obj.getLong().orElse(-1L));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Char Serialization")
    void testCharSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        char a = '7';
        char b = 'ß';

        obj.setChar(a);
        obj.setChar("b", b);
        obj.setChar(a);

        obj.flush();

        //read data again
        assertEquals(a, obj.getChar().orElse((char) -1));
        assertEquals(b, obj.getChar("b").orElse((char) -1));
        assertEquals(a, obj.getChar().orElse((char) -1));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Float Serialization")
    void testFloatSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        float a = -23761.32F;
        float b = 3.141F;

        obj.setFloat(a);
        obj.setFloat("b", b);
        obj.setFloat(a);

        obj.flush();

        //read data again
        assertEquals(a, obj.getFloat().orElse(-1F));
        assertEquals(b, obj.getFloat("b").orElse(-1F));
        assertEquals(a, obj.getFloat().orElse(-1F));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Double Serialization")
    void testDoubleSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        double a = -1*Math.sqrt(2);
        double b = Math.E;

        obj.setDouble(a);
        obj.setDouble("b", b);
        obj.setDouble(a);

        obj.flush();

        //read data again
        assertEquals(a, obj.getDouble().orElse(-1D));
        assertEquals(b, obj.getDouble("b").orElse(-1D));
        assertEquals(a, obj.getDouble().orElse(-1D));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Boolean Serialization")
    void testBooleanSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        boolean a = true;
        boolean b = false;

        obj.setBoolean(a);
        obj.setBoolean("b", b);
        obj.setBoolean(a);

        obj.flush();

        //read data again
        assertEquals(a, obj.getBoolean().orElse(!a));
        assertEquals(b, obj.getBoolean("b").orElse(!b));
        assertEquals(a, obj.getBoolean().orElse(!a));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test String Serialization")
    void testStringSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        String a = "Hello";
        String b = "World!";

        obj.setString(a);
        obj.setString("b", b);
        obj.setString(a);

        obj.flush();

        //read data again
        assertEquals(a, obj.getString().orElse(""));
        assertEquals(b, obj.getString("b").orElse(""));
        assertEquals(a, obj.getString().orElse(""));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Null Serialization")
    void testNullSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        obj.setNull();
        obj.setNull("b");
        obj.setNull();

        obj.flush();

        //read data again
        assertTrue(obj.getNull().isPresent());
        assertTrue(obj.getNull("b").isPresent());
        assertTrue(obj.getNull().isPresent());

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Enum Serialization")
    void testEnumSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        SerializableType a = SerializableType.BYTE;
        SerializableType b = SerializableType.PROPERTIES;

        obj.setEnum(a);
        obj.setEnum("b", b);
        obj.setEnum(a);

        obj.flush();

        //read data again
        assertEquals(a, obj.getEnum(SerializableType.class).orElse(null));
        assertEquals(b, obj.getEnum("b", SerializableType.class).orElse(null));
        assertEquals(a, obj.getEnum(SerializableType.class).orElse(null));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Int List Serialization")
    void testIntListSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        List<Integer> a = List.of(1,2,3,4,-4);
        List<Integer> b = List.of(10,9,8,7,6);

        obj.setIntList(a);
        obj.setIntList("b", b);
        obj.setIntList(a);

        obj.flush();

        //read data again
        assertArrayEquals(a.toArray(), obj.getIntList().orElse(List.of()).toArray());
        assertArrayEquals(b.toArray(), obj.getIntList("b").orElse(List.of()).toArray());
        assertArrayEquals(a.toArray(), obj.getIntList().orElse(List.of()).toArray());

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Byte List Serialization")
    void testByteListSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        List<Byte> a = List.of((byte) 1,(byte) 2,(byte) 3,(byte) 4,(byte) -4);
        List<Byte> b = List.of((byte) 10,(byte) 9,(byte) 8,(byte) 7,(byte) 6);


        obj.setByteList(a);
        obj.setByteList("b", b);
        obj.setByteList(a);

        obj.flush();


        //read data again
        assertArrayEquals(a.toArray(), obj.getByteList().orElse(List.of()).toArray());
        assertArrayEquals(b.toArray(), obj.getByteList("b").orElse(List.of()).toArray());
        assertArrayEquals(a.toArray(), obj.getByteList().orElse(List.of()).toArray());

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Double List Serialization")
    void testDoubleListSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        List<Double> a = List.of(1.4,2.1,3.9,4.0,-4.2);
        List<Double> b = List.of(10D,9.0,8.7,7.2,6.33);

        obj.setDoubleList(a);
        obj.setDoubleList("b", b);
        obj.setDoubleList(a);

        obj.flush();

        //read data again
        assertArrayEquals(a.toArray(), obj.getDoubleList().orElse(List.of()).toArray());
        assertArrayEquals(b.toArray(), obj.getDoubleList("b").orElse(List.of()).toArray());
        assertArrayEquals(a.toArray(), obj.getDoubleList().orElse(List.of()).toArray());

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Long List Serialization")
    void testLongListSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        List<Long> a = List.of(1L,2L,3L,4L,-4L);
        List<Long> b = List.of(10L,9L,8L,7L,6L);

        obj.setLongList(a);
        obj.setLongList("b", b);
        obj.setLongList(a);

        obj.flush();

        //read data again
        assertArrayEquals(a.toArray(), obj.getLongList().orElse(List.of()).toArray());
        assertArrayEquals(b.toArray(), obj.getLongList("b").orElse(List.of()).toArray());
        assertArrayEquals(a.toArray(), obj.getLongList().orElse(List.of()).toArray());

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test String List Serialization")
    void testStringListSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        List<String> a = List.of("Hello", "World", "out", "there!");
        List<String> b = List.of("I", "was", "serialized");

        obj.setStringList(a);
        obj.setStringList("b", b);
        obj.setStringList(a);

        obj.flush();

        //read data again
        assertArrayEquals(a.toArray(), obj.getStringList().orElse(List.of()).toArray());
        assertArrayEquals(b.toArray(), obj.getStringList("b").orElse(List.of()).toArray());
        assertArrayEquals(a.toArray(), obj.getStringList().orElse(List.of()).toArray());

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Array Serialization")
    void testArraySerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        String[] a = new String[]{"Hello", "World", "out", "there!"};
        String[] b = new String[]{"I", "was", "serialized"};

        obj.setArray(a);
        obj.setArray("b", b);
        obj.setArray(a);

        obj.flush();

        //read data again
        assertArrayEquals(a, obj.getArray(String.class).orElse(new String[0]));
        assertArrayEquals(b, obj.getArray("b", String.class).orElse(new String[0]));
        assertArrayEquals(a, obj.getArray(String.class).orElse(new String[0]));

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
        }
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Map Serialization")
    void testMapSerialization(SerializableType type){
        SerializedObject obj = type.createEmpty();

        Map<Integer, String> a = new HashMap<>();
        a.put(1, "Hello");
        a.put(-5, "World");
        a.put(12, "I am");
        a.put(88, "here!");

        Map<String, Double> b = new HashMap<>();
        b.put("Test", Math.E);
        b.put("Key String", 7626.361281);
        b.put("My name ist Test", -9.76276);

        obj.setMap(a);
        obj.setMap("b", b);
        obj.setMap(a);

        obj.flush();

        //read data again
        Map<Integer, String> firstA = obj.getMap(Integer.class, String.class).orElse(new HashMap<>());
        assertEquals(a.size(), firstA.size());

        Map<String, Double> firstB = obj.getMap("b", String.class, Double.class).orElse(new HashMap<>());
        assertEquals(b.size(), firstB.size());

        Map<Integer, String> secondA = obj.getMap(Integer.class, String.class).orElse(new HashMap<>());
        assertEquals(a.size(), secondA.size());

        for(var e : a.entrySet()){
            assertEquals(e.getValue(), firstA.get(e.getKey()));
            assertEquals(e.getValue(), secondA.get(e.getKey()));
        }

        for(var e : b.entrySet()){
            assertEquals(e.getValue(), firstB.get(e.getKey()));
        }

        if(obj instanceof SQLSerializedObject sql){
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), sql.getTableName()));
            assertTrue(SQLSerializedObject.deleteTable(sql.getConnection(), SQLSerializedObject.getTableName(TupleSerializable.class)));
        }

    }
}
