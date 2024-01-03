package account.error.customexceptions.admin;

public class UserRoleExists extends RuntimeException {

    public UserRoleExists() {
        super("The user role exists for person");
    }
}
