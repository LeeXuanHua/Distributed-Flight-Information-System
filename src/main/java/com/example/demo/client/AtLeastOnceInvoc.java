package com.example.demo.client;

import com.example.demo.server.servant.InvocInterface;

public class AtLeastOnceInvoc implements InvocInterface {
    public AtLeastOnceInvoc() {}

    // TODO: implement
    @Override
    public byte[] handleRequest(String clientAddress, int clientRequestId, int clientPort, String unmarshalledData) {
        String res = unmarshalledData + " ATLEASTONCE";
        return res.getBytes();
    }
}
