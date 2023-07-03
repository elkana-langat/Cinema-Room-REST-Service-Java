package cinema.entity;

public class ReturnedTicket {

    private Seat returned_ticket;

    public ReturnedTicket() {
    }

    public ReturnedTicket(Seat returned_ticket) {
        this.returned_ticket = returned_ticket;
    }

    public Seat getReturned_ticket() {
        return returned_ticket;
    }

    public void setReturned_ticket(Seat returned_ticket) {
        this.returned_ticket = returned_ticket;
    }
}
