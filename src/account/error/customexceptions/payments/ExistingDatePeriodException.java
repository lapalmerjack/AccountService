package account.error.customexceptions.payments;

public class ExistingDatePeriodException extends RuntimeException {

    public ExistingDatePeriodException() {
        super("The date period is already entered into database");
    }
}
