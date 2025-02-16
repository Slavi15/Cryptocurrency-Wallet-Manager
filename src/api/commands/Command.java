package api.commands;

import java.nio.channels.SelectionKey;

public abstract sealed class Command
    permits BuyCommand, DepositCommand, HelpCommand, ListCommand, LoginCommand, LogoutCommand, RegisterCommand,
    SellCommand, SummaryCommand, SummaryOverallCommand {

    protected static final String EMAIL_REGEX = "[A-Za-z0-9\\p{Punct}]+@[a-z0-9]+.[a-z]*";

    public abstract String execute(String[] input, SelectionKey key);
}
