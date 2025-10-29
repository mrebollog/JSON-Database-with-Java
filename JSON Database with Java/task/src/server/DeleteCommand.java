package server;

import java.util.List;

public class DeleteCommand implements Command {
    private final JSONDatabase database;
    private final List<String> keyPath;

    public DeleteCommand(JSONDatabase database, List<String> keyPath) {
        this.database = database;
        this.keyPath = keyPath;
    }

    @Override
    public Response execute() {
        return database.delete(keyPath);
    }
}
