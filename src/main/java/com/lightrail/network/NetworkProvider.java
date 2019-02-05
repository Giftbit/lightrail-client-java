package com.lightrail.network;

import com.lightrail.errors.LightrailRestException;

import java.io.IOException;

public interface NetworkProvider {
    <T> T get(String path, Class<T> responseType) throws LightrailRestException, IOException;

    <T> T post(String path, Object body, Class<T> responseType) throws LightrailRestException, IOException;

    <T> T patch(String path, Object body, Class<T> responseType) throws LightrailRestException, IOException;

    <T> T put(String path, Object body, Class<T> responseType) throws LightrailRestException, IOException;

    <T> T request(String method, String path, Object body, Class<T> responseType) throws LightrailRestException, IOException;
}
