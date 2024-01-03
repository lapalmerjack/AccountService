package account.error.customexceptions.admin;

public class RoleDoesNotExist extends RuntimeException {

    public RoleDoesNotExist() {
        super("Role not found!");
    }
}
