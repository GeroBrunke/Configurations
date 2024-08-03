package execute.configurable;

import net.configuration.config.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestConfigurable {

    @Test
    @DisplayName("Test Configurable")
    void testConfigurable(){
        ConfigurableTestObject test = new ConfigurableTestObject();
        String path = Objects.requireNonNull(getClass().getResource("/resources/testConfigurable.json")).getFile();
        test.loadConfig(path, false);

        Configuration config = test.getConfig();
        assertTrue(test.isReadOnly("read_only.finalInt"));

        assertEquals(32.21, config.getDouble("height").orElse(-1.0));
        assertEquals(1023, config.getInt("width").orElse(-1));
        assertEquals("This is a Test", config.getString("name").orElse(""));
        assertEquals(87, config.getInt("read_only.finalInt").orElse(-1));
    }

}
