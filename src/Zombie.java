import java.util.ArrayList;

/** Class: Zombie
 * @version 2.1
 * @author Joseph Wang
 * @date 11/21/2019
 * @description: An undead zombie that exists only to destroy humans.
 */

class Zombie extends Animal implements AbleToEat, HasName {
  private final int MAX_HP;

  private String name, createdBy;
  private int kills;

  //// CONSTRUCTORS ////
  /** Zombie(health, hunger, maxHp, createdBy); 
   * Creates a new zombie with the specified values. Calls super with health and hunger, and assigns maxHp and 
   * createdBy to its own class variables for future use.
   * @param health, an int with this zombie's health.
   * @param hunger, an int with this zombie's decay rate.
   * @param maxHp, an int with this zombie's max health.
   * @param createdBy, a String with how this zombie was created (infected or spawned)
   */
  Zombie(int health, int hunger, int maxHp, String createdBy) {
    super(health, hunger, -5, 0);
    
    this.MAX_HP = maxHp;
    this.name = Simulation.nameGenerator.generateZombieName();
    this.createdBy = createdBy;
  }
  //// CONSTRUCTORS ////

  /// GETTERS/SETTERS ////
  @Override
  /** getName();
   * Gets this zombie's name.
   * @return a String, containing this zombie's name.
   */
  public String getName() {
    return this.name;
  }

  /** getKills();
   * Gets the amount of kills/infections that this zombie has performed.
   * @return an int, with the amount of kills/infections that this zombie has.
   */
  public int getKills() {
    return this.kills;
  }

  /** getCreatedBy(); 
   * Gets how the zombie was created (ie. whether it was spawned by mouse or infected)
   * @return a String, with whether the zombie was infected or spawned.
   */
  public String getCreatedBy() {
    return createdBy;
  }
  /// GETTERS/SETTERS ///
  
  @Override
  /** findPossibleMoves(surroundingEntities); 
   * Takes in a list of the surrounding 8 tiles, and decides whether it can move there or not based on
   * location and if there is something there already.
   * @return an ArrayList of Integers, containing the possible direction in which it can move.
   */
  public ArrayList<Integer> findPossibleMoves(Entity[] surroundingEntities) {
    ArrayList<Integer> positions = new ArrayList<Integer>();
    int[] pos = this.getLocation();

    for (int x = 0; x < surroundingEntities.length; x++) {
      if (surroundingEntities[x] == null) { 
        int[] direction = {pos[0] + directions[x][0], pos[1] + directions[x][1]};

        if (((direction[0] >= 0) && (direction[0] < Town.GRID_H)) && ((direction[1] >= 0) && (direction[1] < Town.GRID_W))) {
          positions.add(x);
        }
      } else { 
        if (!(surroundingEntities[x] instanceof Zombie)){
          positions.add(x);
        }
      }
    }

    return positions;
  }

  /** revalidateValues(possibleMoves, currentValues); 
   * Takes in possible locations and the values of all 8 surrounding locations, and returns only the values
   * of the squares that can be moved to.
   * @param possibleMoves, an ArrayList of Integers that contains the possible moves out of all 8 directions.
   * @param currentValues, a double array that contains all 8 surrounding values.
   * @return a double array, containing all the values of the squares that can be moved to.
   */
  private double[] revalidateValues(ArrayList<Integer> possibleMoves, double[] currentValues) {
    double[] validValues = new double[possibleMoves.size()];

    for (int x = 0; x < possibleMoves.size(); x++) {
      validValues[x] = currentValues[possibleMoves.get(x)];
    }

    return validValues;
  }

  @Override
  /** decideMovement(surroundingEntities, surroundingValues); 
   * Takes in the surrounding 8 entities and the surrounding 8 values and decides where to go
   * based on where it can actually go and the values of that spot.
   * @param surroundingEntities, an Entity array containing the 8 surrounding spots.
   * @param surroundingValues, a double array containing the 8 surrounding values.
   * @return an int array contianing the new positiong that this being wishes to go to.
   */
  public int[] decideMovement(Entity[] surroundingEntities, double[] surroundingValues) {
    ArrayList<Integer> possibleMoves = findPossibleMoves(surroundingEntities);
    if(possibleMoves.size() > 0) {
      double[] values = revalidateValues(possibleMoves, surroundingValues);

      double max = values[0];

      for (int x = 0; x < values.length; x++) {
        if (values[x] > max) {
          max = values[x];
        }
      }
        
      ArrayList<Integer> betterLocations = new ArrayList<Integer>();

      for (int x = 0; x < values.length; x++) {
        if (values[x] == max) {
        betterLocations.add(possibleMoves.get(x));
        }
      }

      int choice = Simulation.randInt(0, betterLocations.size());
      int[] pos = this.getLocation();
      int[] newPos = {pos[0] + directions[betterLocations.get(choice)][0], 
                      pos[1] + directions[betterLocations.get(choice)][1]};
          
      return newPos;
    }

    return this.getLocation();
  }

  /** trample(e); 
   * Takes in an entity, and destroys that entity (sets its health to 0).
   * 
   * @param e, an Entity that is to be trampled
   */
  public void trample(Entity e) {
    e.setHealth(0);
  }
  
  /** infect(victim);
   * Takes in a victim, and produces a new zombie using some of the victim's stats like their health. Also destroys
   * the vicim.
   * @param victim, a Human that is to be infected.
   * @return a Zombie, which is the infected human.
   */
  public Zombie infect(Human victim) {
    int startHealth;
    if (victim.getHealth() > this.MAX_HP) {
      startHealth = this.MAX_HP;
    } else {
      startHealth = victim.getHealth();
    }
    
    Zombie newZombie = new Zombie(startHealth, this.getDecayValue(), this.MAX_HP, "INFECTED");
    newZombie.setShouldPerformAction(false);
    
    victim.setHealth(0);
    this.kills++;

    return newZombie;
  }
  
  /** shouldEat(prey); 
   * Takes in the prey and determines whether the zombie should eat it or infect it based on the prey's 
   * health.
   * @param prey, a Human that is to be eaten.
   * @return true if this zombie has more health, false otherwise.
   */
  public boolean shouldEat(Human prey) {
    if (prey.getHealth() < this.getHealth()) {
      return true;
    }
    
    return false;
  }
  
  @Override
  /** consume(e); 
   * Takes in an entity, and consumes that entity, adding its health to this human's health and
   * destroying that entity.
   * @param e, an Entity that is being consumed.
   */
  public void consume(Entity e) {
    this.setHealth(this.getHealth() + e.getHealth());
    if (this.getHealth() > MAX_HP) {
      this.setHealth(MAX_HP);
    }
    
    e.setHealth(0);
    this.kills++;
  }
}