package com.lightrail.model.api.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.*;
import com.lightrail.model.business.AccountCard;
import com.lightrail.model.business.RequestParametersCreateContact;

import java.io.IOException;


public class APICore {

    private static NetworkProvider networkProvider = new DefaultNetworkProvider();

    public static void setNetworkProvider(NetworkProvider np) {
        networkProvider = np;
    }

    public static final class Transactions {

        static Gson gson = new Gson();

        public static Transaction createByCode(String code, RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_TRANSACTION_BY_CODE, code);
            String bodyJsonString = gson.toJson(transactionParams);
            String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction createByCardId(String cardId,
                                                 RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_TRANSACTION_BY_CARD, cardId);
            String bodyJsonString = gson.toJson(transactionParams);
            String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction simulateByCardId(String cardId,
                                                   RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.SIMULATE_TRANSACTION_BY_CARD, cardId);
            String bodyJsonString = gson.toJson(transactionParams);
            String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction simulateByCode(String code, RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.SIMULATE_TRANSACTION_BY_CODE, code);
            String bodyJsonString = gson.toJson(transactionParams);
            String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction actionOnTransaction(String cardId,
                                                      String transactionId,
                                                      String action,
                                                      RequestParameters transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.ACTION_ON_TRANSACTION, cardId, transactionId, action);
            String bodyJsonString = gson.toJson(transactionParams);
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

        static Gson gson = new Gson();

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
            String bodyJsonString = gson.toJson(createCardParams);
            try {
                return new Card(networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString));
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Card create(RequestParamsCreateAccountByContactId createCardParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_CARD);
            String bodyJsonString = gson.toJson(createCardParams);
            try {
                String cardResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
                String card = gson.fromJson(cardResponse, JsonObject.class).get("card").toString();
                return gson.fromJson(card, Card.class);
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

        public static Card retrieveAccountCardByContactIdAndCurrency(String contactId, String currency) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_ACCOUNT_CARD_BY_CURRENCY, contactId, currency);
            try {
                String response = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
                JsonArray jsonResponse = gson.fromJson(response, JsonObject.class).getAsJsonArray("cards");
                if (jsonResponse.size() > 0) {
                    String jsonCard = jsonResponse.get(0).toString();
                    return gson.fromJson(jsonCard, AccountCard.class);
                } else {
                    throw new CouldNotFindObjectException("Could not find that card");
                }
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
            String bodyJsonString = gson.toJson(params);

            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
                return new Transaction(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Card cancel(String cardId, RequestParameters params) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CANCEL_CARD, cardId);
            String bodyJsonString = gson.toJson(params);

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

        static Gson gson = new Gson();

        public static Contact create(RequestParameters createContactParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_CONTACT);
            String bodyJsonString = gson.toJson(createContactParams);
            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
                JsonObject jsonContact = gson.fromJson(rawAPIResponse, JsonObject.class);
                return gson.fromJson(jsonContact.get("contact").getAsString(), Contact.class);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Contact create(RequestParametersCreateContact createContactParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_CONTACT);
            String bodyJsonString = gson.toJson(createContactParams);
            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
                JsonElement jsonContact = gson.fromJson(rawAPIResponse, JsonObject.class).get("contact");
                return gson.fromJson(gson.toJson(jsonContact), Contact.class);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Contact retrieve(String contactId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CONTACT, contactId);
            System.out.println(urlSuffix);
            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
                JsonElement jsonContact = gson.fromJson(rawAPIResponse, JsonObject.class).get("contact");
                return gson.fromJson(gson.toJson(jsonContact), Contact.class);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Contact retrieveByUserSuppliedId(String userSuppliedId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CONTACT_BY_USERSUPPLIED_ID, userSuppliedId);
            try {
                String rawAPIResponse = networkProvider.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
                return gson.fromJson(rawAPIResponse, ContactSearchResult.class).getOneContact();
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }
    }


}
