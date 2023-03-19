package com.example.demo.server;

import com.example.demo.server.servant.InvocInterface;

public class AtMostOnceInvoc implements InvocInterface {
    public AtMostOnceInvoc() {}
    @Override
    public byte[] handleRequest(String hostAddress, int clientRequestId, int clientPort, String unmarshalledData) {
        String res = unmarshalledData + " ATMOSTONCE";
        return res.getBytes();
    }
}
