package api.exceptions;

public abstract sealed class MyException extends RuntimeException
    permits CoinAPIException {

    public MyException(String message) {
        super(message);
    }

    public MyException(String message, Throwable cause) {
        super(message, cause);
    }
}
