package api.commands.type;

public enum CommandType {
    LOGIN("login", 2),
    REGISTER("register", 2),
    DEPOSIT("deposit-money", 1),
    BUY("buy", 2),
    SELL("sell", 1),
    LIST("list-offerings", 2),
    SUMMARY("get-wallet-summary", 0),
    SUMMARY_OVERALL("get-wallet-overall-summary", 0),
    HELP("help", 0),
    LOGOUT("logout", 0);

    private final String command;
    private final int args;

    CommandType(String command, int args) {
        this.command = command;
        this.args = args;
    }

    public String getCommand() {
        return this.command;
    }

    public int getArgs() {
        return this.args;
    }
}
