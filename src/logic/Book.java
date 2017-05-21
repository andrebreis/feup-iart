package logic;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chrx on 4/6/17.
 */
public class Book {

    private String title;
    private double length, height;
    private String author;
    private int publicationYear;
    private ArrayList<String> genres;

    public Book(String title, double length, double height, String author, int publicationYear) {
        this.title = title;
        this.length = length;
        this.height = height;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public double getLength() {
        return length;
    }

//    @Override
//    public String toString() {
//        return "logic.Book{" +
//                "title=" + title +
//                ", length=" + length +
//                ", height=" + height +
//                ", author='" + author +
//                ", publicationYear=" + publicationYear +
//                ", genres=" + genres +
//                '}';
//    }


    @Override
    public String toString() {
        return "Book{" +
                "title=" + title +
                ", author=" + author +
                ", publicationYear=" + publicationYear +
                '}';
    }

    public double getHeight() {
        return height;
    }
}
