package com.lightrail.params.contacts;

public class AttachContactToValueParams {
    public String valueId;
    public String code;

    /**
     * This is a legacy shim that will be replaced by new functionality.
     */
    @Deprecated
    public Boolean attachGenericAsNewValue;
}
