import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chrx on 4/6/17.
 */
public class Book {

    private String title;
    private double length, height;
    private String author;
    private Date publicationDate;
    ArrayList<String> genres;

    public Book(String title, double length, double height, String author, Date publicationDate, ArrayList<String> genres) {
        this.title = title;
        this.length = length;
        this.height = height;
        this.author = author;
        this.publicationDate = publicationDate;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public double getLength() {
        return length;
    }

    public double getHeight() {
        return height;
    }
}
