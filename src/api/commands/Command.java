package api.commands;

import java.nio.channels.SelectionKey;

public abstract sealed class Command
    permits BuyCommand, DepositCommand, HelpCommand, ListCommand, LoginCommand, LogoutCommand, RegisterCommand,
    SellCommand, SummaryCommand, SummaryOverallCommand {

    protected static final String EMAIL_REGEX = "[A-Za-z0-9\\p{Punct}]+@[a-z0-9]+.[a-z]*";
    protected static final String NAME_REGEX = "[A-Z][a-z0-9]+";

    protected static final int LOGIN_COMMAND_ARGUMENTS_LENGTH = 2;
    protected static final int REGISTER_COMMAND_ARGUMENTS_LENGTH = 3;
    protected static final int DEPOSIT_COMMAND_ARGUMENTS_LENGTH = 1;
    protected static final int BUY_COMMAND_ARGUMENTS_LENGTH = 2;
    protected static final int SELL_COMMAND_ARGUMENTS_LENGTH = 1;
    protected static final int LIST_SUMMARY_HELP_LOGOUT_COMMAND_ARGUMENTS_LENGTH = 0;

    public abstract String execute(String[] input, SelectionKey key);
}

