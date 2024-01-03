package account.error.customexceptions.admin;

public class RoleCombinationException extends RuntimeException {

    public RoleCombinationException() {
        super( "The user cannot combine administrative and business roles!");
    }
}
