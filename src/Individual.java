import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Individual
{
    private int size;
    private int[] genes; //TODO: BOOLEAN[] ?
    private int bitsPerBook;
    private int noBooks;
    private double fitnessValue;

    private int neededBits(int x)
    {
        return (int) Math.floor(Math.log(x) / Math.log(2)) + 1;
    }

    public Individual(int noBooks) {
        this.noBooks = noBooks;
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

    public void setFitnessValue(int fitnessValue) {
        this.fitnessValue = fitnessValue;
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
//            System.out.print(randomPick);
//            int randomPick = 0;
//            System.out.print(" ");
            currentNr = Integer.toBinaryString(randomPick);
            while(currentNr.length() != bitsPerBook)
                currentNr = "0" + currentNr;
            for (char c: currentNr.toCharArray()) {
                this.genes[currentBit] = Character.getNumericValue(c);
                currentBit++;
            }
            nrs.remove(randomPick);
        }
//        System.out.println(Arrays.toString(genes));
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

    private ArrayList<Book> getShelfBooks(int shelfNumber, ArrayList<Point2D.Double> shelves, int firstBook, ArrayList<Book> books) {
        double accumulatedLength = 0;
        ArrayList<Book> shelfBooks = new ArrayList<>();
        int bookIndex = getNthBookIndex(firstBook);
        while((accumulatedLength += books.get(bookIndex).getLength()) < shelves.get(shelfNumber).x) {
            shelfBooks.add(books.get(bookIndex));
            books.remove(bookIndex);
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

    private boolean booksAreDateSorted(ArrayList<Book> books) {
        for (int i = 0; i < books.size() - 1; i++) {
            if(!(books.get(i).getPublicationYear() < books.get(i+1).getPublicationYear()))
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
        int noBooksCurrentAuthor = 0;
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

            if(currentBook.getAuthor().equals(currentAuthor))
                noBooksCurrentAuthor++;
            else {
                if(!booksAreDateSorted(new ArrayList<>(shelfBooks.subList(i-noBooksCurrentAuthor, i))))
                    noDatePens++;
                currentAuthor = currentBook.getAuthor();
                noBooksCurrentAuthor = 1;
            }

            if(!hasCommonGenre(currentBook.getGenres(), nextBook.getGenres()))
                noGenrePens++;

            // /se o i+1 for autor !=, ve se existe mais alguma vez este autor do i+2 para a frente, se sim, penaliza
            //altura se variar > que 20% de altura do livro
            //se o a seguir nao tiver nenhum tema em comum
            //ir juntando livros do current author, quando for diferente ver se estao ordenados
        }

        double filledSpaceRatio = totalBooksLength/shelfDimensions.x;
        double authorPen = noAuthorPens > 5 ? 0.25*filledSpaceRatio : (double) noAuthorPens/20;
        double datePen = noDatePens > 5 ? 0.15*filledSpaceRatio : (double) noDatePens/35;
        double heightPen = noHeightPens > 5 ? 0.15*filledSpaceRatio : (double) noHeightPens/35;
        double genrePen = noGenrePens > 5 ? 0.1*filledSpaceRatio : (double) noGenrePens/50;

        return filledSpaceRatio - authorPen - datePen - heightPen - genrePen;
    }

    public double evaluate(ArrayList<Book> books, ArrayList<Point2D.Double> shelves) {
        double fitness = 0;

        int currentBook = 0;
        int currentShelf = 0;

        ArrayList<Book> tmpBooks = new ArrayList<>(books);

        while(currentBook != this.noBooks) {
            try {
                ArrayList<Book> shelfBooks = getShelfBooks(currentShelf, shelves, currentBook, tmpBooks);
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
}