package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountModel;

import java.math.BigDecimal;

public interface AccountDataInterface {
    BigDecimal getBalanceByUser (String username);


}
