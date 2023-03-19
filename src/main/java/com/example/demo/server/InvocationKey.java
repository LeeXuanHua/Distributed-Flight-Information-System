package com.example.demo.server;

public class InvocationKey {
    private String clientAddress;
    private int clientRequestId;
    private int clientPort;


    public InvocationKey(String clientAddress, int clientRequestId, int clientPort) {
        this.clientAddress = clientAddress;
        this.clientRequestId = clientRequestId;
        this.clientPort = clientPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvocationKey that = (InvocationKey) o;

        if (clientPort != that.clientPort) return false;
        if (clientRequestId != that.clientRequestId) return false;
        return clientAddress.equals(that.clientAddress);
    }

    @Override
    public int hashCode() {
        int result = clientAddress.hashCode();
        result = 31 * result + clientPort;
        result = 31 * result + clientRequestId;
        return result;
    }
}
