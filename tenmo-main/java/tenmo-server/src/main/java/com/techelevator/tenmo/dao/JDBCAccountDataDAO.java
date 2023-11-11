package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountModel;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public class JDBCAccountDataDAO implements AccountDataInterface {

    private JdbcTemplate jdbcTemplate;
    public JDBCAccountDataDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalanceByUser(String username) {
        String sqlFindBalanceById= "select account.balance from account join tenmo_user on account.user_id = tenmo_user.user_id where tenmo_user.username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindBalanceById,username);
        BigDecimal balance = new BigDecimal(0.00);
        while (results.next()){
            balance = results.getBigDecimal("balance");
        }

        return balance;
    }




    public AccountModel findUserById(int userId) {
        String sqlString = "SELECT * FROM account WHERE user_id = ?";
        AccountModel account = null;
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sqlString, userId);
            account = mapRowToAccount(result);
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        return account;
    }


    public AccountModel findAccountById(int id) {
        AccountModel account = null;
        String sql = "SELECT * FROM account WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    private AccountModel mapRowToAccount(SqlRowSet result) {
        AccountModel account = new AccountModel();
        account.setBalance(result.getBigDecimal("balance"));
        account.setAccount_id(result.getInt("account_id"));
        account.setUser_id(result.getInt("user_id"));
        return account;
    }

}





