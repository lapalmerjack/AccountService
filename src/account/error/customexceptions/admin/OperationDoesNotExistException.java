package account.error.customexceptions.admin;

public class OperationDoesNotExistException extends RuntimeException {
    public OperationDoesNotExistException() {
        super("This operation does not exist");
    }
}
