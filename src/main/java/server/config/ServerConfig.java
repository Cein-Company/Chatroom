package server.config;

import files.MyServerConfigFile;

import java.io.Serializable;

public class ServerConfig implements Serializable {

    private String ipAddress;
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
        return MyServerConfigFile.readServerConfig();
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    //Chat Condition
    //Show Last Message to new User
    //Max members
    //Special Users Can join
    //Server Lock (Need Password)
}
