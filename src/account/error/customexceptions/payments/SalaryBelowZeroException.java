package account.error.customexceptions.payments;

public class SalaryBelowZeroException extends RuntimeException {

    public SalaryBelowZeroException() {
        super("The Salary is below 0");
    }
}
