package pl.edu.pwr.jswatowski.shared;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public abstract class SocketServerBase {
    private ServerSocket serverSocket;
    private String serverIP;
    private int serverPort;
    private boolean serverIsWorking = false;
    protected Thread serverThread;

    protected void startServer(int port) throws IOException {
        serverPort = port;
        Runnable thread = () -> {
            try {
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(5000);
                serverIsWorking = true;
                serverIP = serverSocket.getInetAddress().getHostAddress();
                while (serverIsWorking) {
                    try {
                        var clientSocket = serverSocket.accept();
                        var br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        var message = br.readLine();
                        System.out.println(message);
                        var messageItems = message.split(",");
                        if (messageItems.length == 0) {
                            continue;
                        }
                        handleClient(messageItems);
                    } catch (SocketTimeoutException | SocketException ignored) {}
                }
                System.out.println("Server Thread finished");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        serverThread = new Thread(thread);
        serverThread.start();
    }

    protected void stopServer() throws IOException {
        if (serverIsWorking && serverSocket != null) {
            serverIsWorking = false;
            serverSocket.close();
        }
    }

    protected abstract void handleClient(String[] messageItems) throws IOException;

    protected static void sendMessage(String host, int port, String message) {
        try (var socket = new Socket(host, port)) {
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            pw.println(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }
}
