package account.error.customexceptions.admin;

public class RoleDoesNotExistForUser extends RuntimeException {

    public RoleDoesNotExistForUser() {
        super("The user does not have a role!");
    }
}
