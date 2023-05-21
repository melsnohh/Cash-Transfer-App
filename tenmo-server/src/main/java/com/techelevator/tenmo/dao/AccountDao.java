package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    int getAccount_ID(int userID);
    int getUser_ID(int accountID);

    BigDecimal getBalance(int userID);

    String getUsernameByAccountID(int accountID);

    List<User> getListOfAllUsernameAndId();

}
