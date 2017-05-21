package logic;

import java.awt.geom.Point2D;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Random;

public class Individual
{
    private int size;
    private int[] genes;
    private int bitsPerBook;
    private int noBooks;
    private double fitnessValue;

    private static ArrayList<Book> books;
    private static ArrayList<Point2D.Double> shelves;

    private static int MAX_NUM_PENS = 5;
    private static double AUTHOR_PEN_VALUE = 0.2;
    private static double DATE_PEN_VALUE = 0.2;
    private static double HEIGHT_PEN_VALUE = 0.2;
    private static double GENRE_PEN_VALUE = 0.1;


    private int neededBits(int x)
    {
        return (int) Math.floor(Math.log(x) / Math.log(2)) + 1;
    }

    public static void changePenaltiesValues (int maxNumPens, double authorPenValue, double datePenValue, double heightPenValue, double genrePenValue){
        MAX_NUM_PENS = maxNumPens;
        AUTHOR_PEN_VALUE = authorPenValue;
        DATE_PEN_VALUE = datePenValue;
        HEIGHT_PEN_VALUE = heightPenValue;
        GENRE_PEN_VALUE = genrePenValue;
    }

    public Individual() {
        this.noBooks = books.size();
        this.bitsPerBook = neededBits(noBooks);
        this.size = bitsPerBook*noBooks;
        genes = new int[this.size];
    }

    public int getSize() {
        return size;
    }

    public double getFitnessValue() {
        return fitnessValue;
    }

    public static void setBooks(ArrayList<Book> books) {
        Individual.books = books;
    }

    public static void setShelves(ArrayList<Point2D.Double> shelves) {
        Individual.shelves = shelves;
    }

    public int getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, int gene) {
        this.genes[index] = gene;
    }

    public void randGenes() {
        ArrayList<Integer> nrs = new ArrayList<Integer>();
        for(int i = 0; i < noBooks; i++) nrs.add(i);

        Random rand = new Random();
        String currentNr = "";
        int currentBit = 0;
        while (!nrs.isEmpty()) {
            int randomPick = rand.nextInt(nrs.size());
            currentNr = Integer.toBinaryString(randomPick);
            while(currentNr.length() != bitsPerBook)
                currentNr = "0" + currentNr;
            for (char c: currentNr.toCharArray()) {
                this.genes[currentBit] = Character.getNumericValue(c);
                currentBit++;
            }
            nrs.remove(randomPick);
        }
    }

    public void mutate() {
        Random rand = new Random();
        int index = rand.nextInt(size);
        this.setGene(index, 1-this.getGene(index));    // flip
    }

    private int getNthBookIndex(int n){
        String bookGenes = "";
        for(int i = n*bitsPerBook; i < n*bitsPerBook+bitsPerBook; i++)
            bookGenes += genes[i];
        return Integer.parseInt(bookGenes, 2);
    }

    private ArrayList<Book> getShelfBooks(int shelfNumber, int firstBook, ArrayList<Book> tmpBooks) {
        double accumulatedLength = 0;
        ArrayList<Book> shelfBooks = new ArrayList<>();
        int bookIndex = getNthBookIndex(firstBook);
        while((accumulatedLength += tmpBooks.get(bookIndex).getLength()) < shelves.get(shelfNumber).x) {
            shelfBooks.add(tmpBooks.get(bookIndex));
            tmpBooks.remove(bookIndex);
            firstBook++;
            if(firstBook >= noBooks)
                break;
            bookIndex = getNthBookIndex(firstBook);
        }
        return shelfBooks;
    }

    private boolean containsAuthor(ArrayList<Book> list, String author) {
        for(Book b : list) {
            if(b.getAuthor().equals(author))
                return true;
        }
        return false;
    }

    private boolean booksAreDateSorted(ArrayList<Book> booksToCheck) {
        for (int i = 0; i < booksToCheck.size() - 1; i++) {
            if(!(booksToCheck.get(i).getPublicationYear() < booksToCheck.get(i+1).getPublicationYear()))
                return false;
        }
        return true;
    }

    private boolean hasCommonGenre(ArrayList<String> l1, ArrayList<String> l2) {
        for (String genre:
             l1) {
            if(l2.contains(genre))
                return true;
        }
        return false;
    }


    private double evaluateShelf(ArrayList<Book> shelfBooks, Point2D.Double shelfDimensions) {
        double totalBooksLength = 0;
        int noAuthorPens = 0;
        int noDatePens = 0;
        int noHeightPens = 0;
        int noGenrePens = 0;

        String currentAuthor=shelfBooks.get(0).getAuthor();
//        int noBooksCurrentAuthor = 0;
        for (int i = 0; i < shelfBooks.size() - 1; i++) {
            Book currentBook = shelfBooks.get(i);
            Book nextBook = shelfBooks.get(i+1);

            totalBooksLength += currentBook.getLength();
            if(currentBook.getHeight() > shelfDimensions.y)
                return 0;

            if(!currentBook.getAuthor().equals(nextBook.getAuthor())) {
                ArrayList<Book> nextElements = new ArrayList<>(shelfBooks.subList(i+2, shelfBooks.size()));
                if(containsAuthor(nextElements, currentBook.getAuthor()))
                    noAuthorPens++;
            }

            if(currentBook.getHeight() > nextBook.getHeight()*1.2 || currentBook.getHeight() < nextBook.getHeight() * 0.8)
                noHeightPens++;

            if(nextBook.getAuthor().equals(currentAuthor)) {
                if(!(nextBook.getPublicationYear() > currentBook.getPublicationYear()))
                    noDatePens++;
            }
            else
                currentAuthor = nextBook.getAuthor();

            if(!hasCommonGenre(currentBook.getGenres(), nextBook.getGenres()))
                noGenrePens++;

            // /se o i+1 for autor !=, ve se existe mais alguma vez este autor do i+2 para a frente, se sim, penaliza
            //altura se variar > que 20% de altura do livro
            //se o a seguir nao tiver nenhum tema em comum
            //ir juntando livros do current author, quando for diferente ver se estao ordenados
        }

        double filledSpaceRatio = totalBooksLength/shelfDimensions.x;
        double authorPen = noAuthorPens > MAX_NUM_PENS ? AUTHOR_PEN_VALUE*filledSpaceRatio : (double) noAuthorPens/(MAX_NUM_PENS/AUTHOR_PEN_VALUE);
        double datePen = noDatePens > MAX_NUM_PENS ? DATE_PEN_VALUE*filledSpaceRatio : (double) noDatePens/(MAX_NUM_PENS/DATE_PEN_VALUE);
        double heightPen = noHeightPens > MAX_NUM_PENS ? HEIGHT_PEN_VALUE*filledSpaceRatio : (double) noHeightPens/(MAX_NUM_PENS/HEIGHT_PEN_VALUE);
        double genrePen = noGenrePens > MAX_NUM_PENS ? GENRE_PEN_VALUE*filledSpaceRatio : (double) noGenrePens/(MAX_NUM_PENS/GENRE_PEN_VALUE);

        return filledSpaceRatio - authorPen - datePen - heightPen - genrePen;
    }

    public double evaluate() {
        double fitness = 0;
//maxnumpens/x = authorpenvalue, x = maxnumpens/authorpenvalue
        int currentBook = 0;
        int currentShelf = 0;

        ArrayList<Book> tmpBooks = new ArrayList<>(books);

        while(currentBook != this.noBooks) {
            try {
                ArrayList<Book> shelfBooks = getShelfBooks(currentShelf, currentBook, tmpBooks);
                fitness += evaluateShelf(shelfBooks, shelves.get(currentShelf));
                currentBook += shelfBooks.size();
                currentShelf++;
            } catch (IndexOutOfBoundsException e){
                this.fitnessValue = 0;
                return 0;
            }

        }

        this.fitnessValue = fitness;

        return fitness;
    }

    public int getBitsPerBook() {
        return bitsPerBook;
    }

    @Override
    public String toString() {
        String str = "Individual: ";
        for (int gene:
             genes) {
            str += gene;
        }
        str += "\nShelf1: ";

        double accumulatedLength = 0;
        int currentShelf = 0;

        ArrayList<Book> tmpBooks = new ArrayList<>(books);
        for (int i = 0; i <  noBooks; i++){
            int index = getNthBookIndex(i);
            Book b = tmpBooks.get(index);
            tmpBooks.remove(index);
            accumulatedLength += b.getLength();
            if(accumulatedLength > shelves.get(currentShelf).getX()){
                currentShelf++;
                accumulatedLength = b.getLength();
                str += "\nShelf " + (currentShelf+1) + ": ";
            }
            else if (i != 0)
                str += ", ";
            str += b;
        }
        return str;
    }

    private String analyzeShelf(ArrayList<Book> shelfBooks, Point2D.Double shelfDimensions) {
        double totalBooksLength = 0;
        int noAuthorPens = 0;
        int noDatePens = 0;
        int noHeightPens = 0;
        int noGenrePens = 0;

        String currentAuthor=shelfBooks.get(0).getAuthor();
//        int noBooksCurrentAuthor = 0;
        for (int i = 0; i < shelfBooks.size() - 1; i++) {
            Book currentBook = shelfBooks.get(i);
            Book nextBook = shelfBooks.get(i+1);

            totalBooksLength += currentBook.getLength();

            if(!currentBook.getAuthor().equals(nextBook.getAuthor())) {
                ArrayList<Book> nextElements = new ArrayList<>(shelfBooks.subList(i+2, shelfBooks.size()));
                if(containsAuthor(nextElements, currentBook.getAuthor()))
                    noAuthorPens++;
            }

            if(currentBook.getHeight() > nextBook.getHeight()*1.2 || currentBook.getHeight() < nextBook.getHeight() * 0.8)
                noHeightPens++;

            if(nextBook.getAuthor().equals(currentAuthor)) {
                if(!(nextBook.getPublicationYear() > currentBook.getPublicationYear()))
                    noDatePens++;
            }
            else
                currentAuthor = nextBook.getAuthor();

            if(!hasCommonGenre(currentBook.getGenres(), nextBook.getGenres()))
                noGenrePens++;
        }
        return String.format("Occupied Length / Total Length -> %f/%f\nAuthor Penalties: %d\nDate Penalties: %d\nHeight Penalties: %d\nGenre Penalties: %d\n",
                totalBooksLength, shelfDimensions.x, noAuthorPens, noDatePens, noHeightPens, noGenrePens);
    }

    public String analyze() {
        String str = "";
        int currentBook = 0;
        int currentShelf = 0;

        ArrayList<Book> tmpBooks = new ArrayList<>(books);

        while(currentBook != this.noBooks) {
            str += "Shelf " + (currentShelf+1) + ": ";
            ArrayList<Book> shelfBooks = getShelfBooks(currentShelf, currentBook, tmpBooks);
            str += analyzeShelf(shelfBooks, shelves.get(currentShelf));
            currentBook += shelfBooks.size();
            currentShelf++;
        }
        return str;
    }
}