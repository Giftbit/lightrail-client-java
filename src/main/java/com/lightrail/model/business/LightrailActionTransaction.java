package com.lightrail.model.business;

import com.lightrail.model.api.Transaction;

public class LightrailActionTransaction extends LightrailTransaction {
    LightrailActionTransaction (Transaction transaction) {
        this.transactionResponse = transaction;
    }
}
