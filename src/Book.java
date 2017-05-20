import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chrx on 4/6/17.
 */
public class Book {

    private double length, height;
    private String author;
    private Date publicationDate;
    ArrayList<String> genres;

    public Book(double length, double height, String author, Date publicationDate, ArrayList<String> genres) {
        this.length = length;
        this.height = height;
        this.author = author;
        this.publicationDate = publicationDate;
        this.genres = genres;
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
