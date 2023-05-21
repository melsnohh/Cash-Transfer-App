package com.techelevator.tenmo.Services;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {
//    private UserDao userDao;
//
//    public UserService(UserDao userDao) {
//        this.userDao = userDao;
//    }
//
//    public List<User> getAllUsers(){
//        return userDao.findAll();
//    }

    private final AccountDao accountDao;

    public UserService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public String getUsernameByAccountID(int accountID){
        return accountDao.getUsernameByAccountID(accountID);
    }

    public List<User> getListOfUsernameAndId(){
        return accountDao.getListOfAllUsernameAndId();
    }

    public int getAccountIDfromUserID(int userID){
        return accountDao.getAccount_ID(userID);
    }

}
