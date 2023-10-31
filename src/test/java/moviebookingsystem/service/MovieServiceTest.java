package moviebookingsystem.service;

import moviebookingsystem.constant.Genre;
import moviebookingsystem.contract.request.BookingRequest;
import moviebookingsystem.contract.request.MovieRequest;
import moviebookingsystem.model.Booking;
import moviebookingsystem.model.Member;
import moviebookingsystem.model.Movie;
import moviebookingsystem.model.Seat;
import moviebookingsystem.model.ShowTime;
import moviebookingsystem.repository.BookingRepository;
import moviebookingsystem.repository.MemberRepository;
import moviebookingsystem.repository.MovieRepository;
import moviebookingsystem.repository.SeatRepository;
import moviebookingsystem.repository.ShowTimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MovieServiceTest {
    private  MovieRepository movieRepository;
    private ShowTimeRepository showTimeRepository;
    private BookingRepository bookingRepository;
    private SeatRepository seatRepository;
    private MemberRepository memberRepository;
    private ModelMapper modelMapper;
    private MovieService movieService = new MovieService(null,null,null,null,null,null);
    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        movieRepository = Mockito.mock(MovieRepository.class);
        showTimeRepository = Mockito.mock(ShowTimeRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        seatRepository = Mockito.mock(SeatRepository.class);
        memberRepository = Mockito.mock(MemberRepository.class);
        movieService = new MovieService(movieRepository,showTimeRepository,bookingRepository,seatRepository,memberRepository,modelMapper);
    }
    @Test
    void testAddMovie(){
        Movie entity = new Movie();
        entity.setName("Dracula");
        entity.setGenre(Genre.valueOf("HORROR"));
        entity.setId(1L);
        MovieRequest request = new MovieRequest(entity.getName(),entity.getGenre());

        when(movieRepository.save(any())).thenReturn(entity);
        long id = movieService.addMovie(request);
        assertEquals(id, 1L);
    }
    @Test
    void testGetAllMovies(){
        List<Movie> expectedMovies = Arrays.asList(new Movie(), new Movie());
        when(movieRepository.findAll()).thenReturn(expectedMovies);
        List<Movie> actualMovies = movieService.getAllMovies();
        assertEquals(expectedMovies,actualMovies);
        verify(movieRepository,times(1)).findAll();
    }
    @Test
    void testGetMovieById(){
        long movieId = 1L;
        Movie expectedMovie = new Movie();
        expectedMovie.setId(movieId);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(expectedMovie));
        Movie actualMovie = movieService.getMovieById(movieId);
        assertEquals(expectedMovie,actualMovie);
        verify(movieRepository,times(1)).findById(movieId);
    }
    @Test
    void testGetMovieByIdNotFound(){
        long movieId = 1L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            movieService.getMovieById(movieId);
        });
        String expectedMessage = "Movie not found";
        String actualMessage = exception.getMessage();
       assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void testUpdateMovieById(){
        long id = 1L;
        Movie oldMovie = new Movie();
        oldMovie.setId(id);
        MovieRequest movieRequest = new MovieRequest();
        movieRequest.setName("Dark");
        movieRequest.setGenre(Genre.valueOf("ACTION"));
        Movie newMovie = new Movie();
        newMovie.setId(id);
        when(movieRepository.findById(id)).thenReturn(Optional.of(oldMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(newMovie);
        Long updatedMovieId = movieService.updateMovieById(id,movieRequest);
        assertEquals(id,updatedMovieId);
        verify(movieRepository,times(1)).findById(id);
        verify(movieRepository,times(1)).save(any(Movie.class));
    }
    @Test
    void testDeleteMovieById(){
        long id = 1L;
        when(movieRepository.existsById(id)).thenReturn(true);
        movieService.deleteMovieById(id);
        verify(movieRepository,times(1)).deleteById(id);
    }
    @Test
    void testGetAllMoviesByGenre(){
        Genre genre = Genre.ACTION;
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie("Movie1",Genre.ACTION));
        movies.add(new Movie("Movie2",Genre.DRAMA));
        movies.add(new Movie("Movie3",Genre.HORROR));
        movies.add(new Movie("Movie4",Genre.ROMANCE));
        movies.add(new Movie("Movie5",Genre.COMEDY));
        when(movieRepository.findAllByGenre(genre)).thenReturn(movies);
        List<Movie> result = movieService.getAllMoviesByGenre(genre);
        assertEquals(5,result.size());
    }
    @Test
    void testAddShowTimesToMovie(){
        Movie movie = new Movie();
        movie.setId(1L);
        ShowTime showTime = new ShowTime();
        showTime.setId(1L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showTimeRepository.save(showTime)).thenReturn(showTime);
        when(movieRepository.save(movie)).thenReturn(movie);

        Long movieId = movieService.addShowTimesToMovie(1L, Arrays.asList(showTime));

        assertEquals(1L, movieId);
        verify(showTimeRepository, times(1)).save(showTime);
        verify(movieRepository, times(1)).save(movie);
    }
    @Test
    public void testGetAllShowTimesForMovie() {
        Long movieId = 1L;
        ShowTime showTime1 = new ShowTime();
        ShowTime showTime2 = new ShowTime();
        List<ShowTime> expectedShowTimes = Arrays.asList(showTime1, showTime2);
        Movie movie = new Movie();
        movie.setShowTimes(expectedShowTimes);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        List<ShowTime> actualShowTimes = movieService.getAllShowTimesForMovie(movieId);
        assertEquals(expectedShowTimes, actualShowTimes);
    }
    @Test
    void testDeleteShowTime(){
        ShowTime showTime = new ShowTime();
        showTime.setId(1L);
        when(showTimeRepository.findById(1L)).thenReturn(Optional.of(showTime));
        movieService.deleteShowTime(1L);
        verify(showTimeRepository, times(1)).delete(showTime);
    }
}
