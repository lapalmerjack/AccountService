package account.error.customexceptions.admin;

public class RoleAlreadyAssignedException extends RuntimeException {

    public RoleAlreadyAssignedException() {
        super("Role already assigned to a user");
    }
}
