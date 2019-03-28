package transfer;

import java.math.BigDecimal;
import java.util.Date;

public class Transfer {
    private String id;
    private String senderAccount;
    private String recipientAccount;
    private BigDecimal amount;
    private Date time;

    public Transfer() {

    }

    public Transfer(Transfer transfer) {
        this.id = transfer.id;
        this.senderAccount = transfer.senderAccount;
        this.recipientAccount = transfer.recipientAccount;
        this.amount = transfer.amount;
        this.time = transfer.time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getRecipientAccount() {
        return recipientAccount;
    }

    public void setRecipientAccount(String recipientAccount) {
        this.recipientAccount = recipientAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
