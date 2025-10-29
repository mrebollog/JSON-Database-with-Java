package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final JSONDatabase database = new JSONDatabase();
    private final CommandFactory factory = new CommandFactory(database);
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile boolean exit = false;
    private ServerSocket serverSocket;

    public void run() {
        try {
            startServer();
            acceptClients();
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor");
        } finally {
            shutdown();
        }
    }

    private void startServer() throws IOException {
        serverSocket = new ServerSocket(34522);
        System.out.println("Server started!");
    }

    private void acceptClients() {
        while (!exit) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, factory);
                executor.submit(() -> handleClient(handler));
            } catch (IOException e) {
                if (!exit) System.out.println("Error en el servidor");
                closeServerSocket();
            }
        }
    }

    private void handleClient(ClientHandler handler) {
        try {
            handler.run();
        } catch (RuntimeException e) {
            System.out.println("Apagando servidor: " + e.getMessage());
            exit = true;
            closeServerSocket();
        }
    }

    private void closeServerSocket() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                System.out.println("Error al cerrar el servidor");
            }
        }
    }

    private void shutdown() {
        executor.shutdown();
    }
}
