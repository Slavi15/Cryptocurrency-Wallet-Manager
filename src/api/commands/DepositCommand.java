package api.commands;

import api.models.users.User;
import api.models.users.Users;

import java.nio.channels.SelectionKey;

public final class DepositCommand extends Command {

    private final Users users;

    private static final int DEPOSIT_AMOUNT_INDEX = 0;

    public DepositCommand(Users users) {
        this.users = users;
    }

    @Override
    public String execute(String[] input, SelectionKey key) {
        if (input.length != DEPOSIT_COMMAND_ARGUMENTS_LENGTH) {
            return "Usage: deposit-money <amount>";
        }

        return deposit(input[DEPOSIT_AMOUNT_INDEX], key);
    }

    private String deposit(String depositMoney,
                           SelectionKey key) {
        if (key.attachment() == null) {
            return "You must login before making a deposit!";
        }

        try {
            double depositAmount = Double.parseDouble(depositMoney);

            if (Double.compare(depositAmount, 0) <= 0) {
                return "Invalid deposit amount provided!";
            }

            User loggedUser = (User) key.attachment();
            this.users.removeUser(loggedUser);

            loggedUser.depositMoney(depositAmount);
            this.users.addUser(loggedUser);

            return "User " + loggedUser.getUserName() + " successfully added " + depositAmount + " to their wallet!";
        } catch (NullPointerException | NumberFormatException exc) {
            return "Invalid deposit amount provided!";
        }
    }
}
