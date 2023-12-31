package moviebookingsystem.contract.request;

import lombok.Getter;
import lombok.Setter;
import moviebookingsystem.model.Movie;
import moviebookingsystem.model.ShowTime;

@Getter
@Setter
public class BookingRequest {
    private String customerName;
    private ShowTime showTime;
    private Movie movie;
}
