package com.distributedflightinfosys.server;

import com.distributedflightinfosys.server.servant.models.DataEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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

    public String getClientDisplay() {
        if (!response.isPresent()) return toString();

        Object responseObject = response.get();
        String res = "";
        if (responseObject instanceof ArrayList) {
            for (Object responseItem: (ArrayList<Object>) responseObject) {
                res += "\n" + ((DataEntity) responseItem).getClientDisplay() + "\n";
            }
        } else {
            res = ((DataEntity) responseObject).getClientDisplay();
        }
        return res;
    }
}
