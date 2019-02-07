package com.lightrail.network;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.model.PaginatedList;

import java.io.IOException;

public interface NetworkProvider {
    <T> T get(String path, Class<T> responseType) throws LightrailRestException, IOException;

    <T> PaginatedList<T> getPaginatedList(String path, Class<T> responseElementType) throws LightrailRestException, IOException;

    <T> T post(String path, Object body, Class<T> responseType) throws LightrailRestException, IOException;

    <T> T patch(String path, Object body, Class<T> responseType) throws LightrailRestException, IOException;

    <T> T put(String path, Object body, Class<T> responseType) throws LightrailRestException, IOException;

    <T> T delete(String path, Class<T> responseType) throws LightrailRestException, IOException;

    <T> T request(String method, String path, Object body, Class<T> responseType) throws LightrailRestException, IOException;

    <T> PaginatedList<T> requestPaginatedList(String method, String path, Object body, Class<T> responseType) throws LightrailRestException, IOException;
}
