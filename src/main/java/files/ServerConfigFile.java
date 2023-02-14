package files;

import server.config.ServerConfig;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerConfigFile {
    private static final Path configPath = Paths.get( "Config"+File.separator + "serverConfig.txt");

    public static void writeConfig(ServerConfig config) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(configPath.toString()))) {
            out.writeObject(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServerConfig readServerConfig() {
        try {
            new File(configPath.toString()).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(configPath.toString()))) {
            return (ServerConfig) in.readObject();
        } catch (EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
