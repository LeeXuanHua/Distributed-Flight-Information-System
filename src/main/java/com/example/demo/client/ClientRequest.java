package com.example.demo.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ClientRequest {
    private int messageId;
    private int serviceId;
    private String requestBody;

    public ClientRequest(int messageId, int serviceId, String requestBody) {
        this.messageId = messageId;
        this.serviceId = serviceId;
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        return "{" +
                " messageId='" + getMessageId() + "'" +
                ", serviceId='" + getServiceId() + "'" +
                ", requestBody='" + getRequestBody() + "'" +
                "}";
    }
}
