package account.error.customexceptions.users;

public class UserFoundException extends RuntimeException {

    public UserFoundException() {
        super("User exist!");
    }
}