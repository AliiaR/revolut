package account;

import java.math.BigDecimal;

public class Account {
    private String number;
    private String currency;
    private BigDecimal balance;

    public Account() { }

    public Account(Account account) {
        this.number = account.number;
        this.currency = account.currency;
        this.balance = account.balance;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
