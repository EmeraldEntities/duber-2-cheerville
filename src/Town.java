import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.Queue;
import java.util.LinkedList;

/** Class: Town
 * @version 4.1
 * @author Joseph Wang
 * @date 11/21/2019
 * @description: The class that controls everything that has to do with the town!
 */

class Town {
  public static int[][] directions = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
  
  //- I use both a 2d Entity array and an ArrayList of entities to keep track of everything
  private Entity[][] city;
  private ArrayList<Entity> allThings;

  //- Each entity has their own array with two options: Human and Zombie. As the entity moves and whatnot,
  //- their respective array gets updated with the new movement or action (eg. If a human moves, valuesOfHumans
  //- would change based on the new position, and the human value would be stored in the Human section of
  //- valuesOfHumans, while the zombie value would be stored in the Zombie section of valuesOfHumans).
  private double[][][] valuesOfPlants, valuesOfHumans, valuesOfZombies;
  private int[][][] valueInfluencers;
  
  //- Makes array indexing more readable and easier to understand.
  private final int HUMAN = 0;
  private final int ZOMBIE = 1;
  private final int PLANT = 2;

  private final int MALE = 0;
  private final int FEMALE = 1;
  
  public static final int MIN_REPRODUCTION_AGE = 16;
  public static final int PROCREATING_COOLDOWN = 8;

  public final int BASE_GROW_SPEED;

  public static int GRID_H, GRID_W;
  
  private int turnCount;

  //- Various counts for every entity or gender in the town
  private int[] count = new int[3];
  private int[] genderCount = new int[2];

  //- Starting values
  private int[] startingAmounts = new int[3];
  private int[] startingHP = new int[3];

  private int[] maxHP = new int[3];
  
  //- Controls the speed of growing / decaying
  private int decaySpeed, hungerSpeed, rotSpeed, growSpeed;

  Climate climate;

  //- Used for the display
  private Entity selectedEntity;
  
  //// CONSTRUCTORS ////
  /** Town(startH, startP, gSpd, rSpd, hSpd, dSpd, hHP, pHP, zHP, gridH, gridW);
   * This creates an entirely new town, with all the values specified. All these values are able to be
   * changed depending on how you want to change them in the start screen, and are all given a class variable.
   * The constructor then resets the board.
   * @param startH, an int with the starting amounts of humans.
   * @param startP, an int with the starting amounts of plants.
   * @param gSpd, an int with the default speed of growth in the town.
   * @param rSpd, an int that controls the default speed of rot for plants.
   * @param hSpd, an int that controls the default speed of hunger for humans.
   * @param dSpd, an int that controls the default speed of decay for zombies.
   * @param hHP, an int with the base starting health of all humans.
   * @param pHP, an int with the base starting health of all plants.
   * @param zHP, an int with the base starting health of all zombies.
   * @param gridH, an int with the height of the grid.
   * @param gridW, an int with the width of the grid.
   */
  Town(int startH, int startP, int gSpd, int rSpd, int hSpd, int dSpd, int hHP, int pHP, int zHP, int gridH, int gridW) {
    this.startingAmounts[HUMAN] = startH;
    this.startingAmounts[PLANT] = startP;
    this.startingAmounts[ZOMBIE] = 0;
    
    this.startingHP[HUMAN] = hHP;
    this.startingHP[PLANT] = pHP;
    this.startingHP[ZOMBIE] = zHP;

    this.maxHP[HUMAN] = Simulation.maxHumanHP;
    this.maxHP[PLANT] = Simulation.maxPlantHP;
    this.maxHP[ZOMBIE] = Simulation.maxZombieHP;

    this.decaySpeed = dSpd;
    this.hungerSpeed = hSpd;
    this.rotSpeed = rSpd;
    
    this.BASE_GROW_SPEED = gSpd;
    this.growSpeed = this.BASE_GROW_SPEED;

    this.GRID_H = gridH;
    this.GRID_W = gridW;

    this.climate = new Climate(20, 50);

    reset();
  }
  
  //// CONSTRUCTORS ////
  
  /// GETTERS/SETTERS ////
  /** getCity(); 
   * Gets the current city.
   * @return a 2d Entity array containing the current city.
   */
  public Entity[][] getCity() {
    return this.city;
  }

  /** getTurnsSurvived(); 
   * Gets the total amount of turns survived.
   * @return an int with the total amount of turns survived.
   */
  public int getTurnsSurvived() {
    return this.turnCount;
  }

  /** getCounts(); 
   * Gets the total amount of every entity.
   * @return an int array with a total count for every entity.
   */
  public int[] getCounts() {
    return this.count;
  }

  /** getGenderCounts(); 
   * Gets the total amount of both genders.
   * @return an int array with a total count for every gender.
   */
  public int[] getGendersCounts() {
    return this.genderCount;
  }

  /** getWeather(); 
   * Gets this climate's current weather.
   * @return a String with the weather.
   */
  public String getWeather() {
    return this.climate.getWeather();
  }

  /** getSeason(); 
   * Gets this climate's current season.
   * @return a String with the season.
   */
  public String getSeason() {
    return this.climate.getSeason();
  }

  /** getSelectedEntity(); 
   * Gets the current selected entity.
   * @return an Entity that is the current selected one.
   */
  public Entity getSelectedEntity() {
    return this.selectedEntity;
  }
  
  /// GETTERS/SETTERS ////
  
  ////////////////
  
  /** reset(); 
   * Initializes the map, counts, and the list holding all things. It then spawns all the entities based on
   * what was specified in startingAmounts. It then adds all the entities to the map and to the list holding
   * all things, before calling initiateValues();. 
   */
  public void reset() {
    turnCount = 0;
    
    count[HUMAN] = 0;
    count[PLANT] = 0;
    count[ZOMBIE] = 0;
    
    city = new Entity[GRID_H][GRID_W];
    allThings = new ArrayList<Entity>();
    genderCount = new int[2];
    
    //- Start making every single entity
    for (int i = 0; i < startingAmounts.length; i++) {   
      for (int j = 0; j < startingAmounts[i]; j++) {
        int[] start = new int[2];
        
        do {
          start[0] = Simulation.randInt(0, city.length);
          start[1] = Simulation.randInt(0, city[0].length);
          
        } while (city[start[0]][start[1]] != null);
        
        //- Spawn the entity based on what it is
        switch (i) {
          case HUMAN:
            char gender;
            if (genderCount[MALE] <= genderCount[FEMALE]) {
              gender = 'M';
              genderCount[MALE]++;
            } else {
              gender = 'F';
              genderCount[FEMALE]++;
            }

            String lastName = Simulation.nameGenerator.generateLastName();
            city[start[0]][start[1]] = new Human(this.startingHP[HUMAN] + generateHPVariation(), this.hungerSpeed, gender, 18, maxHP[HUMAN], lastName);
            count[HUMAN] += 1;
            break;
            
          case PLANT:
            city[start[0]][start[1]] = new Plant(this.startingHP[PLANT] + generateHPVariation(), this.rotSpeed);
            count[PLANT] += 1;
            break;

          case ZOMBIE:
            city[start[0]][start[1]] = new Zombie(this.startingHP[ZOMBIE] + generateHPVariation(), this.decaySpeed, maxHP[ZOMBIE], "SPAWNED");
            count[ZOMBIE] += 1;
            break;
        }
        
        city[start[0]][start[1]].setLocation(start);
        allThings.add(city[start[0]][start[1]]);
      }
    }

    //- Initialize the values
    initiateValues();
  }
  
  /** initiateValues(); 
   * This creates the new value maps as explained, creating valuesOfPlants (storing changes to value of plants),
   * valuesOfHumans (storing changes to value of humans) and valuesOfZombies (storing changes to value of zombies).
   * It then gets all the entities and calculates their values, then stores them in the appropriate map.
   */
  private void initiateValues() {
    valuesOfPlants = new double[GRID_H][GRID_W][2];
    valuesOfHumans = new double[GRID_H][GRID_W][2];
    valuesOfZombies = new double[GRID_H][GRID_W][2];
    valueInfluencers = new int[GRID_H][GRID_W][2];

    //- Start calculating values for all the entities
    for (int x = 0; x < allThings.size(); x++) {
      int[] location = allThings.get(x).getLocation();

      if (allThings.get(x) instanceof Human) {
        assignValues(allThings.get(x).getHValue(), location, HUMAN, valuesOfHumans);
        assignValues(allThings.get(x).getZValue(), location, ZOMBIE, valuesOfHumans);

      } else if (allThings.get(x) instanceof Plant) {
        assignValues(allThings.get(x).getHValue(), location, HUMAN, valuesOfPlants);

        //- Plants don't necessarily matter to zombies
        valuesOfPlants[location[0]][location[1]][ZOMBIE] = allThings.get(x).getZValue();

      } else if (allThings.get(x) instanceof Zombie) {
        assignValues(allThings.get(x).getHValue(), location, HUMAN, valuesOfZombies);

        //- Other zombies don't necessarily matter to zombies
        valuesOfZombies[location[0]][location[1]][ZOMBIE] = allThings.get(x).getZValue();
      }   
    }
  }
  
  /** assignValues(value, location, valueType, valueMap); 
   * Performs a BFS, starting on the location, and decreasing (or increasing if the starting value is negative) the
   * value until the value hits 0, adding the values to the tile.
   * 
   * The BFS pops from both the list of positions and the list of current values. It changes the value, adds a 
   * modifier, adds the current square to "visited", and if the 8 squares around it are not visited and the current 
   * value plus modifier does not equal 0, adds the next square's position to the list of positions and the current 
   * value plus modifier to the list of modifiers.
   * 
   * @param value, an int which has the starting value.
   * @param location, an int array which contains the starting position of the BFS.
   * @param valueType, either HUMAN or ZOMBIE, depending on which value we're using (HValue or ZValue).
   * @param valueMap, the selected map of values (either valuesOfHumans, valuesOfZombies, or valuesOfPlants).
   */
  private void assignValues(double value, int[] location, int valueType, double[][][] valueMap) {
    Queue<int[]> positions = new LinkedList<>();
    Queue<Double> values = new LinkedList<>();
    ArrayList<int[]> visited = new ArrayList<int[]>();

    //- Add the starting values
    positions.add(location);
    values.add(value);
    visited.add(location);

    //- Repeat the BFS while we still have positions to visit
    while (positions.size() > 0) {
      int[] position = positions.remove();
      double currentValue = values.remove();

      valueMap[position[0]][position[1]][valueType] += currentValue;
      valueInfluencers[position[0]][position[1]][valueType]++;

      //- Change the modifier based on the current value
      int modifier;
      if (currentValue > 0) {
        modifier = -1;
      } else if (currentValue < 0){
        modifier = 1;
      } else {
        modifier = 0;
      }
        
      if (!(currentValue + modifier == 0)) {
        for (int x = 0; x < directions.length; x++) { //- We want to change in all 8 directions
          if ((position[0] + directions[x][0] >= 0) && (position[0] + directions[x][0] < city.length) && 
              (position[1] + directions[x][1] >= 0) && (position[1] + directions[x][1] < city[0].length)) {
            int[] newPos = {position[0] + directions[x][0], position[1] + directions[x][1]};  
            if (!(checkIfIn(visited, newPos))) { //- To make sure we haven't visited this position yet
              positions.add(newPos);
              values.add(currentValue + modifier);
              visited.add(newPos);
            }
          }
        }
      }
    }
  }

  /** resetValues(oldValue, startLocation, valueType, valueMap); 
   * Performs a BFS, starting on the location, and decreasing (or increasing if the starting value is negative) the
   * value until the value hits 0, removing the value from the tile. The opposite of assignValues().
   * 
   * The BFS pops from both the list of positions and the list of current values. It removes the value, adds a 
   * modifier, adds the current square to "visited", and if the 8 squares around it are not visited and the current 
   * value plus modifier does not equal 0, adds the next square's position to the list of positions and the current 
   * value plus modifier to the list of modifiers.
   * 
   * @param oldValue, an int which has the starting value.
   * @param startLocation, an int array which contains the starting position of the BFS.
   * @param valueType, either HUMAN or ZOMBIE, depending on which value we're using (HValue or ZValue).
   * @param valueMap, the selected map of values (either valuesOfHumans, valuesOfZombies, or valuesOfPlants).
   */
  private void resetValues(double oldValue, int[] startLocation, int valueType, double[][][] valueMap) {
    Queue<int[]> positions = new LinkedList<>();
    Queue<Double> values = new LinkedList<>();
    ArrayList<int[]> visited = new ArrayList<int[]>();

    //- Adding the start location and values
    positions.add(startLocation);
    values.add(oldValue);
    visited.add(startLocation);

    while (positions.size() > 0) {
      int[] position = positions.remove();
      double currentValue = values.remove();

      //- Remove the value
      valueMap[position[0]][position[1]][valueType] -= currentValue;
      valueInfluencers[position[0]][position[1]][valueType]--;

      int modifier;
      if (currentValue > 0) {
        modifier = -1;
      } else if (currentValue < 0){
        modifier = 1;
      } else {
        modifier = 0;
      }

      if (!(currentValue + modifier == 0)) {
        for (int x = 0; x < directions.length; x++) {
          if ((position[0] + directions[x][0] >= 0) && (position[0] + directions[x][0] < city.length) && 
              (position[1] + directions[x][1] >= 0) && (position[1] + directions[x][1] < city[0].length)) {
            int[] newPos = {position[0] + directions[x][0], position[1] + directions[x][1]};  
            if (!(checkIfIn(visited, newPos))) { //- Make sure we haven't visited the new position yet
              positions.add(newPos);
              values.add(currentValue + modifier);
              visited.add(newPos);
            }
          }
        }
      }
    }
  }

  /** checkIfIn(list, object); 
   * Checks and sees if the object specified is in the list.
   * @param list, an ArrayList of int arrays that we want to check against.
   * @param object, an int array that we want to check with.
   * @return true if it is in the list, false otherwise.
   */
  private boolean checkIfIn(ArrayList<int[]> list, int[] object) {
    for (int x = 0; x < list.size(); x++) {
      int[] check = list.get(x);
      if ((check[0] == object[0]) && (check[1] == object[1])) {
        return true;
      }
    }

    return false;
  }

  ////////////////

  /** runCycle(); 
   * The main method that does everything that needs to be done in the town. Loops through every entity and decays it,
   * then performs an action based on whether it's dead or not. If it is, destroy it, otherwise process it.
   * 
   * It then loops through them again, getting rid of any dead entities that were later edited, and allowing everyone
   * to move and perform actions again. It also increments people's age and decrements reproduction cooldowns.
   * 
   * Finally, it changes the grow speed according to climate, and adds more plants.
   */
  public void runCycle() {
    //- We'll be using this ArrayList to keep track of entities that are still alive.
    ArrayList<Entity> stillExistingEntities = new ArrayList<Entity>();
    
    for (int x = allThings.size() - 1; x >= 0; x--) {
      if (allThings.get(x).getShouldPerformAction()) {
        decayEntity(x);
        
        if (!(allThings.get(x).isDead())) {
          processEntity(stillExistingEntities, x);
        } else {
          destroy(x);
        }
        
      } else {
        stillExistingEntities.add(allThings.get(x)); //- If they can't do anything, they're still alive so they get added
      }
    }
    allThings = stillExistingEntities; //- Assign all things to the entities that still exist after that loop
    
    //- If we miss anything, this loop will catch it. It also does post-loop processing, like incrementing age or decreasing
    //- reproductive cooldowns.
    for (int x = allThings.size() - 1; x >= 0; x--) { 
      if ((allThings.get(x).isDead())) {
        destroy(x);
      } else {
        allThings.get(x).setShouldPerformAction(true);
        
        if ((allThings.get(x) instanceof Human)) {
          Human person = (Human) allThings.get(x);
          if (person.getReproductionCooldown() != 0) {
            person.decreaseReproductionCooldown(1);
          }
          
          person.incrementAge(1);
        }
      } 
    }

    //- Change the grow speed and add plants
    this.growSpeed = climate.changeGrowSpeed(this.BASE_GROW_SPEED);
    addPlants(this.growSpeed, allThings);
    turnCount++;
  }
 
  /** moveHuman(pos, newPos, person); 
   * Moves the selected human both on the visible map, but also on the value maps that it's associated with.
   * @param pos, an int array with the old position of the human.
   * @param newPos, an int array with the new position of the human.
   * @param person, a Human which is the person we want to move.
   */
  private void moveHuman(int[] pos, int[] newPos, Human person) {
    person.move(newPos);
    city[pos[0]][pos[1]] = null;
    city[newPos[0]][newPos[1]] = person;

    resetValues(person.getHValue(), pos, HUMAN, valuesOfHumans);
    assignValues(person.getHValue(), newPos, HUMAN, valuesOfHumans);
    resetValues(person.getZValue(), pos, ZOMBIE, valuesOfHumans);         
    assignValues(person.getZValue(), newPos, ZOMBIE, valuesOfHumans);
  }

  /** moveZombie(pos, newPos, zombie); 
   * Moves the selected zombie both on the visible map, but also on the value maps that it's associated with.
   * @param pos, an int array with the old position of the zombie.
   * @param newPos, an int array with the new position of the zombie.
   * @param zombie, a zombie which is the person we want to move.
   */
  private void moveZombie(int[] pos, int[] newPos, Zombie zombie) {
    zombie.move(newPos);
    city[pos[0]][pos[1]] = null;
    city[newPos[0]][newPos[1]] = zombie;

    resetValues(zombie.getHValue(), pos, HUMAN, valuesOfZombies);
    assignValues(zombie.getHValue(), newPos, HUMAN, valuesOfZombies);
  }

  /** processEntity(stillExistingEntities, entityID); 
   * This method performs the action of whatever the selected entity needs to do. If it can move, it checks where it wants to move,
   * then performs actions based on what it's moving into.
   * null - Move there.
   * Plant - consume or trample it, depending on what the entity is.
   * Human - Either try to reproduce with it, or eat/infect it.
   * It should never collide with a zombie.
   * @param stillExistingEntities, an ArrayList of Entities that will be constantly added to. Anything alive, new or old, is added to this.
   * @param entityID, an int with its location in the ArrayList of all things.
   */
  private void processEntity(ArrayList<Entity> stillExistingEntities, int entityID) {
    Entity e = allThings.get(entityID);
    
    if (!(e instanceof Plant)) {
      Animal entity = (Animal) e;
      int[] pos = entity.getLocation().clone();
      
      //- Get the movable positions
      Entity[] surroundingSquares = findSurroundingSquares(entity);
      double[] surroundingValues = findSurroundingValues(entity);
      int[] newPos = entity.decideMovement(surroundingSquares, surroundingValues);

      //- If thee direction isn't their own position
      if (!(newPos[0] == pos[0] && newPos[1] == pos[1])) { 
        //- If it's an empty tile
        if (city[newPos[0]][newPos[1]] == null) {
          if (entity instanceof Human) {
            moveHuman(pos, newPos, (Human)entity);

          } else { //- The entity must be a zombie
            moveZombie(pos, newPos, (Zombie)entity);
          }
          
        } else {
          Entity otherE = city[newPos[0]][newPos[1]]; 
          
          //- If it collides with a plant
          if (otherE instanceof Plant) {
            if (entity instanceof Human) {  
              ((Human)entity).consume(otherE); //- Consume
              moveHuman(pos, newPos, (Human)entity);

            } else { //- The entity must be a zombie
              ((Zombie)entity).trample(otherE); //- Destroy

              moveZombie(pos, newPos, (Zombie)entity);
            }
            //- Reset the dead plant's values
            resetValues(otherE.getHValue(), newPos, HUMAN, valuesOfPlants);
            
          } else { //- If it collides with an animal (in this case it has to be a human)
            Animal otherEntity = (Animal) otherE;
            Human otherHuman = (Human) otherEntity;
            Entity newEntity;
            
            if (entity instanceof Human) { //- If this is a Human
              Human currentHuman = (Human) entity;

              //- Begin reproduction
              //- Temporarily give it a cooldown
              currentHuman.setReproductionCooldown(PROCREATING_COOLDOWN);
              int[] safeLocation = currentHuman.decideMovement(surroundingSquares, surroundingValues);

              if (!(safeLocation[0] == pos[0] && safeLocation[1] == pos[1])) { //- Make sure the human can escape to somewhere
                newEntity = currentHuman.reproduce(otherHuman); //- A new baby was formed

                if (((Human)newEntity).getGender() == 'M') {
                  genderCount[MALE] ++;
                } else {
                  genderCount[FEMALE] ++;
                }
                count[HUMAN] += 1;

                if (city[safeLocation[0]][safeLocation[1]] != null) { //- If there was a plant there, reset its values and kill it
                  city[safeLocation[0]][safeLocation[1]].setHealth(0);
                  resetValues(city[safeLocation[0]][safeLocation[1]].getHValue(), safeLocation, HUMAN, valuesOfPlants);
                }

                //- Move the current human and the baby human
                currentHuman.move(safeLocation);
                city[pos[0]][pos[1]] = null;
                city[safeLocation[0]][safeLocation[1]] = currentHuman;

                newEntity.setLocation(pos);
                city[pos[0]][pos[1]] = newEntity;
                stillExistingEntities.add(newEntity);
                
                //- There's no point in removing these values, since another human will occupy it anyways
                assignValues(entity.getHValue(), safeLocation, HUMAN, valuesOfHumans);
                assignValues(entity.getZValue(), safeLocation, ZOMBIE, valuesOfHumans);
              } else {
                //- Reset its cooldown
                currentHuman.setReproductionCooldown(0);
              }
            } else { //- Otherwise if this is a Zombie colliding with a Human
              Zombie currentZombie = (Zombie)entity;
              //- Decide whether to eat or not
              if (currentZombie.shouldEat(otherHuman)) { //- Eating the human
                currentZombie.consume(otherHuman);
                
                city[newPos[0]][newPos[1]] = null;
                entity.move(newPos);
                city[pos[0]][pos[1]] = null;
                city[newPos[0]][newPos[1]] = entity;

                resetValues(entity.getHValue(), pos, HUMAN, valuesOfZombies); //- Reset the old position's values
              } else { //- Infecting the human
                newEntity = currentZombie.infect(otherHuman);

                count[ZOMBIE] += 1;
                
                newEntity.setLocation(newPos);
                city[newPos[0]][newPos[1]] = newEntity;
                stillExistingEntities.add(newEntity);
                //- We don't need to reset the old position's values here, as it infected someone so it stays the same
              }
              assignValues(entity.getHValue(), newPos, HUMAN, valuesOfZombies);

              //- No matter the option, the human's values must always be reset
              resetValues(otherE.getHValue(), newPos, HUMAN, valuesOfHumans);
              resetValues(otherE.getZValue(), newPos, ZOMBIE, valuesOfHumans);
            }
          }
        }
      }
    }
    
    stillExistingEntities.add(e); //Add the current entity to the list of still existing ones
  }

  ////////////////

  /** findSurroundingSquares(entity); 
   * Gets the entity and gets all the squares around them. This includes entities and null values.
   * @param entity, An entity that we wish to get all the squares around of.
   * @return an Entity array containing the entities around them. The index is the direction value.
   */
  private Entity[] findSurroundingSquares(Animal entity) {
    Entity[] surroundingEntities = new Entity[directions.length];
    int[] pos = entity.getLocation();

    for (int x = 0; x < directions.length; x++) {
      int[] direction = {pos[0] + directions[x][0], pos[1] + directions[x][1]};
      
      if (((direction[0] >= 0) && (direction[0] < city.length)) && ((direction[1] >= 0) && (direction[1] < city[0].length))) {
        if (city[direction[0]][direction[1]] != null) {
          surroundingEntities[x] = (city[direction[0]][direction[1]]);
        }
      }
    }

    return surroundingEntities;
  }
  
  /** findSurroundingValues(entity); 
   * Get an entity, and get all the surrounding values in all directions around them. Anything without a value is given a 0.00.
   * @param entity, an Entity that we wish to get all the values around of.
   * @return a double array containing all the values around them. The index is the direction value.
   */
  private double[] findSurroundingValues(Animal entity) {
    DecimalFormat limit = new DecimalFormat("#.##"); //- Formatting so that everything has at most 2 decimal points
    double humanValue, zombieValue, plantValue, influences, value;
    double[] surroundingValues = new double[directions.length];
    int[] pos = entity.getLocation();

    for (int x = 0; x < directions.length; x++) {
      int[] newPos = {pos[0] + directions[x][0], pos[1] + directions[x][1]};
      if (((newPos[0] >= 0) && (newPos[0] < city.length)) && ((newPos[1] >= 0) && (newPos[1] < city[0].length))) {
        if (entity instanceof Human) {
          if (valueInfluencers[newPos[0]][newPos[1]][HUMAN] > 0) { //- This is to avoid dividing by 0
            //- Retrieve the values from each map
            humanValue = valuesOfHumans[newPos[0]][newPos[1]][HUMAN];
            zombieValue = valuesOfZombies[newPos[0]][newPos[1]][HUMAN];
            plantValue = valuesOfPlants[newPos[0]][newPos[1]][HUMAN];
            influences = valueInfluencers[newPos[0]][newPos[1]][HUMAN];

            if (entity.getHealth() < 30) {
              if (valuesOfPlants[newPos[0]][newPos[1]][HUMAN] != 0) {
                plantValue += 3;
              }
            } 

            //- Value is calculated by taking the mean of every square
            value = Double.parseDouble(limit.format((humanValue + plantValue + zombieValue) / influences));
            surroundingValues[x] = value;
          } else {
            surroundingValues[x] = 0.00; //- If there's no value, set it to 0.00 to ensure every square has a value
          }
        } else if (entity instanceof Zombie) {        
          if (valueInfluencers[newPos[0]][newPos[1]][ZOMBIE] > 0) { //- This is to avoid dividing by 0
            //- Retrieve the values from each map
            humanValue = valuesOfHumans[newPos[0]][newPos[1]][ZOMBIE];
            zombieValue = valuesOfZombies[newPos[0]][newPos[1]][ZOMBIE];
            plantValue = valuesOfPlants[newPos[0]][newPos[1]][ZOMBIE];
            influences = valueInfluencers[newPos[0]][newPos[1]][ZOMBIE];
            
            //- Value is calculated by taking the mean of every square
            value = Double.parseDouble(limit.format((humanValue + plantValue + zombieValue) / influences));
            surroundingValues[x] = value;
          } else {
            surroundingValues[x] = 0.00; //- If there's no value, set it to 0.00 to ensure every square has a value
          }
        }
      }
    }

    return surroundingValues;
  }
  
  /** addPlants(number, allEntities); 
   * Takes in a number n and adds n amount of plants to the allEntities ArrayList. Amount is usually the grow speed. 
   * @param number, an int with the amount of plants to be added.
   * @param allEntities, an ArrayList of Entities that contains all the entities in the town.
   */
  private void addPlants(int number, ArrayList<Entity> allEntities) {
    for (int x = 0; x < number; x++) {
      if (allEntities.size() < GRID_H * GRID_W) {   
        int[] start = new int[2];
        
        do {
          start[0] = Simulation.randInt(0, city.length);
          start[1] = Simulation.randInt(0, city[0].length);
          
        } while (city[start[0]][start[1]] != null); //- Spawn a plant in an empty tile
        
        city[start[0]][start[1]] = new Plant(this.startingHP[PLANT] + generateHPVariation(), this.rotSpeed);
        count[PLANT] += 1;
        
        city[start[0]][start[1]].setLocation(start);
        allEntities.add(city[start[0]][start[1]]);

        assignValues(city[start[0]][start[1]].getHValue(), start, HUMAN, valuesOfPlants); //- Assign the values in the proper value map
      }
    }
  } 

  /** addZombie(newPos); 
   * Adds a new zombie at the pos newPos on the map. This is used with the mouse clicks in the GUI.
   * @param newPos, the position for the new zombie
   */
  public void addZombie(int[] newPos) {
    Entity newEntity = new Zombie(this.startingHP[ZOMBIE] + generateHPVariation(), this.decaySpeed, maxHP[ZOMBIE], "SPAWNED");
    city[newPos[0]][newPos[1]] = newEntity;
    
    count[ZOMBIE] += 1;
    newEntity.setLocation(newPos);
    allThings.add(city[newPos[0]][newPos[1]]);

    assignValues(newEntity.getHValue(), newPos, HUMAN, valuesOfZombies); //- Assign the values in the proper value map 
  }

  /** processMouseActions(mousePos); 
   * Takes in a list of mouse positions that are in the grid, and decides what to do with them.
   * If they are on an empty square, add a zombie.
   * If they are on an entity, change selectedEntity to that entity.
   * @param mousePos, an ArrayList of int arrays, each with a mouse position on the grid.
   */
  public void processMouseActions(ArrayList<int[]> mousePos) {
    for (int x = mousePos.size() - 1; x >= 0; x--) {
      int[] newPos = mousePos.get(x);
        
      if (city[newPos[0]][newPos[1]] == null) { //- Add a zombie 
        addZombie(newPos);
      } else {
        for (int y = 0; y < allThings.size(); y++) { //- Get the entity that was selected and select it
          int[] entityLocation = allThings.get(y).getLocation();
          if (entityLocation[0] == newPos[0] && entityLocation[1] == newPos[1]) {
            this.selectedEntity = allThings.get(y);
          }
        }
      }
      
      mousePos.remove(x);
    }
  }

  /** selectedEntityExists(); 
   * Checks to see if there is a selected entity.
   * @return true if this.selectedEntity is not null, otherwise false.
   */
  public boolean selectedEntityExists() {
    if (this.selectedEntity != null) {
      return true;
    }

    return false;
  }
  
  /** checkForHumans(); 
   * Checks to see if there are any humans left in the map.
   * @return true if there is at least 1 human, otherwise false.
   */
  public boolean checkForHumans() {
    if (count[HUMAN] <= 0) {
      return false;
    }
    return true;
  }

  /** generateHPVariation(); 
   * Generates a random variation to be added onto an entity's HP when they spawn to offer individuality.
   * @return a random int from -5 to 8.
   */
  private int generateHPVariation() {
    return Simulation.randInt(-5, 8);
  }
    
  /// ENTITY EDITORS ///
  /** decayEntity(entityID); 
   * Decays the entity inside the ArrayList allThings at position entityID.
   * @param entityID, an int with the index of the entity inside the ArrayList allThings.
   */
  private void decayEntity(int entityID) {
    allThings.get(entityID).decay();
  }
  
  /** destroy(entityID); 
   * Destroys an entity at position entityID in the ArrayList by removing it from the map (if it's still there) and from allThings.
   * It resets the values if they have not already been reset, reduces the count, and also does specific actions based on conditions.
   * If the current entity was selected, try to find an alive child (if its a Human) and change selected entity to that child. Otherwise,
   * reset selected entity.
   * If the current entity was Human, remove it from both the parent's list of alive kids and all its kids' list of alive parents.
   * @param entityID, an int with the index of the entity inside the ArrayList allThings.
   */
  private void destroy(int entityID) {
    int[] position = allThings.get(entityID).getLocation();
    Entity entity = allThings.get(entityID);

    if (city[position[0]][position[1]] == allThings.get(entityID)) { //- If the entity died a normal death and still exists on the map
      city[position[0]][position[1]] = null;
      if (entity instanceof Human) {
        resetValues(entity.getHValue(), position, HUMAN, valuesOfHumans);
        resetValues(entity.getZValue(), position, ZOMBIE, valuesOfHumans);
      } else if (entity instanceof Zombie) {
        resetValues(entity.getHValue(), position, HUMAN, valuesOfZombies);
      } else {
        resetValues(entity.getHValue(), position, HUMAN, valuesOfPlants);
      }
    }
    
    if (entity instanceof Human) { 
      count[HUMAN] -= 1;
      Human person = (Human)entity;
      //- Remove this human from all its kids' list of alive parents and its parents' list of alive kids.
      if (person.hasAliveParents()) {
        for (int x = person.getParents().size() - 1; x >= 0 ; x--) {
          person.getParents().get(x).removeKid(person);
        }
      }

      if (person.hasAliveKids()) {
        for (int x = person.getKids().size() - 1; x >= 0; x--) {
          if (this.selectedEntity == person) { //- Change the selected entity to the first alive child if this human was selected.
            if (person.getKids().get(x).getHealth() > 0) {
              this.selectedEntity = person.getKids().get(x);
            }
          }
          
          person.getKids().get(x).removeParent(person);
        }        
      }

      if (((Human)entity).getGender() == 'M') {
        genderCount[MALE]--;
      } else {
        genderCount[FEMALE]--;
      }

    } else if (entity instanceof Zombie) {
      count[ZOMBIE] -= 1;

    } else {
      count[PLANT] -= 1;

    }
    
    if (entity == this.selectedEntity) { //- If this entity was selected and wasn't passed on successfully, reset the selected entity.
      this.selectedEntity = null;
    }

    allThings.remove(entityID);
  }
  /// ENTITY EDITORS ///
}