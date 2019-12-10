/** Class: Plant
 * @version 1.1
 * @author Joseph Wang
 * @date 11/21/2019
 * @description: A small plant that fuels all the humans in Cheerville!
 */

class Plant extends Entity implements HasName{
  private String name;

  //// CONSTRUCTORS ////
  /** Plant(health, decaySpeed); 
   * Creates a new plant with the given values. Calls super with most of them, and assigns a name to its own
   * class variable.
   * @param health, an int with this plant's health.
   * @param decaySpeed, an int with this plant's decay speed.
   */
  Plant(int health, int decaySpeed) {
    super(health, decaySpeed, 2.0, 0);

    this.name = Simulation.nameGenerator.generatePlantName();
  }
  //// CONSTRUCTORS ////

  @Override
  /** getName(); 
   * Gets this plant's name.
   * @return a String, with this plant's name.
   */
  public String getName() {
    return this.name;
  }
}