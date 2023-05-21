package com.techelevator.tenmo.Services;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.transferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.transfer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TransferService {

    private final transferDao transferDao;
    private AccountDao accountDao;
    private BalanceService balanceService;
    //enum for status option

    public TransferService(com.techelevator.tenmo.dao.transferDao transferDao, AccountDao accountDao, BalanceService balanceService) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.balanceService = balanceService;
    }

    public List<transfer> viewTransfer (int id, String status){
        return transferDao.viewAllTransferByAccountIdAndStatus(accountDao.getAccount_ID(id), status);
    }

    public transfer postSendTransaction(transfer transfer){
        return transferDao.postNewTransferFromAccount(transfer);
    }

    @Transactional
    public void updateBalanceAfterTransaction(int id, transfer transfer){

        BigDecimal curTransferAmount = transfer.getAmount();
        BigDecimal curSenderBalanceAmount = balanceService.viewBalance(id);
        BigDecimal newSenderBalance = curSenderBalanceAmount.subtract(curTransferAmount);

        BigDecimal curReceiverBalanceAmount = balanceService.viewBalance(accountDao.getUser_ID(transfer.getAccount_to()));
        BigDecimal newReceiverBalance = curReceiverBalanceAmount.add(curTransferAmount);

        transferDao.updateBalanceAfterTransfer(id, newSenderBalance);
        transferDao.updateBalanceAfterTransfer(accountDao.getUser_ID(transfer.getAccount_to()), newReceiverBalance);
    }

    public List<transfer> getTransferDetailsByTransferID(int transferId){
        return transferDao.getTransferByTransferID(transferId);
    }

    public void transferStatusUpdate(int userId, int transferId, int status){
        transferDao.updatedTransferStatus(transferId, status);
        List<transfer> transferList = getTransferDetailsByTransferID(transferId);
        transfer updateTransfer = transferList.get(0);

        if(status == 2){
            updateBalanceAfterTransaction(userId, updateTransfer);
        }
    }


}
