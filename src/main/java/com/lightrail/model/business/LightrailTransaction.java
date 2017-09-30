package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.net.APICore;
import com.lightrail.model.api.objects.Metadata;
import com.lightrail.model.api.objects.RequestParameters;
import com.lightrail.model.api.objects.Transaction;

import java.io.IOException;
import java.util.*;

public class LightrailTransaction extends Transaction {

    public LightrailTransaction(Transaction transaction) {
        super(transaction.getRawJson());
    }

    String getDefaultCaptureUserSuppliedId() {
        return getUserSuppliedId() + "-capture";
    }

    String getDefaultVoidUserSuppliedId() {
        return getUserSuppliedId() + "-void";
    }

    String getDefaultRefundUserSuppliedId() {
        return getUserSuppliedId() + "-refund";
    }

    public LightrailTransaction refund() throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        return refund(getDefaultRefundUserSuppliedId(), null);
    }

    public LightrailTransaction refund(Metadata metadata) throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        return refund(getDefaultRefundUserSuppliedId(), metadata);
    }

    public LightrailTransaction refund(String userSuppliedId, Metadata metadata) throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        RequestParameters transactionParams = new RequestParameters();
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, userSuppliedId);
        if (metadata != null && !metadata.isEmpty())
            transactionParams.put(LightrailConstants.Parameters.METADATA, metadata);
        return refund(transactionParams);
    }

    public LightrailTransaction capture() throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        return capture(getDefaultCaptureUserSuppliedId(), null);
    }

    public LightrailTransaction capture(Metadata metadata) throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        return capture(getDefaultCaptureUserSuppliedId(), metadata);
    }

    public LightrailTransaction capture(String userSuppliedId, Metadata metadata) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        RequestParameters transactionParams = new RequestParameters();
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, userSuppliedId);
        if (metadata != null && !metadata.isEmpty())
            transactionParams.put(LightrailConstants.Parameters.METADATA, metadata);
        return capture(transactionParams);
    }

    public LightrailTransaction doVoid() throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        return doVoid(getDefaultVoidUserSuppliedId(), null);
    }

    public LightrailTransaction doVoid(Metadata metadata) throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        return doVoid(getDefaultVoidUserSuppliedId(), metadata);
    }

    public LightrailTransaction doVoid(String userSuppliedId, Metadata metadata) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        RequestParameters transactionParams = new RequestParameters();
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, userSuppliedId);
        if (metadata != null && !metadata.isEmpty())
            transactionParams.put(LightrailConstants.Parameters.METADATA, metadata);
        return doVoid(transactionParams);
    }

    public LightrailTransaction capture(RequestParameters transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        if (! LightrailConstants.API.Transactions.TYPE_PENDING.equals(this.getTransactionType())) {
            throw new BadParameterException("Not a pending transaction.");
        }

        Transaction captureTransaction = APICore.Transactions.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.CAPTURE,
                transactionParams);
        return new LightrailTransaction(captureTransaction);
    }

    public LightrailTransaction doVoid(RequestParameters transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        if (! LightrailConstants.API.Transactions.TYPE_PENDING.equals(this.getTransactionType())) {
            throw new BadParameterException("Not a pending transaction.");
        }

        Transaction cancelTransaction = APICore.Transactions.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.VOID,
                transactionParams);
        return new LightrailTransaction(cancelTransaction);
    }

    public LightrailTransaction refund(RequestParameters transactionParams) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Transaction refundTransaction = APICore.Transactions.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.REFUND,
                transactionParams);
        return new LightrailTransaction(refundTransaction);
    }

    public static final class Create {

        public static LightrailTransaction pendingByContact(String contactId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return pendingByContact(contactId, value, currency, null);
        }

        public static LightrailTransaction pendingByContact(String contactId, int value, String currency, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byContact(contactId, value, currency, true, metadata);
        }

        public static LightrailTransaction byContact(String contactId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byContact(contactId, value, currency, null);
        }

        public static LightrailTransaction byContact(String contactId, int value, String currency, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byContact(contactId, value, currency, false, metadata);
        }

        public static LightrailTransaction pendingByCardId(String cardId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return pendingByCardId(cardId, value, currency, null);
        }

        public static LightrailTransaction pendingByCardId(String cardId, int value, String currency, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byCardId(cardId, value, currency, true, metadata);
        }

        public static LightrailTransaction byCardId(String cardId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byCardId(cardId, value, currency, null);
        }

        public static LightrailTransaction byCardId(String cardId, int value, String currency, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byCardId(cardId, value, currency, false, metadata);
        }

        public static LightrailTransaction pendingByCode(String code, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return pendingByCode(code, value, currency, null);
        }

        public static LightrailTransaction pendingByCode(String code, int value, String currency, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byCode(code, value, currency, true, metadata);
        }

        public static LightrailTransaction byCode(String code, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byCode(code, value, currency,null);
        }

        public static LightrailTransaction byCode(String code, int value, String currency, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byCode(code, value, currency, false, metadata);
        }

        private static LightrailTransaction byCardId(String cardId, int value, String currency, boolean pending, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            RequestParameters transactionParams = new RequestParameters();
            transactionParams.put(LightrailConstants.Parameters.CARD_ID, cardId);
            transactionParams.put(LightrailConstants.Parameters.VALUE, value);
            transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
            transactionParams.put(LightrailConstants.Parameters.PENDING, pending);
            if (metadata != null && !metadata.isEmpty())
                transactionParams.put(LightrailConstants.Parameters.METADATA, metadata);
            return create(transactionParams);
        }

        private static LightrailTransaction byCode(String code, int value, String currency, boolean pending, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            RequestParameters transactionParams = new RequestParameters();
            transactionParams.put(LightrailConstants.Parameters.CODE, code);
            transactionParams.put(LightrailConstants.Parameters.VALUE, value);
            transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
            transactionParams.put(LightrailConstants.Parameters.PENDING, pending);
            if (metadata != null && !metadata.isEmpty())
                transactionParams.put(LightrailConstants.Parameters.METADATA, metadata);
            return create(transactionParams);
        }

        private static LightrailTransaction byContact(String contactId, int value, String currency, boolean pending, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            RequestParameters transactionParams = new RequestParameters();
            transactionParams.put(LightrailConstants.Parameters.CONTACT, contactId);
            transactionParams.put(LightrailConstants.Parameters.VALUE, value);
            transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
            transactionParams.put(LightrailConstants.Parameters.PENDING, pending);
            if (metadata != null && !metadata.isEmpty())
                transactionParams.put(LightrailConstants.Parameters.METADATA, metadata);
            return create(transactionParams);
        }

        public static LightrailTransaction create(RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            return create(transactionParams, false);
        }

        private static LightrailTransaction create(RequestParameters transactionParams, boolean simulate) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            LightrailConstants.Parameters.requireParameters(Arrays.asList(
                    LightrailConstants.Parameters.VALUE,
                    LightrailConstants.Parameters.CURRENCY
            ), transactionParams);

            transactionParams = LightrailContact.handleContact(transactionParams);

            makeSureValueOfPendingTransactionIsNegative(transactionParams);

            String code = (String) transactionParams.remove(LightrailConstants.Parameters.CODE);
            String cardId = (String) transactionParams.remove(LightrailConstants.Parameters.CARD_ID);

            transactionParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(transactionParams);

            LightrailTransaction transaction;
            if (code != null && !code.isEmpty()) {
                if (simulate) {
                    transaction = new LightrailTransaction(APICore.Transactions.simulateTransactionByCode(code, transactionParams));
                } else {
                    transaction = new LightrailTransaction(APICore.Transactions.createTransactionByCode(code, transactionParams));
                }
            } else if (cardId != null && !cardId.isEmpty()) {
                if (simulate) {
                    transaction = new LightrailTransaction(APICore.Transactions.simulateTransactionByCard(cardId, transactionParams));
                } else {
                    transaction = new LightrailTransaction(APICore.Transactions.createTransactionByCard(cardId, transactionParams));
                }
            } else
                throw new BadParameterException("Must provide either a 'code', a 'cardId', or a valid 'contact'.");

            return transaction;
        }

        private static void makeSureValueOfPendingTransactionIsNegative(RequestParameters transactionParams) {
            Integer value = (Integer) transactionParams.get(LightrailConstants.Parameters.VALUE);
            Boolean pending = (Boolean) transactionParams.get(LightrailConstants.Parameters.PENDING);
            if (pending == null)
                pending = false;

            if (pending && value >= 0)
                throw new BadParameterException("Pending transaction value must be negative.");
        }
    }

    public static final class Simulate {

        public static LightrailTransaction pendingByContact(String contactId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return pendingByContact(contactId, value, currency, null);
        }

        public static LightrailTransaction pendingByContact(String contactId, int value, String currency, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byContact(contactId, value, currency, true, metadata);
        }

        public static LightrailTransaction byContact(String contactId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byContact(contactId, value, currency, null);
        }

        public static LightrailTransaction byContact(String contactId, int value, String currency, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byContact(contactId, value, currency, false, metadata);
        }

        public static LightrailTransaction byCardId(String cardId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byCardId(cardId, value, currency, null);
        }
        public static LightrailTransaction byCardId(String cardId, int value, String currency, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            RequestParameters transactionParams = new RequestParameters();
            transactionParams.put(LightrailConstants.Parameters.CARD_ID, cardId);
            transactionParams.put(LightrailConstants.Parameters.VALUE, value);
            transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
            if (metadata != null && !metadata.isEmpty())
                transactionParams.put(LightrailConstants.Parameters.METADATA, metadata);
            return simulate(transactionParams);
        }

        public static LightrailTransaction byCode(String code, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            return byCode(code, value, currency, null);
        }
        public static LightrailTransaction byCode(String code, int value, String currency, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            RequestParameters transactionParams = new RequestParameters();
            transactionParams.put(LightrailConstants.Parameters.CODE, code);
            transactionParams.put(LightrailConstants.Parameters.VALUE, value);
            transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
            if (metadata != null && !metadata.isEmpty())
                transactionParams.put(LightrailConstants.Parameters.METADATA, metadata);
            return simulate(transactionParams);
        }

        public static LightrailTransaction simulate(RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            if (!transactionParams.containsKey(LightrailConstants.Parameters.NSF)) {
                RequestParameters newParams = new RequestParameters(transactionParams);
                newParams.put(LightrailConstants.Parameters.NSF, false);
                return Create.create(newParams, true);
            } else {
                return Create.create(transactionParams, true);
            }
        }
        private static LightrailTransaction byContact(String contactId, int value, String currency, boolean pending, Metadata metadata) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
            RequestParameters transactionParams = new RequestParameters();
            transactionParams.put(LightrailConstants.Parameters.CONTACT, contactId);
            transactionParams.put(LightrailConstants.Parameters.VALUE, value);
            transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
            transactionParams.put(LightrailConstants.Parameters.PENDING, pending);
            if (metadata != null && !metadata.isEmpty())
                transactionParams.put(LightrailConstants.Parameters.METADATA, metadata);
            return simulate(transactionParams);
        }
    }

    public static final class Retrieve {

        public static LightrailTransaction retrieve(RequestParameters transactionParams) throws AuthorizationException, IOException, CouldNotFindObjectException {
            String code = (String) transactionParams.get(LightrailConstants.Parameters.CODE);
            String cardId = (String) transactionParams.get(LightrailConstants.Parameters.CARD_ID);
            String transactionId = (String) transactionParams.get(LightrailConstants.Parameters.TRANSACTION_ID);
            String userSuppliedId = (String) transactionParams.get(LightrailConstants.Parameters.USER_SUPPLIED_ID);

            try {
                if (code != null && userSuppliedId != null) {
                    return byCodeAndUserSuppliedId(code, userSuppliedId);
                } else if (cardId != null && userSuppliedId != null) {
                    return byCardIdAndUserSuppliedId(cardId, userSuppliedId);
                } else if (code != null && transactionId != null) {
                    return byCardIdAndTransactionId(cardId, transactionId);
                } else if (cardId != null && transactionId != null) {
                    return byCardIdAndTransactionId(cardId, transactionId);
                } else {
                    throw new BadParameterException("Not enough information to retrieve the transaction.");
                }
            } catch (InsufficientValueException e) {//never happens
                throw new RuntimeException(e);
            }
        }

        public static LightrailTransaction byCardIdAndUserSuppliedId(String cardId, String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            return new LightrailTransaction(APICore.Transactions.retrieveTransactionByCardIdAndUserSuppliedId(cardId, userSuppliedId));
        }

        public static LightrailTransaction byCardIdAndTransactionId(String cardId, String transactionId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            return new LightrailTransaction(APICore.Transactions.retrieveTransactionByCardIdAndTransactionId(cardId, transactionId));
        }

        public static LightrailTransaction byCodeAndUserSuppliedId(String code, String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            return new LightrailTransaction(APICore.Transactions.retrieveTransactionByCodeAndUserSuppliedId(code, userSuppliedId));
        }

        public static LightrailTransaction byCodeAndTransactionId(String code, String transactionId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            return new LightrailTransaction(APICore.Transactions.retrieveTransactionByCodeAndTransactionId(code, transactionId));
        }
    }

}
