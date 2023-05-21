package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dao.mapper.transferRowMapper;
import com.techelevator.tenmo.model.transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class JdbcTransferDao implements transferDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<transfer> viewAllTransferByAccountIdAndStatus(int accountId, String status) {
        //Status should be transfer_status_desc: Pending, Approved, Rejected
        String sql = "SELECT * FROM transfer t\n" +
                "JOIN transfer_status ts ON t.transfer_status_id = ts.transfer_status_id\n" +
                "WHERE (t.account_from =? OR t.account_to = ?) AND ts.transfer_status_desc = ?";

        List<transfer> transferHistory = jdbcTemplate.query(sql, new Object[] {accountId, accountId,status}, new transferRowMapper());

        return transferHistory;
    }

    @Override
    public List<transfer> getTransferByTransferID(int transferId) {
        String sql= "SELECT * FROM transfer WHERE transfer_id=?";
        List<transfer> transferList = jdbcTemplate.query(sql, new Object[]{transferId}, new transferRowMapper());
        //this list should contain only 1 row
        return transferList;
    }

    //First post will always be pending
    @Override
    public transfer postNewTransferToAccount(int accountId, transfer newTransfer) {
        String sql = "INSERT INTO transfer\n" +
                "(transfer_type_id, transfer_status_id, account_from, amount)\n"+
                "VALUES (?, ?, ?, ?)\n" +
                "WHERE account_to = ?";

        int transferId = jdbcTemplate.queryForObject(sql, Integer.class, newTransfer.getTransfer_type_id(), newTransfer.getTransfer_status_id(), newTransfer.getAccount_from(), newTransfer.getAmount(), newTransfer.getAccount_to());
        newTransfer.setTransfer_id(transferId);

        return newTransfer;
    }

    //Post will always be approved status when sending
    @Override
    public transfer postNewTransferFromAccount(transfer newTransfer) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
//Consider to use enum here for transfer_type_id, transfer_type_desc AND transfer_status_id, transfer_status_desc
        Integer transferId = jdbcTemplate.queryForObject(sql, Integer.class, newTransfer.getTransfer_type_id(), newTransfer.getTransfer_status_id(), newTransfer.getAccount_from(),newTransfer.getAccount_to(), newTransfer.getAmount());
        newTransfer.setTransfer_id(transferId);

        return newTransfer;
    }

    @Override
    public void updateBalanceAfterTransfer(int userId, BigDecimal newAccountBalance) {
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, newAccountBalance,userId);

    }


    @Override
    public void updatedTransferStatus(int transferId, int statusId) {
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
        jdbcTemplate.update(sql, statusId, transferId);

    }

}
