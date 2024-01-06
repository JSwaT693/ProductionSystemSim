package pl.edu.pwr.jswatowski.shared;

import java.io.IOException;
import java.net.Socket;

public abstract class MachineBase extends SocketServerBase {
    protected final String worldIp;
    protected final int worldPort;
    protected int id = 0;

    public MachineBase(String worldIp, int worldPort, int serverPort) {
        this.worldIp = worldIp;
        this.worldPort = worldPort;
        try {
            startServer(serverPort);
            Thread.sleep(500);
            register();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleClient(String[] messageItems) throws IOException {
        switch (messageItems[0]) {
            case "register" -> {
                id = Integer.parseInt(messageItems[1]);
                System.out.println(messageItems[0] + "ed with id: " + id);
            }
            case "unregister" -> {
                stopServer();
                System.out.println(messageItems[0] + " succesful");
            }
            case "move" -> System.out.println("Move succesful");
        }
    }

    public void register() {
        var message = "register," + getServerIP() + "," + getServerPort() + "," + getRole();
        sendMessage(worldIp, worldPort, message);
    }

    public void unregister() {
        var message = "unregister," + id;
        sendMessage(worldIp, worldPort, message);
    }

    public void move() {
        var message = "move," + id;
        sendMessage(worldIp, worldPort, message);
    }

    protected abstract MachineType getRole();
}
