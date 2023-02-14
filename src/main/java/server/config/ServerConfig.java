package server.config;

import files.ServerConfigFile;

import java.io.Serializable;

public class ServerConfig implements Serializable {

    private ServerMode mode;
    private boolean showHistory;
    private int port;
    private String pass;

    public ServerConfig(ServerMode mode, boolean showHistory, int port, String pass) {
        this.mode = mode;
        this.showHistory = showHistory;
        this.port = port;
        this.pass = pass;
    }

    private void setUp(ServerConfig sc) {
    }

    public static ServerConfig factory() {
        return ServerConfigFile.readServerConfig();
    }

    public ServerMode getMode() {
        return mode;
    }

    public void setMode(ServerMode mode) {
        this.mode = mode;
    }

    public boolean isShowHistory() {
        return showHistory;
    }

    public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    //Chat Condition
    //Show Last Message to new User
    //Max members
    //Special Users Can join
    //Server Lock (Need Password)
}
