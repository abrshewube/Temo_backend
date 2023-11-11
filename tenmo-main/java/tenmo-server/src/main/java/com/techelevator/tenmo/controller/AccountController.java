package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDataInterface;
import com.techelevator.tenmo.dao.JdbcUserDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;


import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {
    @Autowired
    private AccountDataInterface accountDataDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TransferDAO transferDAO;



    private  Principal principal;
    private JdbcTemplate jdbcTemplate;


    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        BigDecimal balance = new BigDecimal(String.valueOf(accountDataDAO.getBalanceByUser(principal.getName())));
        return balance;
    }

    @RequestMapping(path = "/get-all-users", method = RequestMethod.GET)
    public List<OtherUser> users(Principal principal) {
        List<OtherUser> users = userDAO.findAllButLoggedIn(principal.getName());
        for (OtherUser u : users) {
            System.out.println(u.getId() + " " + u.getUsername());
        }
        return users;
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public NewTransfer newBalance(@RequestBody NewTransfer newTransfer) {
        transferDAO.deductFrom(newTransfer.getFromUserId(), newTransfer.getAmount());
        transferDAO.addMoneyTo(newTransfer.getToUserId(), newTransfer.getAmount());
        transferDAO.addToTransferTable(newTransfer);


        return newTransfer;
    }

    @RequestMapping(path = "/history/{id}", method = RequestMethod.GET)
    public List<Transfer> showHistory(@PathVariable int id) {
        List<Transfer> output = transferDAO.showTransfers(id);


        return output;

    }

    @RequestMapping(path = "/history/{id}/{transferId}", method = RequestMethod.GET)
    public Transfer historyByTransferId(@PathVariable int id, @PathVariable int transferId) {
        Transfer requestedByTransferId = transferDAO.getTransferById(id, transferId);
        return requestedByTransferId;
    }

    @RequestMapping(path = "/request-money", method = RequestMethod.POST)
    public ResponseEntity<NewTransfer> requestMoney(@RequestBody NewTransfer newTransfer) {
        transferDAO.requestMoney(newTransfer.getFromUserId(), newTransfer.getToUserId(), newTransfer.getAmount());
        return new ResponseEntity<>(newTransfer, HttpStatus.OK);
    }


    @RequestMapping(path = "/view-requests", method = RequestMethod.GET)
    public List<NewTransfer> viewMoneyRequests(Principal principal) {
        String username = principal.getName();
        int userId = userDAO.findIdByUsername(username);

        // Retrieve money requests to the user
        return transferDAO.getMoneyRequestsByUserId(userId);
    }








    // Helper method to get the current balance for a user
    private BigDecimal getCurrentBalance(int userId) {
        String sqlGetBalance = "SELECT balance FROM account WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sqlGetBalance, BigDecimal.class, userId);
    }





}
