package com.example.demo.server.servant;

public interface InvocInterface {
    byte[] handleRequest(String clientAddress, int clientRequestId, int clientPort, String unmarshalledData);
}
