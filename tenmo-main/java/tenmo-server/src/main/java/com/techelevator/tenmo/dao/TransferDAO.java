package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.NewTransfer;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {
    void deductFrom(int accountFrom, BigDecimal amount);

    void addMoneyTo(int accountTo, BigDecimal amount);

    NewTransfer addToTransferTable(NewTransfer newTransfer);

    List<Transfer> showTransfers(int userId);

    Transfer getTransferById(int transferId, int userId);
    void requestMoney(int userIdFrom, int userIdTo, BigDecimal amount);
//    List<Transfer> getTransfersByUserId(int userId);

    List<NewTransfer> getMoneyRequestsByUserId(int userId);
 int convertedAccountID(int userId);
 int convertedUserID(int accountId);
    void approveOrRejectTransfer(int transferId, boolean approved);











}
