package com.example.demo.server;

import com.example.demo.server.servant.InvocInterface;

public class AtLeastOnceInvoc implements InvocInterface {
    public AtLeastOnceInvoc() {}
    @Override
    public byte[] handleRequest(String hostAddress, int clientRequestId, int clientPort, String unmarshalledData) {
        String res = unmarshalledData + " ATLEASTONCE";
        return res.getBytes();
    }
}
