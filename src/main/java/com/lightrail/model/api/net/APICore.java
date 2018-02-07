package com.lightrail.model.api.net;

import com.google.gson.Gson;
import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.*;

import java.io.IOException;


public class APICore {

    private static NetworkProvider networkProvider = new DefaultNetworkProvider();

    public static void setNetworkProvider(NetworkProvider np) {
        networkProvider = np;
    }

    public static final class Transactions {

        public static Transaction createByCode(String code, RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_TRANSACTION_BY_CODE, code);
            String bodyJsonString = new Gson().toJson(transactionParams);
            String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction createByCardId(String cardId,
                                                 RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_TRANSACTION_BY_CARD, cardId);
            String bodyJsonString = new Gson().toJson((transactionParams));
            String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction simulateByCardId(String cardId,
                                                   RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.SIMULATE_TRANSACTION_BY_CARD, cardId);
            String bodyJsonString = new Gson().toJson(transactionParams);
            String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction simulateByCode(String code, RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.SIMULATE_TRANSACTION_BY_CODE, code);
            String bodyJsonString = new Gson().toJson(transactionParams);
            String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction actionOnTransaction(String cardId,
                                                      String transactionId,
                                                      String action,
                                                      RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.ACTION_ON_TRANSACTION, cardId, transactionId, action);
            String bodyJsonString = new Gson().toJson((transactionParams));
            String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction retrieveByCardIdAndUserSuppliedId(String cardId,
                                                                    String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_TRANSACTION_BASED_ON_CARD_AND_USERSUPPLIEDID,
                    cardId, userSuppliedId);
            return new TransactionSearchResult(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null)).getOneTransaction();
        }

        public static Transaction retrieveByCodeAndUserSuppliedId(String code,
                                                                  String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_USERSUPPLIEDID,
                    code, userSuppliedId);
            return new TransactionSearchResult(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null)).getOneTransaction();
        }

        public static Transaction retrieveByCodeAndTransactionId(String code,
                                                                 String transactionId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_TRANSACTIONID,
                    code, transactionId);
            return new Transaction(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null));
        }

        public static Transaction retrieveByCardIdAndTransactionId(String cardId,
                                                                   String transactionId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_TRANSACTION_BASED_ON_CARD_AND_TRANSACTIONID,
                    cardId, transactionId);
            return new Transaction(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null));
        }
    }

    public static final class Cards {

        public static CardDetails retrieveDetailsByCardId(String cardId) throws AuthorizationException, IOException, CouldNotFindObjectException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_CARD_DETAILS_BASED_ON_CARD,
                    cardId);
            try {
                return new CardDetails(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null));
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static CardDetails retrieveDetailsByCode(String code) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_CARD_DETAILS_BASED_ON_CODE,
                    code);
            try {
                return new CardDetails(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null));
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Card create(RequestParameters createCardParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_CARD);
            String bodyJsonString = new Gson().toJson((createCardParams));
            try {
                return new Card(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString));
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Card retrieveByCardId(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CARD_BY_CARD_ID, cardId);
            try {
                return new Card(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null));
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Card retrieveByUserSuppliedId(String userSuppliedId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CARD_BY_USERSUPPLIED_ID, userSuppliedId);
            try {
                return new CardSearchResult(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null)).getOneCard();
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static CardSearchResult retrieveCardsOfContact(String contactId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CONTACT_CARDS, contactId);
            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
                return new CardSearchResult(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Transaction actionOnCard(String cardId, String action, RequestParameters params) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.ACTION_ON_CARD, cardId, action);
            String bodyJsonString = new Gson().toJson((params));

            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
                return new Transaction(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Card cancel(String cardId, RequestParameters params) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CANCEL_CARD, cardId);
            String bodyJsonString = new Gson().toJson((params));

            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
                return new Card(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static FullCode retrieveFullCode(String contactId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_FULL_CODE, contactId);
            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
                return new FullCode(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }


    }

    public static final class Contacts {

        public static Contact create(RequestParameters createContactParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_CONTACT);
            String bodyJsonString = new Gson().toJson((createContactParams));
            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
                return new Contact(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Contact retrieve(String contactId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CONTACT, contactId);
            System.out.println(urlSuffix);
            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
                return new Contact(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Contact retrieveByUserSuppliedId(String userSuppliedId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CONTACT_BY_USERSUPPLIED_ID, userSuppliedId);
            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
                return new ContactSearchResult(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null)).getOneContact();
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }
    }


}
