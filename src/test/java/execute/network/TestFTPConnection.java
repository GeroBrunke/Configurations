package execute.network;

import com.jcraft.jsch.SftpException;
import net.configuration.main.Main;
import net.configuration.network.FTPConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestFTPConnection {

    //@Test
    @DisplayName("Test Create Connection")
    void testCreateConnection(){
        FTPConnection con = this.createTestConnection();
        assertTrue(con.connect());
        assertTrue(con.isConnected());

        con.disconnect();
        assertFalse(con.isConnected());
    }

    //@Test
    @DisplayName("Test Upload And Download File")
    void testFileManagement() throws SftpException, IOException, URISyntaxException {
        FTPConnection con = this.createTestConnection();
        File localFile = Objects.requireNonNull(this.getLocalTestFile());

        con.connect();
        assertTrue(con.isConnected());

        String remoteFile = "/home/ipi/cloud/backup/testConfigurable.json";
        con.uploadFile(localFile.getPath(), "/home/ipi/cloud/backup");
        assertTrue(con.existsRemoteFile(remoteFile));

        String localDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath();
        File download = con.downloadFile(remoteFile, localDir);
        assertTrue(download.exists());
        Files.delete(download.toPath());

        con.deleteRemoteFile(remoteFile);
        assertFalse(con.existsRemoteFile(remoteFile));

        con.disconnect();
        assertFalse(con.isConnected());

    }

    private FTPConnection createTestConnection(){
        //connect to wsl
        return new FTPConnection("172.28.147.109", 22, "ipi", "test123");
        //return new FTPConnection("localhost", 21, "java", "javatests");
    }

    private File getLocalTestFile(){
        String path = Objects.requireNonNull(getClass().getResource("/resources/testConfigurable.json")).getFile();
        return new File(path);
    }

}
