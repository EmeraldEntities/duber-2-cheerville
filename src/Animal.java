import java.util.ArrayList;

/** Class: Animal
 * @version 1.2
 * @author Joseph Wang
 * @date 11/21/2019
 * @description: An intelligent entity that can think and move.
 */

abstract class Animal extends Entity implements Movable {
  //// CONSTRUCTORS ////

  /** Animal(health, decayValue, hValue, zValue); 
   * The constructor for all the animals. Takes in the specified values and calls super with them.
   * @param health, an int that has this animal's health.
   * @param decayValue, an int that has this animal's decay value.
   * @param hValue, a double that has this animal's value to humans.
   * @param zValue, a double that has this animal's value to zombies.
   */
  Animal(int health, int decayValue, double hValue, double zValue) {
    super(health, decayValue, hValue, zValue);
  }
  
  //// CONSTRUCTORS ////

  abstract public int[] decideMovement(Entity[] surroundingEntities, double[] surroundingValues);

  abstract public ArrayList<Integer> findPossibleMoves(Entity[] surroundingEntities);
  
  @Override
  /** move(newCoords); 
   * Takes in the coords of the new destination and sets this animal's position to them.
   * @param newCoords, an int array with the new coordinates.
   */
  public void move(int[] newCoords) {
    this.setLocation(newCoords);

  }
}