package account.error.customexceptions.users;

public class MinimumPasswordLengthException extends RuntimeException {

    public MinimumPasswordLengthException() {
        super("Password length must be 12 chars minimum!");
    }
}