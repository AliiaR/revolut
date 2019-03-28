package transfer;

import account.AccountDao;
import http.ExchangeWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class TransferController {
    private Gson gson;
    TransferDao transferDao;
    
    public TransferController() {
        this.transferDao = TransferDao.getTransferDao();
        gson = new GsonBuilder()
                .registerTypeAdapter(Transfer.class, new TransferJsonConverter())
                .create();
    }

    public void getList(ExchangeWrapper exchange) throws Exception {        
        String accountNumber = exchange.getUriParam("accountNumber", "");
        List<Transfer> transfers = accountNumber.isEmpty() ?
                transferDao.getAll() : transferDao.getByAccountNumber(accountNumber);
        exchange.setResponseBody(gson.toJson(transfers));
    }

    public void create(ExchangeWrapper exchange) throws Exception {
        Transfer transfer = gson.fromJson(exchange.getRequestBody(), Transfer.class);
        try {
            transferDao.create(transfer);
            exchange.setResponseBody(gson.toJson(transfer));
            exchange.setResponseCode(201);
        } catch (AccountDao.NotFoundException e) {
            exchange.setResponseCode(400);
        } catch (AccountDao.NotEnoughMoneyException e ) {
            exchange.setResponseCode(422);
        }
    }

    public void getById(ExchangeWrapper exchange) throws Exception {
        String transferId = exchange.getUriParam("id");
        try {
            Transfer account = transferDao.get(transferId);
            exchange.setResponseBody(gson.toJson(account));
        } catch (TransferDao.NotFoundException e) {
            exchange.setResponseCode(404);
        }
    }

    public void delete(ExchangeWrapper exchange) throws Exception {
        String transferId = exchange.getUriParam("id");
        try {
            transferDao.delete(transferId);
        } catch (AccountDao.NotFoundException e) {
            exchange.setResponseCode(400);
        } catch (TransferDao.NotFoundException e) {
            exchange.setResponseCode(404);
        } catch (AccountDao.NotEnoughMoneyException e ) {
            exchange.setResponseCode(422);
        }
    }
}
