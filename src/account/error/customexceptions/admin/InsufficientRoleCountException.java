package account.error.customexceptions.admin;

public class InsufficientRoleCountException extends RuntimeException {

    public InsufficientRoleCountException() {
        super("The user must have at least one role!");
    }
}
