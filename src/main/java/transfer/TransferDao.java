package transfer;

import account.AccountDao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TransferDao {
    public static class NotFoundException extends Exception {}

    private long nextTransferId = 1;
    private ConcurrentHashMap<String, Transfer> storage;
    private AccountDao accountDao;
    private static TransferDao transferDao;

    private TransferDao() {
        storage = new ConcurrentHashMap<>();
        accountDao = AccountDao.getAccountDao();
    }

    public static synchronized TransferDao getTransferDao() {
        if (transferDao == null) {
            transferDao = new TransferDao();
        }
        return transferDao;
    }

    public List<Transfer> getAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Transfer> getByAccountNumber(String accountNumber) {
        List<Transfer> transfers = new ArrayList<>();
        for (Transfer transfer : getAll()) {
            if (accountNumber.equals(transfer.getRecipientAccount())
                    || accountNumber.equals(transfer.getSenderAccount())
            ) {
                transfers.add(transfer);
            }
        }
        return transfers;
    }

    public void create(Transfer transfer) throws AccountDao.NotFoundException, AccountDao.NotEnoughMoneyException {
        String transferId = getNextId();
        transfer.setId(transferId);
        transfer.setTime(new Date());
        if (!accountDao.exists(transfer.getSenderAccount())
            || !accountDao.exists(transfer.getRecipientAccount())
        ) {
            throw new AccountDao.NotFoundException();
        }
        accountDao.updateAccountBalance(transfer.getSenderAccount(), BigDecimal.ZERO.subtract(transfer.getAmount()));
        accountDao.updateAccountBalance(transfer.getRecipientAccount(), transfer.getAmount());
        storage.put(transferId, transfer);
    }

    public Transfer get(String transferId) throws NotFoundException {
        if (!exists(transferId)) {
            throw new NotFoundException();
        }
        return new Transfer(storage.get(transferId));
    }

    public void delete(String transferId)
            throws NotFoundException, AccountDao.NotFoundException, AccountDao.NotEnoughMoneyException
    {
        Transfer transfer = get(transferId);
        if (!accountDao.exists(transfer.getSenderAccount())
                || !accountDao.exists(transfer.getRecipientAccount())
                ) {
            throw new AccountDao.NotFoundException();
        }
        accountDao.updateAccountBalance(transfer.getRecipientAccount(), BigDecimal.ZERO.subtract(transfer.getAmount()));
        accountDao.updateAccountBalance(transfer.getSenderAccount(), transfer.getAmount());
        storage.remove(transferId);
    }

    public boolean exists(String transferId) {
        return storage.containsKey(transferId);
    }

    private synchronized String getNextId() {
        return String.valueOf(nextTransferId++);
    }
}
