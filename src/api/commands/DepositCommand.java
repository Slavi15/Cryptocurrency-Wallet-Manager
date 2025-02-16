package api.commands;

import api.commands.type.CommandType;
import api.models.users.User;
import api.models.users.Users;

import java.nio.channels.SelectionKey;

public final class DepositCommand extends Command {

    private static final String DEPOSIT_COMMAND_INVALID_USAGE = "Usage: deposit-money <amount>";
    private static final String DEPOSIT_COMMAND_NOT_LOGGED_IN = "You must login before making a deposit!";
    private static final String DEPOSIT_COMMAND_INVALID_AMOUNT = "Invalid deposit amount provided!";
    private static final String DEPOSIT_COMMAND_SUCCESSFUL_OPERATION =
        "User with email %s successfully added %f to their wallet!";

    private final Users users;

    private static final int DEPOSIT_AMOUNT_INDEX = 0;

    public DepositCommand(Users users) {
        this.users = users;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != CommandType.DEPOSIT.getArgs()) {
            return DEPOSIT_COMMAND_INVALID_USAGE;
        }

        return deposit(input[DEPOSIT_AMOUNT_INDEX], key);
    }

    private String deposit(String depositMoney,
                           SelectionKey key) {
        if (key.attachment() == null) {
            return DEPOSIT_COMMAND_NOT_LOGGED_IN;
        }

        try {
            double depositAmount = Double.parseDouble(depositMoney);

            if (Double.compare(depositAmount, 0) <= 0) {
                return DEPOSIT_COMMAND_INVALID_AMOUNT;
            }

            User loggedUser = (User) key.attachment();
            this.users.removeUser(loggedUser);

            loggedUser.depositMoney(depositAmount);
            this.users.addUser(loggedUser);

            return DEPOSIT_COMMAND_SUCCESSFUL_OPERATION.formatted(loggedUser.getEmail(), depositAmount);
        } catch (NullPointerException | NumberFormatException exc) {
            return DEPOSIT_COMMAND_INVALID_AMOUNT;
        }
    }
}
