package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class MoneyRequest {
    private int userIdFrom;
    private int userIdTo;
    private BigDecimal amount;

    public MoneyRequest() {
        // Default no-argument constructor
    }

    public MoneyRequest(int userIdFrom, int userIdTo, BigDecimal amount) {
        this.userIdFrom = userIdFrom;
        this.userIdTo = userIdTo;
        this.amount = amount;
    }

    public int getUserIdFrom() {
        return userIdFrom;
    }

    public void setUserIdFrom(int userIdFrom) {
        this.userIdFrom = userIdFrom;
    }

    public int getUserIdTo() {
        return userIdTo;
    }

    public void setUserIdTo(int userIdTo) {
        this.userIdTo = userIdTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

