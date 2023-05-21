package com.techelevator.tenmo.Services;

import com.techelevator.tenmo.dao.AccountDao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalanceService {
    private AccountDao accountDao;

    public BalanceService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public BigDecimal viewBalance(int userID){
//        System.out.println("Your current balance is:" + accountDao.getBalance(userID));
        return accountDao.getBalance(userID);
    }
}
