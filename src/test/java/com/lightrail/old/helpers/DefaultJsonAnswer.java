package com.lightrail.old.helpers;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class DefaultJsonAnswer implements Answer {

    @Override
    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        return "{\"card\":{\"cardId\":\"12345\"}}";
    }
}
