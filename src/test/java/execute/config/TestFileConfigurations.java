package execute.config;

import execute.serializable.complex.TestObject;
import net.configuration.config.Configuration;
import net.configuration.config.FileConfiguration;
import net.configuration.serializable.api.SerializableType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestFileConfigurations {

    private static final String BOOLEAN = "boolean";
    private static final String INTEGER = "integer";
    private static final String SHORT = "short";
    private static final String CHAR = "char";
    private static final String FLOAT = "float";
    private static final String DOUBLE = "double";
    private static final String LONG = "long";
    private static final String BYTE = "byte";
    private static final String STRING = "string";
    private static final String COLLECTION = "collection";
    private static final String ARRAY = "array";

    private final Map<SerializableType, String> fileNames = this.getFileNames();

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Primitive Config")
    void testPrimitiveConfig(@NotNull SerializableType type) throws URISyntaxException {
        File file = this.loadFile(this.fileNames.get(type));
        assertTrue(file.exists());

        Configuration config = FileConfiguration.loadConfig(file);
        config.setBoolean(BOOLEAN, false);
        config.setInt(INTEGER, -7);
        config.setShort(SHORT, (short) 182);
        config.setChar(CHAR, 'p');
        config.setFloat(FLOAT, 0.0171892F);
        config.setDouble(DOUBLE, Math.E);
        config.setLong(LONG, -361281L);
        config.setByte(BYTE, (byte) 0x5D);
        config.setString(STRING, "Test String value for primitive");


        //read again and check if the values were updated
        assertFalse(config.getBoolean(BOOLEAN).orElseThrow());
        assertEquals(-7, config.getInt(INTEGER).orElseThrow());
        assertEquals(182, (short) config.getShort(SHORT).orElseThrow());
        assertEquals('p', config.getChar(CHAR).orElseThrow());
        assertEquals(0.0171892F, config.getFloat(FLOAT).orElseThrow(), 0.0001);
        assertEquals(Math.E, config.getDouble(DOUBLE).orElseThrow(), 0.0001);
        assertEquals(-361281L, config.getLong(LONG).orElseThrow());
        assertEquals(0x5D, (byte) config.getByte(BYTE).orElseThrow());
        assertEquals("Test String value for primitive", config.getString(STRING).orElseThrow());


        this.restoreDefaultValues(config);
        this.checkForDefaultValues(config);

    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Collection Config")
    void testListConfig(@NotNull SerializableType type) throws URISyntaxException {
        File file = this.loadFile(this.fileNames.get(type));
        assertTrue(file.exists());

        Configuration config = FileConfiguration.loadConfig(file);

        //set and get all values for this config
        Integer[] newArray = new Integer[]{19,8,7,6};
        List<Integer> newList = List.of(19,8,7,6);
        config.setArray(ARRAY, newArray);
        config.setList(COLLECTION, newList);

        //Read again and check if updated

        List<Integer> list = config.getList(COLLECTION, Integer.class).orElseThrow();
        Integer[] array = config.getArray(ARRAY, Integer.class).orElseThrow();

        assertArrayEquals(newArray, array);
        assertArrayEquals(newList.toArray(), list.toArray());

        this.restoreDefaultValues(config);
        this.checkForDefaultValues(config);


    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Complex Config")
    void testComplexConfig(@NotNull SerializableType type) throws URISyntaxException {
        File file = this.loadFile(this.fileNames.get(type));
        assertTrue(file.exists());
        Configuration config = FileConfiguration.loadConfig(file);

        TestObject obj = new TestObject(1.1F);
        String val = "Hello World";
        int z = 9;

        config.set(STRING, val);
        config.set("complex", obj);
        config.set(INTEGER, z);


        //Read again and check if updated
        assertEquals(val, config.get(STRING, String.class).orElseThrow());
        assertEquals(obj, config.get("complex", TestObject.class).orElseThrow());
        assertEquals(z, config.get(INTEGER, Integer.class).orElseThrow());

        this.restoreDefaultValues(config);
        this.checkForDefaultValues(config);

    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Complex List Config")
    void testComplexListConfig(@NotNull SerializableType type) throws URISyntaxException {
        File file = this.loadFile(this.fileNames.get(type));
        assertTrue(file.exists());

        Configuration config = FileConfiguration.loadConfig(file);

        String val = "Hello World";
        int z = 9;
        TestObject one = new TestObject(22);
        TestObject two = new TestObject(-91);
        TestObject three = new TestObject(2);
        List<TestObject> a = List.of(one, two, three);


        config.set(STRING, val);
        config.setList("complexList", a);
        config.set(INTEGER, z);


        //Read again and check if updated
        assertEquals(val, config.get(STRING, String.class).orElseThrow());
        assertArrayEquals(a.toArray(), config.getList("complexList", TestObject.class).orElseThrow().toArray());
        assertEquals(z, config.get(INTEGER, Integer.class).orElseThrow());

        this.restoreDefaultValues(config);
        this.checkForDefaultValues(config);
    }

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Reload Config")
    void testReloadConfig(@NotNull SerializableType type) throws URISyntaxException {
        File file = this.loadFile(this.fileNames.get(type));
        assertTrue(file.exists());

        Configuration config = FileConfiguration.loadConfig(file);

        config.setBoolean(BOOLEAN, false);
        config.setInt(INTEGER, -7);
        config.setShort(SHORT, (short) 183);
        config.setChar(CHAR, 'p');
        config.setFloat(FLOAT, 0.0171892F);
        config.setDouble(DOUBLE, Math.PI);
        config.setLong(LONG, -361281L);
        config.setByte(BYTE, (byte) 0x5D);
        config.setString(STRING, "Test String Reloading");

        assertTrue(config.reload());

        this.checkForDefaultValues(config);


    }

    private void checkForDefaultValues(Configuration config) {
        assertTrue(config.getBoolean(BOOLEAN).orElseThrow());
        assertEquals(5, config.getInt(INTEGER).orElseThrow());
        assertEquals(-3, (short) config.getShort(SHORT).orElseThrow());
        assertEquals('d', config.getChar(CHAR).orElseThrow());
        assertEquals(3.14, config.getFloat(FLOAT).orElseThrow(), 0.0001);
        assertEquals(-0.271, config.getDouble(DOUBLE).orElseThrow(), 0.0001);
        assertEquals(3761281901L, config.getLong(LONG).orElseThrow());
        assertEquals(98, (byte) config.getByte(BYTE).orElseThrow());
        assertEquals("Hello World", config.getString(STRING).orElseThrow());
    }

    private void restoreDefaultValues(Configuration config){
        config.setBoolean(BOOLEAN, true);
        config.setInt(INTEGER, 5);
        config.setShort(SHORT, (short) -3);
        config.setChar(CHAR, 'd');
        config.setFloat(FLOAT, 3.14F);
        config.setDouble(DOUBLE, -0.271);
        config.setLong(LONG, 3761281901L);
        config.setByte(BYTE, (byte) 98);
        config.setString(STRING, "Hello World");

        Integer[] newArray = new Integer[]{1,2,3,4};
        List<Integer> newList = List.of(newArray);
        config.setArray(ARRAY, newArray);
        config.setList(COLLECTION, newList);

        config.save();
    }


    private File loadFile(String name) throws URISyntaxException {
        URL url = getClass().getResource("/resources/" + name);
        Objects.requireNonNull(url);

        return new File(url.toURI());
    }

    private Map<SerializableType, String> getFileNames() {
        Map<SerializableType, String> fileNames = new HashMap<>();
        fileNames.put(SerializableType.BYTE, "testConfigByte.data");
        fileNames.put(SerializableType.JSON, "testConfigJson.json");
        fileNames.put(SerializableType.PROPERTIES, "testConfigProperties.properties");
        fileNames.put(SerializableType.TEXT, "testConfigText.txt");
        fileNames.put(SerializableType.XML, "testConfigXml.xml");
        fileNames.put(SerializableType.YAML, "testConfigYml.yml");

        return fileNames;
    }
}

