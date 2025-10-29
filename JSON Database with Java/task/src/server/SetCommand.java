package server;

import com.google.gson.JsonElement;

import java.util.List;

public class SetCommand implements Command {
    private final JSONDatabase database;
    private final List<String> keyPath;
    private final JsonElement value;

    public SetCommand(JSONDatabase database, List<String> keyPath, JsonElement value) {
        this.database = database;
        this.keyPath = keyPath;
        this.value = value;
    }

    @Override
    public Response execute() {
        return database.set(keyPath, value);
    }
}
