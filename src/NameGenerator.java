import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;

/** Class: NameGenerator
 * @version 1.1
 * @author Joseph Wang
 * @date 12/2/2019
 * @description: A random name generator! This will read in names from a file and choose a random name from those
 *              names as needed.
 */

class NameGenerator {
    private ArrayList<String> maleFirstNames, femaleFirstNames, lastNames, zombieNames, plantNames;

    //// CONSTRUCTORS ////
    /** NameGenerator(); 
     * Opens up all the name files and stores them all in their respective names list.
     * 
     * @throws Exception, in case it cannot find the file.
     */
    NameGenerator() throws Exception {
        maleFirstNames = new ArrayList<String>();
        femaleFirstNames = new ArrayList<String>();
        lastNames = new ArrayList<String>();
        zombieNames = new ArrayList<String>();
        plantNames = new ArrayList<String>();

        File femNameFile = new File("./names/femaleNames.txt");
        File maleNameFile = new File("./names/maleNames.txt");
        File lastNameFile = new File("./names/lastNames.txt");
        File zombieNameFile = new File("./names/zombieNames.txt");
        File plantNameFile = new File("./names/plantNames.txt");

        Scanner femaleNameInput = new Scanner(femNameFile);
        Scanner maleNameInput = new Scanner(maleNameFile);
        Scanner lastNameInput = new Scanner(lastNameFile);
        Scanner zombieNameInput = new Scanner(zombieNameFile);
        Scanner plantNameInput = new Scanner(plantNameFile);

        while(femaleNameInput.hasNext()) {
            String name = femaleNameInput.nextLine();
            this.femaleFirstNames.add(name);
        }

        while(maleNameInput.hasNext()) {
            String name = maleNameInput.nextLine();
            this.maleFirstNames.add(name);
        }

        while(lastNameInput.hasNext()) {
            String name = lastNameInput.nextLine();
            this.lastNames.add(name);
        }

        while(zombieNameInput.hasNext()) {
            String name = zombieNameInput.nextLine();
            this.zombieNames.add(name);
        }

        while(plantNameInput.hasNext()) {
            String name = plantNameInput.nextLine();
            this.plantNames.add(name);
        }

        femaleNameInput.close();
        maleNameInput.close();
        lastNameInput.close();
        zombieNameInput.close();
        plantNameInput.close();
    }
    //// CONSTRUCTORS ////

    /** generateFirstName(gender); 
     * Generates a random first name from a list of first names based on the gender.
     * @param gender, a char that determines which list it gets a first name from.
     * @return a random first name.
     */
    public String generateFirstName(char gender) {
        int choice;

        if (gender == 'M') {
            choice = Simulation.randInt(0, this.maleFirstNames.size());
            return this.maleFirstNames.get(choice);
        } else {
            choice = Simulation.randInt(0, this.femaleFirstNames.size());
            return this.femaleFirstNames.get(choice);
        }
    }
    /** generateLastName(); 
     * Generates a random last name from a list of last names.
     * @return a random last name.
     */
    public String generateLastName() {
        int choice = Simulation.randInt(0, this.lastNames.size());

        return this.lastNames.get(choice);
    }

    /** generateZombieName(); 
     * Generates a random zombie name from a list of zombie names.
     * @return a random zombie name.
     */
    public String generateZombieName() {
        int choice = Simulation.randInt(0, this.zombieNames.size());

        return this.zombieNames.get(choice);
    }

    /** generatePlantName(); 
     * Generates a random plant name from a list of plant names.
     * @return a random plant name.
     */
    public String generatePlantName() {
        int choice = Simulation.randInt(0, this.plantNames.size());

        return this.plantNames.get(choice);
    }
}