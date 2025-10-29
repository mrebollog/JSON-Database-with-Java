package server;

public class CommandExecutor {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public Response executeCommand() {
        return command.execute();
    }
}
