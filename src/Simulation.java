import java.util.Scanner;
import java.lang.Math;

/** Class: Simulation
 * @version 3.1
 * @author Joseph Wang
 * @date 11/21/2019
 * @description: The file that controls everything, like the town, display, etc. This is the full project of cheerville, a simulation
 *               of Duber's hometown gone wrong.
 */

class Simulation {
  public static NameGenerator nameGenerator; //- For generating random names
  public static int maxHumanHP, maxPlantHP, maxZombieHP;
  public static void main(String[] args) throws Exception{ 
    Scanner input = new Scanner(System.in);
    System.out.println("Welcome to Cheerville Simulator!");
    System.out.println("This was made by Joseph Wang. Hi! :)");
    System.out.println("Recommended starting amounts will be provided, but if you decide to choose your own amounts, be wary. The simulation will not be as balanced.");
    System.out.println();

    int startH, startP, simSpd, decaySpd, hungerSpd, rotSpd, growSpd, hHP, pHP, zHP;
    
    //- Checks just to make sure inputs are valid (above 0, and usually not 0)
    do {
        System.out.println("How many humans would you like to start with? (Recommended: 20)");
        startH = input.nextInt();
        System.out.println("How many plants would you like to start with? (Recommended: 50)");
        startP = input.nextInt();
    } while (!((startH > 0) && (startP > 0)));
    
    do {
      System.out.println("How fast do you want this sim to run? (In ms) (Recommended: 100, 150, 250, 500, 1000)");
      simSpd = input.nextInt();
      System.out.println("How fast do you want plants to decay? (Recommended: 1)");
      decaySpd = input.nextInt();
      System.out.println("How fast do you want humans to starve? (Recommended: 1)");
      hungerSpd = input.nextInt();
      System.out.println("How fast do you want zombies to rot? (Recommended: 1)");
      rotSpd = input.nextInt();
      System.out.println("Whats the absolute minimum amount of plants to grow per turn? (Recommended: 0)");
      growSpd = input.nextInt();
    } while (!((simSpd > 0) && (decaySpd > 0) && (hungerSpd > 0) && (rotSpd > 0) && (growSpd >= 0)));
    
    do {
      System.out.println("What's a human's starting health? (Recommended: 30)");
      hHP = input.nextInt();
      System.out.println("What's a plant's staring health? (Recommended: 10)");
      pHP = input.nextInt();
      System.out.println("What's a zombie's starting health? (Recommended: 6)");
      zHP = input.nextInt();
    } while (!((hHP > 0) && (pHP > 0) && (zHP > 0)));

    do {
      System.out.println("What's a human's max health? (Recommended: 60)");
      maxHumanHP = input.nextInt();
      System.out.println("What's a plant's max health? (Recommended: 20)");
      maxPlantHP = input.nextInt();
      System.out.println("What's a zombie's max health? (Recommended: 6, 12, 20)");
      maxZombieHP = input.nextInt();
    } while (!((maxHumanHP > 0) && (maxPlantHP > 0) && (maxZombieHP > 0)));

    System.out.println("WARNING: When setting grid size, be careful. Anything bigger than the recommended will cause the graph to be larger than the screen.");
    System.out.println("Strange dimensions may cause the GUI to scale strangly or overlap with parts. Make sure you choose a large enough grid.");
    int gridH, gridW;
    
    do {
      System.out.println("How tall do you want the grid to be (Recommended: 25)");
      gridH = input.nextInt();
      System.out.println("How wide do you want the grid to be (Recommended: 25)");
      gridW = input.nextInt();
    } while (((gridH * gridW) < (startH + startP)) || !((gridH > 0) && (gridW > 0)));

    System.out.println();
    System.out.println("\nThank you. Creating your simulation...");

    nameGenerator = new NameGenerator();
    Town cheerville = new Town(startH, startP, growSpd, rotSpd, hungerSpd, decaySpd, hHP, pHP, zHP, gridH, gridW);
    System.out.println("Welcome to Cheerville!");

    MatrixDisplayWithMouse display = new MatrixDisplayWithMouse("Cheerville!", cheerville.getCity());
    
    do {
      display.updateClimate(cheerville.getWeather(), cheerville.getSeason());
      display.refresh();  
      display.updateGraph(); 
      
      try { 
         Thread.sleep(simSpd); 
      } catch (Exception e) {};   

      if (!(display.shouldStopSimulation())) {
        cheerville.runCycle(); //- Actually run the cycle
      }

      cheerville.processMouseActions(display.getMouseClickLocations()); //- Add zombies or change selected entity
      
      int[] totalCount = cheerville.getCounts();
      int[] totalGenderCount = cheerville.getGendersCounts();
      display.updateCounts(totalGenderCount[0], totalGenderCount[1], totalCount[0], totalCount[1], totalCount[2]); 
      display.updateTurns(cheerville.getTurnsSurvived());

      if (cheerville.selectedEntityExists() || display.selectedEntityExists()){
        if (display.getSelectedEntity() != cheerville.getSelectedEntity()) {
          display.updateSelectedEntity(cheerville.getSelectedEntity());
        }
      }

    } while (cheerville.checkForHumans());

    display.refresh(); //- One last refresh to make sure the screen works
    System.out.println("THE WORLD HAS ENDED! Humans have survived for " + cheerville.getTurnsSurvived() + " turns!"); 
    
    input.close();
  }
  
  /** randInt(min, max); 
   * This method accepts two ints, min and max, and gets a random integer between them.
   * @param min - the minimum integer (inclusive)
   * @param max - the maximum integer (exclusive)
   * @return a random int between min (inclusive) and max (exclusive)
   */
  public static int randInt(int min, int max) {
    int range = max - min;
    return (int) (min + (Math.random() * range));
  }
}