package account;

import http.ExchangeWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class AccountController {
    private Gson gson;
    private AccountDao accountDao;
    
    public AccountController() {
        this.accountDao = AccountDao.getAccountDao();
        gson = new GsonBuilder()
                .registerTypeAdapter(Account.class, new AccountJsonConverter())
                .create();
    }

    public void getList(ExchangeWrapper exchange) throws Exception {
        List<Account> accounts = accountDao.getAll();
        exchange.setResponseBody(gson.toJson(accounts));
    }

    public void create(ExchangeWrapper exchange) throws Exception {
        Account account = gson.fromJson(exchange.getRequestBody(), Account.class);
        try {
            accountDao.create(account);
            exchange.setResponseCode(201);
            exchange.setResponseBody(gson.toJson(account));
        } catch (AccountDao.AlreadyExistsException e) {
            exchange.setResponseCode(409);
        }
    }

    public void getByNumber(ExchangeWrapper exchange) throws Exception {
        String accountNumber = exchange.getUriParam("number");
        try {
            Account account = accountDao.get(accountNumber);
            exchange.setResponseBody(gson.toJson(account));
        } catch (AccountDao.NotFoundException e) {
            exchange.setResponseCode(404);
        }
    }

    public void update(ExchangeWrapper exchange) throws Exception {
        String accountNumber = exchange.getUriParam("number");
        Account account = gson.fromJson(exchange.getRequestBody(), Account.class);
        try {
            accountDao.update(accountNumber, account);
            exchange.setResponseBody(gson.toJson(account));
        } catch (AccountDao.NotFoundException e) {
            exchange.setResponseCode(404);
        }
    }

    public void delete(ExchangeWrapper exchange) throws Exception {
        String accountNumber = exchange.getUriParam("number");
        try {
            accountDao.delete(accountNumber);
        } catch (AccountDao.NotFoundException e) {
            exchange.setResponseCode(404);
        }
    }
}
