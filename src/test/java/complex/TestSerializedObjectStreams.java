package complex;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableType;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class TestSerializedObjectStreams {

    @ParameterizedTest
    @EnumSource(SerializableType.class)
    @DisplayName("Test Stream Serialization")
    void testStreamSerialization(SerializableType type) throws IOException {
        //write a complex object into a file stream and then deserialize it from the input stream
        File file = new File("deleteMe.file");
        assertTrue(file.createNewFile());

        TestObject a = new TestObject(1F);
        SerializedObject obj = type.createEmpty(TestObject.class);
        a.write(obj);
        obj.flush();

        try(FileOutputStream fos = new FileOutputStream(file)){
            obj.writeToStream(fos);
        }

        try(FileInputStream fis = new FileInputStream(file)){
            SerializedObject readObj = SerializedObject.readFromStream(type, TestObject.class, fis);
            Creator<TestObject> creator = Creator.getCreator(TestObject.class);

            //read again
            assertEquals(TestObject.class, readObj.getForClass().orElse(null));
            TestObject read = creator.read(readObj);
            assertEquals(a, read);
        }catch(SerializationException e){
            e.printStackTrace();
            assertTrue(file.delete());
            fail();
        }


        assertTrue(file.delete());

    }

}
