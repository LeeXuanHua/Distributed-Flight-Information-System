package com.example.demo.server.servant;

public interface InvocInterface {
    byte[] handleRequest(String hostAddress, int clientRequestId, int clientPort, String unmarshalledData);
}
