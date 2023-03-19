package com.example.demo.server;

import com.example.demo.server.servant.InvocInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class AtMostOnceInvoc implements InvocInterface {
    private HashMap<InvocationKey, byte[]> InvocationHistory = new HashMap<InvocationKey, byte[]>();
    public AtMostOnceInvoc() {}

    @Override
    public byte[] handleRequest(String clientAddress, int clientRequestId, int clientPort, String unmarshalledData) {
        InvocationKey invocationKey = new InvocationKey(clientAddress, clientPort, clientRequestId);
        byte[] res;
        if (InvocationHistory.containsKey(invocationKey)) {
            res = InvocationHistory.get(invocationKey);
            log.warn("!!! Duplicate request detected. Returning cached reply");
        } else {
            res = (unmarshalledData + " ATMOSTONCE").getBytes();
            InvocationHistory.put(invocationKey, res);
        }
        return res;
    }
}
