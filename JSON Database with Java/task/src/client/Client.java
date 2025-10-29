package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;

public class Client {
    private final Gson gson = new Gson();

    public void run(String[] args) {
        Args arguments = new Args();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        String msgSend = buildMessage(arguments);
        if (msgSend == null) return;

        try (Socket socket = new Socket("127.0.0.1", 34522);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Client started!");
            output.writeUTF(msgSend);
            System.out.println("Sent: " + msgSend);

            String msgRead = input.readUTF();
            System.out.println("Received: " + msgRead);

        } catch (IOException e) {
            System.out.println("Error en el cliente");
        }
    }

    private String buildMessage(Args arguments) {
        if (arguments.getInputFile() != null) {
            String path = System.getProperty("user.dir") + "/src/client/data/" + arguments.getInputFile();
            try (Reader reader = new FileReader(path)) {
                JsonObject fileJson = gson.fromJson(reader, JsonObject.class);
                return gson.toJson(fileJson);
            } catch (IOException e) {
                System.out.println("Error al leer el archivo: " + path);
                return null;
            }
        } else {
            return gson.toJson(arguments);
        }
    }
}