package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.transfer;

import java.math.BigDecimal;
import java.util.List;

public interface transferDao {
    List<transfer> viewAllTransferByAccountIdAndStatus(int accountId, String status);

    List<transfer> getTransferByTransferID(int transferId);

    transfer postNewTransferToAccount(int accountId, transfer transfer);

    void updatedTransferStatus(int transferId, int statusId);

    transfer postNewTransferFromAccount(transfer transfer);

    void updateBalanceAfterTransfer(int userId, BigDecimal newAccountBalance);


}
