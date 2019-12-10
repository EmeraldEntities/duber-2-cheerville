import java.util.ArrayList;

/** Class: Human
 * @version 2.1
 * @author Joseph Wang
 * @date 11/21/2019
 * @description: A living, breathing, eating human that exists to only eat and procreate!
 */

class Human extends Animal implements AbleToEat, HasName {
  private static int REBELLION_FACTOR = 18; //- If they're rebellious, they switch last names
  private int reproductionCooldown, age;
  private char gender, canMateWith;
  private String firstName, lastName;
  private String[] parentNames = new String[2];
  private ArrayList<Human> aliveChildren, aliveParents;

  private final int MAX_HP;
  
  //// CONSTRUCTORS ////

  /** Human(health, hunger, gender, maxHp, lastName, parents);
   * This creates a new human and is used if this human was spawned through reproduction. Takes the values and calls
   * super with health and hunger, while assigning everything else to its own class variable. Also assigns
   * parents names, its first name and what gender it can mate with, and initializes the children ArrayList.
   * @param health, an int with the human's health.
   * @param hunger, an int with the human's decay value.
   * @param gender, a char with the human's gender.
   * @param maxHp, an int with the human's max possible hp.
   * @param lastName, a String with the human's last name.
   * @param parents, an ArrayList of Humans with this human's parents.
   */
  Human(int health, int hunger, char gender, int maxHp, String lastName, ArrayList<Human> parents) {
    //- If this Human was spawned through reproduction.
    super(health, hunger, 1.0, 3.0);
    
    this.gender = gender;
    
    if (this.gender == 'F') {
      this.canMateWith = 'M';
    } else {
      this.canMateWith = 'F';
    }
    
    this.age = 0;
    this.reproductionCooldown = 0;
    this.MAX_HP = maxHp;
    
    this.firstName = Simulation.nameGenerator.generateFirstName(this.gender);
    
    //- Determines whether the child will change their last name or not
    int rebelChance = Simulation.randInt(0, Human.REBELLION_FACTOR + 1);
    if (rebelChance == Human.REBELLION_FACTOR) {
      this.lastName = Simulation.nameGenerator.generateLastName();
    } else {
      this.lastName = lastName;
    }

    this.aliveParents = parents;
    this.aliveChildren = new ArrayList<Human>();

    this.parentNames[0] = parents.get(0).getFirstName() + " " + parents.get(0).getLastName();
    this.parentNames[1] = parents.get(1).getFirstName() + " " + parents.get(1).getLastName();
  }
  
  /** Human(health, hunger, gender, age, maxHp, lastName);
   * This creates a new human and is used if this human was spawned from the start. Takes the values and calls
   * super with health and hunger, while assigning everything else to its own class variable. Also assigns
   * parents names, its own name and what gender it can mate with, and initializes the children and parent
   * ArrayList.
   * @param health, an int with the human's health.
   * @param hunger, an int with the human's decay value.
   * @param gender, a char with the human's gender.
   * @param maxHp, an int with the human's max possible hp.
   * @param lastName, a String with the human's last name.
   */
  Human(int health, int hunger, char gender, int age, int maxHp, String lastName) {
    //- If the human was spawned from the spawn method
    super(health, hunger, 1.0, 3.0);
    
    this.gender = gender;
    
    if (this.gender == 'F') {
      this.canMateWith = 'M';
    } else {
      this.canMateWith = 'F';
    }
    
    this.age = age;
    this.reproductionCooldown = 0;
    this.MAX_HP = maxHp;
    this.firstName = Simulation.nameGenerator.generateFirstName(this.gender);
    this.lastName = Simulation.nameGenerator.generateLastName();

    this.aliveParents = new ArrayList<Human>(2);
    this.aliveChildren = new ArrayList<Human>();
    
    this.parentNames[0] = "The great abyss";
    this.parentNames[1] = "A cup of java";
  }
  //// CONSTRUCTORS ////
  
  /// GETTERS/SETTERS ////
  /** getReproductionCooldown(); 
   * Gets this human's reproduction cooldown.
   * @return an int, with this human's reproduction cooldown
   */
  public int getReproductionCooldown() {
    return this.reproductionCooldown;
  }
  
  /** setReproductionCooldown(newCooldown); 
   * Sets this human's reproduction cooldown.
   * @param newCooldown, an int with a new reproduction cooldown
   */
  public void setReproductionCooldown(int newCooldown) {
    this.reproductionCooldown = newCooldown;
  }
  
  /** decreaseReproductionCooldown(number);
   * Takes in a number and decreases this human's reproduction cooldown by it.
   * @param number, an int with how much this human's reproduction cooldown should decrease.
   */
  public void decreaseReproductionCooldown(int number) {
    this.reproductionCooldown -= number;
  }
  
  /** getGender(); 
   * Gets this human's gender.
   * @return a char, with this human's gender.
   */
  public char getGender() {
    return this.gender;
  }
  
  /** getCanMateWith(); 
   * Gets the gender which this human can mate with.
   * @return a char, with which gender this human can mate with.
   */
  public char getCanMateWith() {
    return this.canMateWith;
  } 
  
  /** getAge(); 
   * Gets how old this human is.
   * @return an int, with this human's age.
   */
  public int getAge() {
    return age;
  }
  
  /** incrementAge(amount); 
   * Takes in an amount, and increases this human's age by that amount.
   * @param amount, an int with how much this human's age should increase.
   */
  public void incrementAge(int amount) {
    this.age += amount;
  }

  /** getFirstName(); 
   * Gets this human's first name.
   * @return a String, with this human's first name.
   */
  public String getFirstName() {
    return this.firstName;
  }

  /** getLastName(); 
   * Gets this human's last name.
   * @return a String, with this human's last name.
   */
  public String getLastName() {
    return this.lastName;
  }

  @Override
  /** getName(); 
   * Gets this human's full name.
   * @return a String, with this human's name.
   */
  public String getName() {
    return this.firstName + " " + this.lastName;
  }

  /** hasAliveParents(); 
   * Checks to see if this human has any alive parents.
   * @return true if this human does, false otherwise.
   */
  public boolean hasAliveParents() {
    if (this.aliveParents.size() < 0) {
      return true;
    }
    return false;
  }

  /** getParents(); 
   * Gets this human's parents.
   * @return an ArrayList of Humans, with this human's parents.
   */
  public ArrayList<Human> getParents() {
    return this.aliveParents;
  }

  /** getParentNames(); 
   * Gets this human's parents' names.
   * @return an String array, with this human's parents' names.
   */
  public String[] getParentNames() {
    return this.parentNames;
  }

  /** removeParents(deadParent); 
   * Deletes the provided parent from the ArrayList of parents.
   * @param deadParent, a Human that is the human of this entity.
   */
  public void removeParent(Human deadParent) {
    this.aliveParents.remove(deadParent);
  }

  /** hasAliveKids(); 
   * Checks to see if this human has any alive children.
   * @return true if this human does, otherwise false.
   */
  public boolean hasAliveKids() {
    if (this.aliveChildren.size() > 0) {
      return true;
    }

    return false;
  }

  /** getKids(); 
   * Gets an ArrayList containing this human's children.
   * @return an ArrayList of Humans, which contains this human's children.
   */
  public ArrayList<Human> getKids() {
    return this.aliveChildren;
  }

  /** addKid(child); 
   * Adds a child to this human's children ArrayList.
   * @param child, a Human that is the child of this human.
   */
  public void addKid(Human child) {
    this.aliveChildren.add(child);
  }

  /** removeKid(deadChild); 
   * Removes a child from this human's children ArrayList.
   * @param deadChild, a Human that is to be removed.
   */
  public void removeKid(Human deadChild) {
    this.aliveChildren.remove(deadChild);
  }

  /** getAmountOfAliveKids(); 
   * Get the total amount of alive children that this human has.
   * @return an int, with the amount of alive children this human has.
   */
  public int getAmountOfAliveKids() {
    return this.aliveChildren.size();
  }
  
  /// GETTERS/SETTERS ////

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
        if (surroundingEntities[x] instanceof Human) {
          Human mate = (Human)surroundingEntities[x];
          if (this.getReproductionCooldown() == 0 && mate.getReproductionCooldown() == 0) {
            if (this.getAge() >= Town.MIN_REPRODUCTION_AGE && mate.getAge() >= Town.MIN_REPRODUCTION_AGE) {
              if (this.getCanMateWith() == mate.getGender()) {
                positions.add(x);
              }
            }
          }
        } else if (surroundingEntities[x] instanceof Plant) {
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
    if (possibleMoves.size() > 0) {
      double[] values = revalidateValues(possibleMoves, surroundingValues);

      double max = values[0];
      //- Get the max priority
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

  /** reproduce(mate); 
   * Takes in a human mate and creates a new child with that mate. Also adjusts values of the mate and 
   * current human, as well as the child.
   * @param mate, a Human that this human is procreating with.
   * @return a Human, which is the child of this human and this human's mate.
   */
  public Human reproduce(Human mate) {
    mate.setShouldPerformAction(false);
    mate.setReproductionCooldown(Town.PROCREATING_COOLDOWN);
    this.setReproductionCooldown(Town.PROCREATING_COOLDOWN);
    
    String lastName;

    if (this.gender == 'M') {
      lastName = this.lastName;
    } else {
      lastName = mate.getLastName();
    }

    char[] genders = {'M', 'F'};
    int gender = Simulation.randInt(0, genders.length);
    
    ArrayList<Human> parents = new ArrayList<Human>();
    parents.add(this);
    parents.add(mate);

    Human child = new Human(((int)((this.getHealth() + mate.getHealth()) / 2)), this.getDecayValue(), 
                              genders[gender], this.MAX_HP, lastName, parents);
 
    child.setShouldPerformAction(false);

    this.addKid(child);
    mate.addKid(child);

    return child;
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
  }
}