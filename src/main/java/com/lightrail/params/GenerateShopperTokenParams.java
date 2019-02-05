package com.lightrail.params;

import com.google.gson.JsonObject;

public class GenerateShopperTokenParams {
    public String contactId = "";
    public int validityInSeconds = 43200;
    public JsonObject metadata = null;
}
