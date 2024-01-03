package account.error.customexceptions.users;

public class PasswordMatchesBannedPassword extends RuntimeException {

    public PasswordMatchesBannedPassword() {
        super("The password is in the hacker's database!");
    }
}