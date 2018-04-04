package com.lightrail.network;

import com.lightrail.LightrailClient;
import com.lightrail.model.LightrailException;
import com.lightrail.params.CardSearchParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.lang.String.format;

public class EndpointBuilder {
    private LightrailClient lr;

    public EndpointBuilder(LightrailClient lr) {
        this.lr = lr;
    }

    public final String apiBaseURL = "https://api.lightrail.com/v1/";

    public String createTransaction(String cardId) throws LightrailException {
        return format("cards/%s/transactions", urlEncode(cardId));
    }

    public String getTransaction(String cardId, String transactionId) throws LightrailException {
        return format("cards/%s/transactions/%s", urlEncode(cardId), urlEncode(transactionId));
    }

    public String voidPendingTransaction(String cardId, String transactionId) throws LightrailException {
        return getTransaction(cardId, transactionId) + "/void";
    }

    public String capturePendingTransaction(String cardId, String transactionId) throws LightrailException {
        return getTransaction(cardId, transactionId) + "/capture";
    }

    public String dryRunTransaction(String cardId) throws LightrailException {
        return createTransaction(cardId) + "/dryRun";
    }

    public String retrieveContactById(String contactId) throws LightrailException {
        return format("contacts/%s", urlEncode(contactId));
    }

    public String searchContactByUserSuppliedId(String userSuppliedId) throws LightrailException {
        return format("contacts?userSuppliedId=%s", urlEncode(userSuppliedId));
    }

    public String createCard() {
        return "cards";
    }

    public String getCardDetails(String cardId) throws LightrailException {
        return format("cards/%s/details", urlEncode(cardId));
    }

    public String searchCardsByParams(CardSearchParams params) throws LightrailException {
        String urlQuery = "cards?";

        if (params.cardType != null && !params.cardType.isEmpty()) {
            urlQuery = urlQuery + "cardType=" + urlEncode(params.cardType) + "&";
        }
        if (params.userSuppliedId != null && !params.userSuppliedId.isEmpty()) {
            urlQuery = urlQuery + "userSuppliedId=" + urlEncode(params.userSuppliedId) + "&";
        }
        if (params.contactId != null && !params.contactId.isEmpty()) {
            urlQuery = urlQuery + "contactId=" + urlEncode(params.contactId) + "&";
        }
        if (params.currency != null && !params.currency.isEmpty()) {
            urlQuery = urlQuery + "currency=" + urlEncode(params.currency) + "&";
        }

        return urlQuery;
    }

    public String createContact() {
        return "contacts";
    }

    public String createProgram() {
        return "programs";
    }

    public String retrieveProgram(String programId) throws LightrailException {
        return format("programs/%s", urlEncode(programId));
    }

    private String urlEncode(String string) throws LightrailException {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new LightrailException("Could not URL-encode the parameter " + string);
        }
    }
}
