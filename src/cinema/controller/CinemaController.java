package cinema.controller;

import cinema.entity.*;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CinemaController {
    private final List<Seat> availableSeats = new ArrayList<>();
    private final Map<UUID, Seat> boughtTickets = new HashMap<>();
    private int currentIncome = 0;
    private int numberOfSeatsAvailable = 81;
    private int numberOfTicketsPurchased = 0;

    @PostConstruct
    public void init() {
        int totalRows = 9;
        int totalColumns = 9;

        for (int i = 1; i <= totalRows; i++) {
            for (int j = 1; j <= totalColumns; j++) {
                if (i <= 4) {
                    Seat seat = new Seat(i, j, 10);
                    availableSeats.add(seat);
                } else {
                    Seat seat = new Seat(i, j, 8);
                    availableSeats.add(seat);
                }
            }
        }
    }

    @GetMapping(path = "/seats", produces = MediaType.APPLICATION_JSON_VALUE)
    public CinemaInfo getSeats() {
        return new CinemaInfo(9, 9, availableSeats);
    }

    @PostMapping(path = "/purchase", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> purchaseSeat(@RequestBody ReceivedSeat seat) {
        int price;
        if (seat.getRow() <= 4) {
            price = 10;
        } else {
            price = 8;
        }


        Seat requestedSeat = new Seat(seat.getRow(), seat.getColumn(), price);

        int rows = requestedSeat.getRow();
        int columns = requestedSeat.getColumn();

        if (isSeatObjectInList(availableSeats, requestedSeat)) {
            currentIncome += price;
            numberOfSeatsAvailable -= 1;
            numberOfTicketsPurchased += 1;
            availableSeats.remove(requestedSeat);
            UUID token = UUID.randomUUID();
            TokenResponse tokenResponse = new TokenResponse(token, requestedSeat);
            boughtTickets.put(token, requestedSeat);
            return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        } else if (rows > 9 || rows < 1 || columns < 1 || columns > 9) {
            ErrorClass errorClass = new ErrorClass("The number of a row or a column is out of bounds!");
            return new ResponseEntity<>(errorClass, HttpStatus.BAD_REQUEST);
        } else {
            ErrorClass errorClass = new ErrorClass("The ticket has been already purchased!");
            return new ResponseEntity<>(errorClass, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(path = "/return")
    public ResponseEntity<Object> returnTicket(@RequestBody Token token) {
        UUID tokens = UUID.fromString(token.getToken());

        if (boughtTickets.containsKey(tokens)) {
            Seat seat = new Seat(boughtTickets.get(tokens));
            currentIncome -= seat.getPrice();
            numberOfSeatsAvailable += 1;
            numberOfTicketsPurchased -= 1;
            availableSeats.add(seat);
            boughtTickets.remove(tokens);
            ReturnedTicket returnedTicket = new ReturnedTicket(seat);
            return new ResponseEntity<>(returnedTicket, HttpStatus.OK);
        } else {
            ErrorClass errorClass = new ErrorClass("Wrong token!");
            return new ResponseEntity<>(errorClass, HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam(value = "password", required = false) String password) {
        if (password == null) {
            ErrorClass errorClass = new ErrorClass("The password is wrong!");
            return new ResponseEntity<>(errorClass, HttpStatus.UNAUTHORIZED);
        } else {
            TheatreStats theatreStats = new TheatreStats(currentIncome, numberOfSeatsAvailable, numberOfTicketsPurchased);
            return new ResponseEntity<>(theatreStats, HttpStatus.OK);
        }
    }


    private boolean isSeatObjectInList(List<Seat> seats, Seat requestedSeat) {
        Iterator<Seat> iterator = seats.iterator();
        while (iterator.hasNext()) {
            Seat seat = iterator.next();
            if (seat.getRow() == requestedSeat.getRow()
                    && seat.getColumn() == requestedSeat.getColumn()
                    && seat.getPrice() == requestedSeat.getPrice()) {
                iterator.remove(); // Remove the seat from the list
                return true;
            }
        }
        return false;
    }
}
