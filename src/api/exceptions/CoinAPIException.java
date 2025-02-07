package api.exceptions;

public final class CoinAPIException extends MyException {

    public CoinAPIException(String message) {
        super(message);
    }

    public CoinAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
