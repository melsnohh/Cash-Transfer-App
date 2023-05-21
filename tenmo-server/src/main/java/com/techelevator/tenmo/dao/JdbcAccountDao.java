package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dao.mapper.UserMapper;
import com.techelevator.tenmo.dao.mapper.transferRowMapper;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{
   private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int getAccount_ID(int userID) {
        String sql = "SELECT account_id FROM account WHERE user_id=?";

        return jdbcTemplate.queryForObject(sql, Integer.class, userID);

    }

    @Override
    public int getUser_ID(int accountID) {
        String sql = "SELECT user_id FROM account WHERE account_id=?";

        return jdbcTemplate.queryForObject(sql, Integer.class, accountID);
    }


    @Override
    public BigDecimal getBalance(int userID) {
        String sql = "SELECT balance FROM account WHERE user_id=?";

        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userID);

    }
    @Override
    public String getUsernameByAccountID(int accountID) {

        String sql = "SELECT username FROM tenmo_user tu\n" +
                "JOIN account a ON a.user_id = tu.user_id\n" +
                "WHERE a.account_id =?";
        return jdbcTemplate.queryForObject(sql, String.class, accountID);
    }

    @Override
    public List<User> getListOfAllUsernameAndId() {
        String sql = "SELECT username, user_id FROM tenmo_user tu";
        List<User> usernameList = jdbcTemplate.query(sql, new Object[]{},new UserMapper());
        return usernameList;
    }

}
