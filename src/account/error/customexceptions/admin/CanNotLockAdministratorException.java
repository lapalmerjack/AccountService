package account.error.customexceptions.admin;

public class CanNotLockAdministratorException extends RuntimeException {


    public CanNotLockAdministratorException() {
        super("Can't lock the ADMINISTRATOR!");
    }
}
