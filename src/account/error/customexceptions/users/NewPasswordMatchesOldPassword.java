package account.error.customexceptions.users;

public class NewPasswordMatchesOldPassword extends RuntimeException {

    public NewPasswordMatchesOldPassword () {
        super("The passwords must be different!");
    }
}