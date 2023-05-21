package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Services.BalanceService;
import com.techelevator.tenmo.Services.TransferService;
import com.techelevator.tenmo.Services.UserService;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@PreAuthorize("isAuthenticated()")
@RestController
public class TenmoController {
    private BalanceService balanceService;
    private TransferService transferService;
    private UserService userService;

    public TenmoController(BalanceService balanceService, TransferService transferService, UserService userService) {
        this.balanceService = balanceService;
        this.transferService = transferService;
        this.userService = userService;
    }

    //MAKE SURE TO HAVE VARIABLE NAMED THE SAME AS PATH I.E "id"
    @GetMapping("user/{id}")
    public BigDecimal viewBalance(@PathVariable int id){
        return balanceService.viewBalance(id);
    }

    @GetMapping("user/{id}/Transfer")
    public List<transfer> viewAllApprovedTransfer(@PathVariable int id){
        return transferService.viewTransfer(id, "Approved");
    }

    //Consider to use requestparam to use the same method for both all transfer and filtered transfer
    @GetMapping("user/{id}/Transfer/Transaction/{transferId}")
    public List<transfer> viewTransferByTransferID(@PathVariable int id, @PathVariable int transferId){
        return transferService.getTransferDetailsByTransferID(transferId);
    }

    @PutMapping("user/{id}/Transfer/Transaction")
    public void updateTransferStatusByTransferID(@PathVariable int id, @RequestBody transfer transfer){
        transferService.transferStatusUpdate(id,transfer.getTransfer_id(), transfer.getTransfer_status_id());
    }

    //Added {id} to path since only authenticated users can get here
    @GetMapping("user/{id}/Users")
    public List<User> getAllAccounts(@PathVariable int id){
//        return userService.getAllUsers();
        return userService.getListOfUsernameAndId();
    }

    //REVIEW AND DOUBLE CHECK PATH MAKE SENSE
    @GetMapping("user/{id}/Users/{userID}")
    public int getAccountIDFromUserID(@PathVariable int userID){
        return userService.getAccountIDfromUserID(userID);
    }

    @GetMapping("user/{id}/Transfer/Pending")
    public List<transfer> viewAllPendingTransfers(@PathVariable int id){
        return transferService.viewTransfer(id, "Pending");
    }

    @GetMapping("user/{id}/Transfer/{accountID}")
    public String getUsernameByAccountID(@PathVariable int accountID){
        return userService.getUsernameByAccountID(accountID);
    }

    //For Send Bucks
    @PostMapping ("user/{id}/Transfer")
    public transfer postSendTransaction(@PathVariable int id, @RequestBody transfer transfer){
        transferService.updateBalanceAfterTransaction(id, transfer);
        return transferService.postSendTransaction(transfer);
    }


    @PostMapping ("user/{id}/TransferRequest")
    public transfer postRequestTransaction(@PathVariable int id, @RequestBody transfer transfer){
        return transferService.postSendTransaction(transfer);
    }



}
