package api.models.input;

import java.util.Arrays;

public record Input(String command, String[] args) {

    private static final String SEPARATOR = " +";

    public static Input of(String input) {
        String[] tokens = input.split(SEPARATOR);

        return new Input(
            tokens[0],
            Arrays.stream(tokens, 1, tokens.length).toArray(String[]::new)
        );
    }
}
