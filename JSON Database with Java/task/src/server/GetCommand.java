package server;

import java.util.List;

public class GetCommand implements Command {
    private final JSONDatabase database;
    private final List<String> keyPath;

    public GetCommand(JSONDatabase database, List<String> keyPath) {
        this.database = database;
        this.keyPath = keyPath;
    }

    @Override
    public Response execute() {
        return database.get(keyPath);
    }
}

