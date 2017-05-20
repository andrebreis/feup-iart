import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Population
{
    final static int ELITISM_K = 5;
    final static int POP_SIZE = 200 + ELITISM_K;  // population size
    final static int MAX_ITER = 2000;             // max number of iterations
    final static double MUTATION_RATE = 0.1;     // probability of mutation
    final static double CROSSOVER_RATE = 0.8;     // probability of crossover

    private static Random m_rand = new Random();  // random-number generator
    private Individual[] m_population;
    private double totalFitness;

    private ArrayList<Book> books;
    private ArrayList<Point2D.Double> shelves;

    public Population(String booksFilename, ArrayList<Point2D.Double> shelves) {

        this.books = parseBooks(booksFilename);
        this.shelves = shelves;

        m_population = new Individual[POP_SIZE];

        // init population
        for (int i = 0; i < POP_SIZE; i++) {
            m_population[i] = new Individual(this.books.size());
            m_population[i].randGenes();
        }

        // evaluate current population
        this.evaluate();
    }

    public ArrayList<Book> parseBooks(String filename) {

        BufferedReader br = null;
        String line = "";
        String splitCharacter = ";";

        boolean genresLine = false;
        ArrayList<Book> books = new ArrayList<>();
        Book tempBook = null;

        try {
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                if(genresLine) {
                    if(tempBook == null)
                        continue;
                    String[] genres = line.split(splitCharacter);
                    tempBook.setGenres(new ArrayList<String>(Arrays.asList(genres)));
                    books.add(tempBook);
                    tempBook = null;
                }
                else {
                    String[] parts = line.split(splitCharacter);
                    if(parts.length != 5) {
                        System.out.println("Invalid parts size on line: " + line);
                        continue;
                    }
                    String title = parts[0];
                    String author = parts[1];
                    int publicationYear = Integer.parseInt(parts[2]);
                    double height = Double.parseDouble(parts[3]);
                    double width = Double.parseDouble(parts[4]);
                    tempBook = new Book(title, width, height, author, publicationYear);
                }
                genresLine = !genresLine;
                // use comma as separator
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return books;
    }

    public void setPopulation(Individual[] newPop) {
        // this.m_population = newPop;
        System.arraycopy(newPop, 0, this.m_population, 0, POP_SIZE);
    }

    public Individual[] getPopulation() {
        return this.m_population;
    }

    public double evaluate() {
        this.totalFitness = 0.0;
        for (int i = 0; i < POP_SIZE; i++) {
            this.totalFitness += m_population[i].evaluate(this.books, this.shelves);
        }
        return this.totalFitness;
    }

    public Individual rouletteWheelSelection() {
        double randNum = m_rand.nextDouble() * this.totalFitness;
        int idx;
        for (idx=0; idx<POP_SIZE && randNum>0; ++idx) {
            randNum -= m_population[idx].getFitnessValue();
        }
        return m_population[idx-1];
    }

    public Individual findBestIndividual() {
        int idxMax = 0, idxMin = 0;
        double currentMax = 0.0;
        double currentMin = 1.0;
        double currentVal;

        for (int idx=0; idx<POP_SIZE; ++idx) {
            currentVal = m_population[idx].getFitnessValue();
            if (currentMax < currentMin) {
                currentMax = currentMin = currentVal;
                idxMax = idxMin = idx;
            }
            if (currentVal > currentMax) {
                currentMax = currentVal;
                idxMax = idx;
            }
            if (currentVal < currentMin) {
                currentMin = currentVal;
                idxMin = idx;
            }
        }

        //return m_population[idxMin];      // minimization
        return m_population[idxMax];        // maximization
    }

    public Individual[] crossover(Individual indiv1,Individual indiv2) {
        Individual[] newIndiv = new Individual[2];
        newIndiv[0] = new Individual(this.books.size());
        newIndiv[1] = new Individual(this.books.size());

        int randPoint = m_rand.nextInt(this.books.size());
        randPoint = randPoint * newIndiv[0].getBitsPerBook(); //assures that the crossover point doesnt break a book number
        int i;
        for (i=0; i<randPoint; ++i) {
            newIndiv[0].setGene(i, indiv1.getGene(i));
            newIndiv[1].setGene(i, indiv2.getGene(i));
        }
        for (; i<newIndiv[0].getSize(); ++i) {
            newIndiv[0].setGene(i, indiv2.getGene(i));
            newIndiv[1].setGene(i, indiv1.getGene(i));
        }

        return newIndiv;
    }


    // 211 total length, max height = 26
    public static void main(String[] args) {

        Point2D.Double shelf = new Point2D.Double(50, 30);
        ArrayList<Point2D.Double> shelves = new ArrayList<>();
        for(int i = 0; i < 5; i++) shelves.add(shelf);
        Population pop = new Population("sample1.csv", shelves);
        Individual[] newPop = new Individual[POP_SIZE];
        Individual[] indiv = new Individual[2];

        // current population
        System.out.print("Total Fitness = " + pop.totalFitness);
        System.out.println(" ; Best Fitness = " +
                pop.findBestIndividual().getFitnessValue());

        // main loop
        int count;
        for (int iter = 0; iter < MAX_ITER; iter++) {
            count = 0;

            // Elitism
            for (int i=0; i<ELITISM_K; ++i) {
                newPop[count] = pop.findBestIndividual();
                count++;
            }

            // build new Population
            while (count < POP_SIZE) {
                // Selection
                indiv[0] = pop.rouletteWheelSelection();
                indiv[1] = pop.rouletteWheelSelection();

                // Crossover
                if ( m_rand.nextDouble() < CROSSOVER_RATE ) {
                    indiv = pop.crossover(indiv[0], indiv[1]);
                }

//                // Mutation
                if ( m_rand.nextDouble() < MUTATION_RATE ) {
                    indiv[0].mutate();
                }
                if ( m_rand.nextDouble() < MUTATION_RATE ) {
                    indiv[1].mutate();
                }

                // add to new population
                newPop[count] = indiv[0];
                newPop[count+1] = indiv[1];
                count += 2;
            }
            pop.setPopulation(newPop);

            // reevaluate current population
            pop.evaluate();
            System.out.print("Total Fitness = " + pop.totalFitness);
            System.out.println(" ; Best Fitness = " +
                    pop.findBestIndividual().getFitnessValue());
        }

        // best indiv
        Individual bestIndiv = pop.findBestIndividual();

    }
}
