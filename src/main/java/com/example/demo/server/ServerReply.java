package com.example.demo.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServerReply {
    private boolean isSuccess;
    private String serverMsg;
    private Optional<Object> response;

    @Override
    public String toString() {
        return "Operation " + (this.isSuccess() ? "succeeded." : "failed.") +
                " ServerMsg='" + this.getServerMsg() + "'" +
                ", response=" + this.getResponse();
    }
}
