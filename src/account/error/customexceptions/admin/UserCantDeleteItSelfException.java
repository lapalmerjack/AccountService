package account.error.customexceptions.admin;

public class UserCantDeleteItSelfException extends RuntimeException {

    public UserCantDeleteItSelfException() {
        super("Can't remove ADMINISTRATOR role!");
    }
}
