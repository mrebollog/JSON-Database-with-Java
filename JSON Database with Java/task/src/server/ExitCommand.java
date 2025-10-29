package server;

public class ExitCommand implements Command {
    @Override
    public Response execute() {
        return Response.ok();
    }
}

