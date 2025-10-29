package server;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final CommandFactory factory;

    public ClientHandler(Socket socket, CommandFactory factory) {
        this.socket = socket;
        this.factory = factory;
    }

    @Override
    public void run() {
        try (socket;
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            String msgRead = input.readUTF();
            System.out.println("Received: " + msgRead);

            Command command = factory.createCommand(msgRead);
            CommandExecutor executor = new CommandExecutor();
            executor.setCommand(command);
            Response response = executor.executeCommand();
            String jsonResponse = new Gson().toJson(response);
            output.writeUTF(jsonResponse);
            System.out.println("Sent: " + jsonResponse);

            if (command instanceof ExitCommand) {
                throw new RuntimeException("Exit command received");
            }
        } catch (IOException e) {
            System.out.println("Error al manejar cliente");
        }
    }
}