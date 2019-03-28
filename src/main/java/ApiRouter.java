import account.AccountController;
import http.ExchangeWrapper;
import transfer.TransferController;
import com.google.gson.JsonParseException;

public class ApiRouter extends http.Router {
    private AccountController accountController;
    private TransferController transferController;

    public ApiRouter() {
        this.accountController = new AccountController();
        this.transferController = new TransferController();
    }

    @Override
    protected void initRoutes() {
        addGET("/accounts", accountController::getList);
        addPOST("/accounts", accountController::create);
        addGET("/accounts/{number}", accountController::getByNumber);
        addPUT("/accounts/{number}", accountController::update);
        addDELETE("/accounts/{number}", accountController::delete);

        addGET("/transfers", transferController::getList);
        addPOST("/transfers", transferController::create);
        addGET("/transfers/{id}", transferController::getById);
        addDELETE("/transfers/{id}", transferController::delete);
    }

    @Override
    protected void onException(ExchangeWrapper exchange, Throwable exception) {
        if (exception instanceof JsonParseException) {
            exchange.setResponseCode(400);
            return;
        }
        super.onException(exchange, exception);
    }
}
