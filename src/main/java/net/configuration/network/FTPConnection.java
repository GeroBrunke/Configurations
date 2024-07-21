package net.configuration.network;

import com.jcraft.jsch.*;
import net.configuration.serializable.api.*;
import net.configuration.serializable.impl.SimpleCreatorImpl;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FTPConnection implements SerializableObject {

    @SerializationAPI
    @SuppressWarnings("unused")
    private static final Creator<FTPConnection> CREATOR = new SimpleCreatorImpl<>(FTPConnection.class);

    private String host;
    private int port;
    private String user;
    private String password;

    @IgnoreSerialization
    private transient ChannelSftp channel;

    /**
     * Create a new {@link FTPConnection} instance to the given remote server.
     *
     * @param host The remote host name.
     * @param port The FTP port, default 22.
     * @param user The username for verification on the remote device.
     * @param password The password for this username.
     */
    public FTPConnection(@NotNull String host, int port, @NotNull String user, @NotNull String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @SuppressWarnings("unused")
    private FTPConnection(){} //Hide implicit

    /**
     * Connect to the remote device via an FTP connection.
     *
     * @return True if the connection could be established.
     */
    public boolean connect(){
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(this.user, this.host, this.port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(this.password);
            session.connect();

            Channel c = session.openChannel("sftp");
            c.connect();

            this.channel = (ChannelSftp) c;
            return true;
        } catch (JSchException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Close the active connection to the remote device.
     */
    public void disconnect(){
        if(this.isConnected()){
            this.channel.disconnect();
        }
    }

    /**
     * @return If this instance is currently connected to the remote device.
     */
    public boolean isConnected(){
        return channel != null && channel.isConnected();
    }

    /**
     * Check if the file at the given path exists on the remote device. This method throws an  {@link IllegalStateException}
     * if an exception with state not equal to {@link ChannelSftp#SSH_FX_NO_SUCH_FILE} occurs.
     *
     * @param remotePath The path to check for existence.
     * @return True if the remote file or directory exists on the given path.
     */
    public boolean existsRemoteFile(@NotNull String remotePath){
        try{
            this.channel.stat(remotePath);
            return true;
        }catch (SftpException e){
            if(e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE)
                return false;

            throw new IllegalStateException(e);
        }
    }

    /**
     * Upload the given local file to the remote device to the given target location.
     *
     * @param localFile The file to upload.
     * @param remoteDir The remote directory to load the file into.
     * @throws SftpException If the file could not be uploaded.
     */
    public void uploadFile(@NotNull String localFile, @NotNull String remoteDir) throws SftpException {
        this.channel.put(localFile, remoteDir, ChannelSftp.OVERWRITE);
    }

    /**
     * Download the given remote file to the local device at given local path.
     *
     * @param remoteFile The file to download.
     * @param localDir The local directory to save the file into.
     * @return The {@link File} reference to the downloaded file.
     * @throws SftpException If the file could not be downloaded.
     */
    public File downloadFile(@NotNull String remoteFile, @NotNull String localDir) throws SftpException {
        this.channel.get(remoteFile, localDir);
        String filename = FilenameUtils.getName(remoteFile);
        File file = new File(localDir + File.separator + filename);
        if(!file.exists())
            throw new IllegalStateException("Downloaded file does not exists locally.");

        return file;
    }

    /**
     * Delete the given remote file on the remote device. Cannot be undone. This method will raise an exception if the
     * file cannot be deleted.
     *
     * @param remoteFile The remote file to delete.
     * @throws SftpException If the file could not be deleted.
     */
    public void deleteRemoteFile(@NotNull String remoteFile) throws SftpException {
        this.channel.rm(remoteFile);
    }

    @Override
    public void write(@NotNull SerializedObject dest) {
        dest.setString(this.host);
        dest.setInt(this.port);
        dest.setString(this.user);
        dest.setString(this.password);
    }

    @Override
    public @NotNull FTPConnection read(@NotNull SerializedObject src) {
        this.host = src.getString().orElse("");
        this.port = src.getInt().orElse(-1);
        this.user = src.getString().orElse("");
        this.password = src.getString().orElse("");

        return this;
    }
}
