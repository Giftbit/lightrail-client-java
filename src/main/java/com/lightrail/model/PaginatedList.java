package com.lightrail.model;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.network.NetworkProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaginatedList<T> extends ArrayList<T> {

    private final static Pattern linkRegex = Pattern.compile("<([^>]+)>;\\s*rel=\"([a-zA-Z]+)\"");

    private int maxLimit;

    // Links support.
    private NetworkProvider networkProvider;
    private Class<T> elementType;
    private Map<String, String> links = new HashMap<>();

    public int getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(int maxLimit) {
        this.maxLimit = maxLimit;
    }

    public void setLinks(String links, NetworkProvider networkProvider, Class<T> elementType) {
        this.networkProvider = networkProvider;
        this.elementType = elementType;

        String[] linkLines = links.split(",");
        for (String linkLine : linkLines) {
            Matcher matcher = linkRegex.matcher(linkLine);
            if (matcher.matches()) {
                this.links.put(matcher.group(2), matcher.group(1));
            }
        }
    }

    public boolean hasFirst() {
        return links.containsKey("first");
    }

    public PaginatedList<T> getFirst() throws LightrailRestException, IOException {
        if (!hasPrevious()) {
            throw new IllegalStateException("List does not have first.");
        }
        return networkProvider.getPaginatedList(".." + links.get("first"), elementType);
    }

    public boolean hasPrevious() {
        return links.containsKey("prev");
    }

    public PaginatedList<T> getPrevious() throws LightrailRestException, IOException {
        if (!hasPrevious()) {
            throw new IllegalStateException("List does not have previous.");
        }
        return networkProvider.getPaginatedList(".." + links.get("prev"), elementType);
    }

    public boolean hasNext() {
        return links.containsKey("next");
    }

    public PaginatedList<T> getNext() throws LightrailRestException, IOException {
        if (!hasNext()) {
            throw new IllegalStateException("List does not have next.");
        }
        return networkProvider.getPaginatedList(".." + links.get("next"), elementType);
    }

    public boolean hasLast() {
        return links.containsKey("last");
    }

    public PaginatedList<T> getLast() throws LightrailRestException, IOException {
        if (!hasNext()) {
            throw new IllegalStateException("List does not have last.");
        }
        return networkProvider.getPaginatedList(".." + links.get("last"), elementType);
    }
}
