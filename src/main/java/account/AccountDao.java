package account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AccountDao {
    public static class AlreadyExistsException extends Exception {}
    public static class NotFoundException extends Exception {}
    public static class NotEnoughMoneyException extends Exception {}

    private ConcurrentHashMap<String, Account> storage;
    private static AccountDao accountDao;

    private AccountDao() {
        storage = new ConcurrentHashMap<>();
    }

    public static synchronized AccountDao getAccountDao() {
        if (accountDao == null) {
            accountDao = new AccountDao();
        }
        return accountDao;
    }

    public List<Account> getAll() {
        return new ArrayList<>(storage.values());
    }

    public void updateAccountBalance(String accountNumber, BigDecimal amountDiff)
            throws NotEnoughMoneyException, NotFoundException {
        Account account = get(accountNumber);
        if (amountDiff.compareTo(BigDecimal.ZERO) < 0
                && (account.getBalance().add(amountDiff)).compareTo(BigDecimal.ZERO) < 0
        ) {
            throw new NotEnoughMoneyException();
        }
        account.setBalance(account.getBalance().add(amountDiff));
        update(accountNumber, account);
    }

    public void create(Account account) throws AlreadyExistsException {
        String accountNumber = account.getNumber();
        if (exists(accountNumber)) {
            throw new AlreadyExistsException();
        }
        storage.put(accountNumber, account);
    }

    public Account get(String accountNumber) throws NotFoundException {
        if (!exists(accountNumber)) {
            throw new NotFoundException();
        }
        return new Account(storage.get(accountNumber));
    }

    public void update(String accountNumber, Account account) throws NotFoundException {
        if (!exists(accountNumber)) {
            throw new NotFoundException();
        }
        account.setNumber(accountNumber);
        storage.put(accountNumber, account);
    }

    public void delete(String accountNumber) throws NotFoundException {
        if (!exists(accountNumber)) {
            throw new NotFoundException();
        }
        storage.remove(accountNumber);
    }

    public boolean exists(String accountNumber) {
        return storage.containsKey(accountNumber);
    }
}
