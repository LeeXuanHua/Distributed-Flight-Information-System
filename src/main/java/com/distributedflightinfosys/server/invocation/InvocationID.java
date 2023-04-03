package com.distributedflightinfosys.server.invocation;

import com.distributedflightinfosys.server.servant.models.ClientID;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class InvocationID {
    private ClientID clientId;
    private int clientMessageId;

    @Override
    // Override comparison function to allow checking for duplicate InvocationIDs
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvocationID that = (InvocationID) o;

        if (clientMessageId != that.clientMessageId) return false;
        return Objects.equals(clientId, that.clientId);
    }

    @Override
    // Hashcode used to easily uniquely identify InvocationIDs
    public int hashCode() {
        int result = clientId != null ? clientId.hashCode() : 0;
        result = 31 * result + clientMessageId;
        return result;
    }
}
