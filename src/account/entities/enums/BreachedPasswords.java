package account.entities.enums;

public enum BreachedPasswords {

    PASSWORD_FOR_JANUARY("PasswordForJanuary"),
    PASSWORD_FOR_FEBRUARY("PasswordForFebruary"),
    PASSWORD_FOR_MARCH("PasswordForMarch"),
    PASSWORD_FOR_APRIL("PasswordForApril"),
    PASSWORD_FOR_MAY("PasswordForMay"),
    PASSWORD_FOR_JUNE("PasswordForJune"),
    PASSWORD_FOR_JULY("PasswordForJuly"),
    PASSWORD_FOR_AUGUST("PasswordForAugust"),
    PASSWORD_FOR_SEPTEMBER("PasswordForSeptember"),
    PASSWORD_FOR_OCTOBER("PasswordForOctober"),
    PASSWORD_FOR_NOVEMBER("PasswordForNovember"),
    PASSWORD_FOR_DECEMBER("PasswordForDecember");


    private final String breachedPassword;

    BreachedPasswords(String breachedPassword) {
        this.breachedPassword = breachedPassword;
    }

    public String getBreachedPassword() {
        return breachedPassword;
    }
}