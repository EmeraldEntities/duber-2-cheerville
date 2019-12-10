import java.util.ArrayList;

/** Interface: Movable
 * @version 1.2
 * @author Joseph Wang
 * @date 11/21/2019
 * @description: The interface for things that can move.
 */

public interface Movable {
  //- All the possible direction modifiers that our entities can move in
  public final int[][] directions = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
  
  //- Direction values used for indexing
  public final int UP = 0;
  public final int UPRIGHT = 1;
  public final int RIGHT = 2;
  public final int DOWNRIGHT = 3;
  public final int DOWN = 4;
  public final int DOWNLEFT = 5;
  public final int LEFT = 6;
  public final int UPLEFT = 7;

  public void move(int[] newCoords);
  public int[] decideMovement(Entity[] surroundingEntities, double[] surroundingValues);
  public ArrayList<Integer> findPossibleMoves(Entity[] surroundingEntities);
}
