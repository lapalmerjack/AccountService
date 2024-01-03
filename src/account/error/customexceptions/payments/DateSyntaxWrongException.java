package account.error.customexceptions.payments;

public class DateSyntaxWrongException extends RuntimeException {

    public DateSyntaxWrongException() {
        super("Please input the proper date period");
    }
}
