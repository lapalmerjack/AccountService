package account.error.customexceptions.payments;

public class NoExistingDatePeriodException extends RuntimeException {

    public NoExistingDatePeriodException() {

        super("No existing salary period exists for this user");
    }
}
