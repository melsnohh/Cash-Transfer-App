package com.techelevator.tenmo.dao.mapper;

import com.techelevator.tenmo.model.transfer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class transferRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        transfer newTransfer = new transfer();
        newTransfer.setTransfer_id(resultSet.getInt("transfer_id"));
        newTransfer.setTransfer_type_id(resultSet.getInt("transfer_type_id"));
        newTransfer.setTransfer_status_id(resultSet.getInt("transfer_status_id"));
        newTransfer.setAccount_from(resultSet.getInt("account_from"));
        newTransfer.setAccount_to(resultSet.getInt("account_to"));
        newTransfer.setAmount(resultSet.getBigDecimal("amount"));

        return newTransfer;
    }
}
