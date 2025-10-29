package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CommandFactory {
    private final JSONDatabase database;
    private final Gson gson = new Gson();

    public CommandFactory(JSONDatabase database) {
        this.database = database;
    }

    public Command createCommand(String msg) {
        try {
            JsonObject json = gson.fromJson(msg, JsonObject.class);
            String type = json.get("type").getAsString();
            if ("exit".equals(type)) {
                return new ExitCommand();
            }
            List<String> keyPath = parseKey(json.get("key"));

            return switch (type) {
                case "get" -> new GetCommand(database, keyPath);
                case "set" -> new SetCommand(database, keyPath, json.get("value"));
                case "delete" -> new DeleteCommand(database, keyPath);
                default -> () -> Response.error("Unknown command");
            };
        } catch (Exception e) {
            return () -> Response.error("Invalid JSON");
        }
    }

    private List<String> parseKey(JsonElement keyElement) {
        if (keyElement == null) return null;
        if (keyElement.isJsonArray()) {
            List<String> keyPath = new ArrayList<>();
            for (JsonElement el : keyElement.getAsJsonArray()) {
                keyPath.add(el.getAsString());
            }
            return keyPath;
        } else {
            return List.of(keyElement.getAsString());
        }
    }


}