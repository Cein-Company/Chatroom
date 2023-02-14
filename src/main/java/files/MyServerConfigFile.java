package files;

import server.config.ServerConfig;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyServerConfigFile extends Files {
    private static final String configPath = path + "\\serverConfig.txt";

    public static void writeConfig(ServerConfig config) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(configPath))) {
            out.writeObject(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServerConfig readServerConfig() {
        try {
            new File(configPath).createNewFile();
        } catch (IOException e) {
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(configPath))) {
            return (ServerConfig) in.readObject();
        } catch (EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
