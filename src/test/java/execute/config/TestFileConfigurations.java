package execute.config;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

class TestFileConfigurations {

    @Test
    void test(){

    }


    private File loadFile(String name) throws URISyntaxException {
        URL url = TestFileConfigurations.class.getClassLoader().getResource("./test/java/execute/config/resources/" + name);
        Objects.requireNonNull(url);

        return new File(url.toURI());
    }

}

