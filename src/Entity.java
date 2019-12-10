/** Class: Entity
 * @version 2.1
 * @author Joseph Wang
 * @date 11/21/2019
 * @description: Anything that appears on the grid is an entity. It is the superclass of all things living.
 */

abstract class Entity {
  private int health, decayValue;
  private double hValue, zValue;
  private int[] location = new int[2];
  private boolean shouldPerformAction = true;
  
  //// CONSTRUCTORS ////
  /** Entity(health, decayValue, hValue, zValue); 
   * Creates a new entity with the specified values. Assigns each of them to each of their class variables.
   * @param health, an int with this entity's health.
   * @param decayValue, an int with this entity's decay value.
   * @param hValue, a double with this entity's value to humans.
   * @param zValue, a double with this entity's value to zombies.
   */
  Entity(int health, int decayValue, double hValue, double zValue) {
    this.health = health;
    this.decayValue = decayValue;
    this.hValue = hValue;
    this.zValue = zValue;
  }
  
  //// CONSTRUCTORS ////
  
  /// GETTERS/SETTERS ////

  /** getHealth(); 
   * Gets this entity's health and returns it.
   * @return an int, with this entity's health.
   */
  public int getHealth() {
    return this.health;
  }
  
  /** setHealth(value); 
   * Takes in a value and sets this entity's health equal to that value.
   * @param value, an int with the new health of this entity.
   */
  public void setHealth(int value) {
    this.health = value;
  }
  
  /** getLocation(); 
   * Gets this entity's location and returns it.
   * @return an int array, with this entity's location.
   */
  public int[] getLocation() {
    return this.location;
  }

  /** setLocation(newLocation); 
   * Takes in a new location, and sets this entity's location to that new location.
   * @param newLocation, an int array with the new coordinates of this entity.
   */
  public void setLocation(int[] newLocation) {
    this.location[0] = newLocation[0];
    this.location[1] = newLocation[1];
  }
  
  /** getDecayValue(); 
   * Gets this entity's decay value.
   * @return an int, with this entity's decay value.
   */
  public int getDecayValue() {
    return this.decayValue;
  }
  
  /** getShouldPerformAction(); 
   * Gets whether this entity should perform an action or not.
   * @return a boolean with this entity's shouldPerformAction value.
   */
  public boolean getShouldPerformAction() {
    return this.shouldPerformAction;
  }
  
  /** setShouldPerformAction(value); 
   * Takes a boolean, and sets this entity's shouldPerformAction value to that value.
   * @param value, a boolean which controls whether this entity should perform an action or not.
   */
  public void setShouldPerformAction(boolean value) {
    this.shouldPerformAction = value;
  }
  
  /** getHValue(); 
   * Gets this entity's value to humans.
   * @return a double, with this entity's value to humans.
   */
  public double getHValue() {
    return this.hValue;
  }

  /** getZValue(); 
   * Gets this entity's value to zombies.
   * @return a double, with this entity's value to zombies.
   */
  public double getZValue() {
    return this.zValue;
  }
  /// GETTERS/SETTERS ////
  
  /** decay(); 
   * Reduces this entity's health by its decay value.
   */
  public void decay() {
    this.health -= this.decayValue;
  }
  
  /** isDead(); 
   * Checks whether this entity is dead or not.
   * @return true if this entity's health is below or equal to 0, false otherwise.
   */
  public boolean isDead() {
    if (this.health <= 0) {
      return true;
    }
    return false;
  }
}