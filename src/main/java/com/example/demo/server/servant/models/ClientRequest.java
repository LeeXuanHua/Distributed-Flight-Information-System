package com.example.demo.server.servant.models;

public class ClientRequest {
    private int messageId;
    private int serviceId;
    private String requestBody;

    public ClientRequest(int messageId, int serviceId, String requestBody) {
        this.messageId = messageId;
        this.serviceId = serviceId;
        this.requestBody = requestBody;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getRequestBody() {
        return this.requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
