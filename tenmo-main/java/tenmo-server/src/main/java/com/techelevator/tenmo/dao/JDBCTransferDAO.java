package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.controller.AccountController;
import com.techelevator.tenmo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JDBCTransferDAO implements TransferDAO {
    private JdbcTemplate jdbcTemplate;
    private  AccountDataInterface accountDataInterface;


    public JDBCTransferDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;


    }

    @Override
    public void deductFrom(int userIdFrom, BigDecimal amount) {  //Updates the account that the transfer is coming from (user inputting the transfer)
        AccountModel transferFromAccount = new AccountModel(); //created a from account object
        String sqlSetTransferFromAccountData = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?";//setting the values using postGres searching by the id number
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSetTransferFromAccountData, userIdFrom);
        while (results.next()) {

            transferFromAccount.setAccount_id(results.getInt("account_id"));
            transferFromAccount.setUser_id(results.getInt("user_id"));
            transferFromAccount.setBalance(results.getBigDecimal("balance"));
        }//no helper method but is basically our mapToRowSet
        String sqlUpdateUserIdFrom = "UPDATE account SET balance = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlUpdateUserIdFrom, transferFromAccount.getBalance().subtract(amount), userIdFrom);//updates the amount in the account it's from


    }

    @Override
    public void addMoneyTo(int userIdTo, BigDecimal amount) { // set up SQL stuff to add money to the user specified in the front end
        AccountModel transferToAccount = new AccountModel(); //created a from account object
        String sqlSetTransferFromAccountData = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?";//setting the values using postGres searching by the id number
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSetTransferFromAccountData, userIdTo);
        while (results.next()) {

            transferToAccount.setAccount_id(results.getInt("account_id"));
            transferToAccount.setUser_id(results.getInt("user_id"));
            transferToAccount.setBalance(results.getBigDecimal("balance"));

            String sqlUpdateUserIdTo = "UPDATE account SET balance = ? WHERE user_id = ?";
            jdbcTemplate.update(sqlUpdateUserIdTo, (transferToAccount.getBalance().add(amount)), userIdTo);

        }

    }

    public NewTransfer addToTransferTable(NewTransfer newTransfer) {
        int fromAccountIdConverted = convertedAccountID(newTransfer.getFromUserId());
        int toAccountIdConverted = convertedAccountID(newTransfer.getToUserId());


        String sqlPostToTransfer = "insert into transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)"
                + "values (2, 2, ?, ?, ?)";
        jdbcTemplate.update(sqlPostToTransfer, fromAccountIdConverted, toAccountIdConverted, newTransfer.getAmount());
        return newTransfer;

    }

    public List<Transfer> showTransfers(int userId) {
        List<Transfer> listOfTransfers = new ArrayList<>();

        String sqlGetTransfers = "select * from tenmo_user " +
                "JOIN account ON tenmo_user.user_id = account.user_id " +
                "JOIN transfer ON account.account_id = transfer.account_from OR account.account_id = transfer.account_to " +
                "WHERE account.account_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetTransfers, convertedAccountID(userId));
        while (results.next()) {
            Transfer transferHistory = mapRowToTransfer(results);
            listOfTransfers.add(transferHistory);

            if (transferHistory.getAccountTo() == convertedAccountID(userId)) {
                transferHistory.setOtherUser(findUsernameById(convertedUserID(transferHistory.getAccountFrom())));
                transferHistory.setWasSentToUs(true);
            } else {
                transferHistory.setOtherUser(findUsernameById(convertedUserID(transferHistory.getAccountTo())));
                transferHistory.setWasSentToUs(false);
            }

            }
        return listOfTransfers;

    }

    @Override
    public Transfer getTransferById(int currentUser, int transferId) {
        String sqlShowTransferById = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlShowTransferById, transferId);
        while (results.next()) {
            Transfer transferById = mapRowToTransfer(results);

        if (transferById.getAccountFrom() == convertedAccountID(currentUser)) {
            transferById.setWasSentToUs(false);
            transferById.setOtherUser(findUsernameById(convertedUserID(transferById.getAccountTo())));
            return transferById;

        } else {
            transferById.setWasSentToUs(true);
            transferById.setOtherUser(findUsernameById(convertedUserID(transferById.getAccountFrom())));
            return transferById;

        }
        } return null;
    }
    @Override
    public int convertedAccountID(int userId) {
        int accountIdConverted = 0;
        String sqlConvertUserIdToAccountId = "SELECT account_id FROM account WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlConvertUserIdToAccountId, userId);
        while (results.next()) {
            accountIdConverted = results.getInt("account_id");
        }
        return accountIdConverted;
    }
    @Override
    public int convertedUserID(int accountId) {
        int convertedUserId = 0;
        String sqlConvertAccountIdToUserId = "SELECT user_id FROM account WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlConvertAccountIdToUserId, accountId);
        while (results.next()) {
            convertedUserId = results.getInt("user_id");
        }
        return convertedUserId;

    }

    @Override
    public void approveOrRejectTransfer(int transferId, boolean approved) {

    }


    public void approveOrRejectTransfer(int transferId, int userId, boolean approved) {

    }

//    @Override
//    public void approveOrRejectTransfer(int transferId, int userId, boolean approved) {
//        // Retrieve the transfer information
//        Transfer transfer = getTransferById(userId, transferId);
//
//        if (transfer == null) {
//            // Handle the case where the transfer doesn't exist or is not accessible
//            // You can return an error response or throw an exception.
//            // For example, you can return a 404 Not Found response.
//            System.out.println("Transfer not found or not accessible");
//            return;
//        }
//
//        if (transfer.getTransferTypeId() == 1 && transfer.getTransferStatusId() == 1) {
//            if (approved) {
//                // Deduct the amount from the user's account
//                deductFrom(userId, transfer.getAmount());
//                // Add the amount to the requester's account
//                addMoneyTo(transfer.getFromUserId(), transfer.getAmount());
//            }
//
//            // Update the transfer status based on approval
//            int newTransferStatusId = approved ? 2 : 3; // 2 for Approved, 3 for Rejected
//            String sqlUpdateTransferStatus = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
//            jdbcTemplate.update(sqlUpdateTransferStatus, newTransferStatusId, transferId);
//        } else {
//            // Handle the case where the transfer is not a Request Transfer or already approved/rejected
//            // You can return an error response or throw an exception.
//            // For example, you can return a 400 Bad Request response.
//            System.out.println("Invalid transfer for approval or already processed");
//        }
//    }


    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));


        return transfer;
    }

    public String findUsernameById(int userId) {
        String username = "";
        String sql = "SELECT username FROM tenmo_user WHERE user_id = ?";
        username = jdbcTemplate.queryForObject(sql, String.class, userId);
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            username = results.getString("username");
        }
        return username;

    }


@Override
public void requestMoney(int userIdFrom, int userIdTo, BigDecimal amount) {
    // Retrieve the current balance of the user initiating the request
    BigDecimal currentBalance = getCurrentBalance(userIdFrom);

    // Check if the current balance is sufficient to cover the requested amount
    if (currentBalance.compareTo(amount) < 0) {

        System.out.println("Insufficient balance to request money");
    }

    // Deduct the requested amount from the user's account
    deductFrom(userIdFrom, amount);

    // Insert a record into the transfer table to represent the money request
    String sqlPostToTransfer = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?)";
    jdbcTemplate.update(sqlPostToTransfer, 1, 1, convertedAccountID(userIdFrom), convertedAccountID(userIdTo), amount);
}

    // Helper method to get the current balance for a user
    private BigDecimal getCurrentBalance(int userId) {
        String sqlGetBalance = "SELECT balance FROM account WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sqlGetBalance, BigDecimal.class, userId);
    }



    //  get requests

    @Override
    public List<NewTransfer> getMoneyRequestsByUserId(int userId) {
        String sqlGetMoneyRequests = "SELECT * FROM transfer " +
                "WHERE account_to = ? AND transfer_type_id = 1 AND transfer_status_id = 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetMoneyRequests, convertedAccountID(userId));
        List<NewTransfer> moneyRequests = new ArrayList<>();

        while (results.next()) {
            NewTransfer moneyRequest = new NewTransfer();
            moneyRequest.setTransferId(results.getInt("transfer_id"));
            moneyRequest.setFromUserId(convertedUserID(results.getInt("account_from")));
            moneyRequest.setToUserId(userId);
            moneyRequest.setAmount(results.getBigDecimal("amount"));
            moneyRequests.add(moneyRequest);
        }

        return moneyRequests;

    }





    // New one





}

