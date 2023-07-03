package cinema.entity;

import java.util.List;
import java.util.UUID;

public class TokenResponse {

    private UUID token;
    private Seat ticket;

    public TokenResponse() {
    }

    public TokenResponse(UUID token, Seat ticket) {
        this.token = token;
        this.ticket = ticket;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public Seat getTicket() {
        return ticket;
    }

    public void setTicket(Seat ticket) {
        this.ticket = ticket;
    }
}
