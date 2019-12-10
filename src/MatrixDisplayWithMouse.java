import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;

/** Class: MatrixDisplayWithMouse
 * @version 1.5
 * @date 11/21/2019
 * @author Mangat + Joseph Wang
 * @description: A program that controls the GUI of Cheerville.
 */

class MatrixDisplayWithMouse extends JFrame {
  private int maxX, maxY, GridToScreenRatio, smallPadding, bigPadding, gridSize; //- These have to do with the size and scales
  private int maleHumanCount, femaleHumanCount, humanCount, plantCount, zombieCount, turns;
  private int buttonStartX, buttonStartY, buttonWidth, buttonLength;
  private final int GRAPH_SIZE = 225; //- Maximum size of the graph we have
  private final int MIN_RAINDROPS;
  private int totalRaindrops;

  private String currentWeather, currentSeason;
  private boolean stopSimulation = true;
  private Entity selectedEntity;
  private Entity[][] matrix; //- For the city

  private ArrayList<int[]> mouseClicks, graph;

  //- Preset the colours for each entity
  private  Color maleColor = new Color(0, 151, 255);
  private Color femaleColor = Color.MAGENTA;
  private Color maleChildColor = Color.CYAN;
  private Color femaleChildColor = Color.PINK;
  private Color plantColor = Color.GREEN;
  private Color zombieColor = Color.RED;
  private Color[] colors = {maleColor, femaleColor, plantColor, zombieColor};
  
  /** MatrixDisplayWithMouse(title, matrix); 
   * Sets up a matrix display with mouse listener attatched.
   * @param title, a String with the title of the program.
   * @param matrix, the map of the city to be displayed
   */
  MatrixDisplayWithMouse(String title, Entity[][] matrix) {
    super(title);
    
    this.matrix = matrix;

    //- Set up size restraints
    maxX = 1024;
    maxY = 768;
    GridToScreenRatio = (maxX / 2) / (matrix.length+1);  //ratio to fit in screen as square map
    smallPadding = 25;
    bigPadding = 100;

    this.graph = new ArrayList<int[]>(); //- Initialize the graph
    this.gridSize = 2;

    this.turns = 0;
    this.totalRaindrops = 50;
    this.MIN_RAINDROPS = 40;

    //- JFrame stuff
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(maxX, maxY);
    this.setResizable(false);
    
    this.getContentPane().add(new MatrixPanel());
    this.setLayout(new GridLayout(1, 2));
    
    this.setVisible(true);

    //- Set up starting button sizes
    buttonStartX = maxX - bigPadding * 2;
    buttonStartY = smallPadding/2;
    buttonWidth = smallPadding;
    buttonLength = smallPadding * 6;

    mouseClicks = new ArrayList<int[]>();
  }
  
  /** refresh(); 
   * Refreshs the display.
   */
  public void refresh() { 
    this.repaint();
  }

  //Inner Class 
  class MatrixPanel extends JPanel {
    
    MatrixPanel() { 
      addMouseListener(new MatrixPanelMouseListener());
    }
    
    /** paintComponent(g); 
     * Paints the actual components onto the GUI.
     * @param g, a Graphics object that is used for drawing.
     */
    public void paintComponent(Graphics g) {        
      super.repaint();
      
      setDoubleBuffered(true); 
      
      for (int i = 0; i < matrix.length; i++)  { 
        for (int j = 0; j < matrix[0].length; j++)  { 
          if (matrix[i][j] == null) {
            if (currentWeather.equals("Rainy")) {       //- Change the background colour based on the weather
              g.setColor(Color.GRAY);
            } else if (currentWeather.equals("Cloudy")) {
              g.setColor(Color.LIGHT_GRAY);
            } else {
              g.setColor(Color.WHITE);
            }
          } else if (matrix[i][j] instanceof Human) {   //- This block matches character-colour pairs
            Human person = (Human)matrix[i][j];
            int personHealth = person.getHealth();
            if (personHealth > 100) {
              personHealth = 100;
            }
            
            if (person.getGender() == 'F') {
              if (person.getAge() < 16) {
                g.setColor(femaleChildColor);
              } else {
                g.setColor(femaleColor);
              }
            } else if (person.getGender() == 'M') {
              if (person.getAge() < 16) {
                g.setColor(maleChildColor);
              } else {
                g.setColor(maleColor);
              }
            }
          } else if (matrix[i][j] instanceof Zombie) {
            g.setColor(zombieColor);

          } else if (matrix[i][j] instanceof Plant) {  
            g.setColor(plantColor);
          } else {
            g.setColor(Color.BLACK);
          }
          
          g.fillRect(j*GridToScreenRatio, i*GridToScreenRatio, GridToScreenRatio, GridToScreenRatio);
          //- Change the grid colour based on the season
          Color seasonColor;
          if (currentSeason.equals("Spring")) {
            seasonColor = new Color(12, 132, 0);
          } else if (currentSeason.equals("Summer")) {
            seasonColor = new Color(125, 125, 0);
          } else if (currentSeason.equals("Fall")) {
            seasonColor = new Color(150, 50, 0);
          } else if (currentSeason.equals("Winter")) {
            seasonColor = new Color(0, 80, 150);
          } else {
            seasonColor = Color.BLACK;
          }

          g.setColor(seasonColor);

          g.drawRect(j*GridToScreenRatio, i*GridToScreenRatio, GridToScreenRatio, GridToScreenRatio); //- Draw the border

          if (matrix[i][j] == selectedEntity && selectedEntity != null) { //- Draw the selected indicator onto the grid
            g.setColor(Color.YELLOW);
            g.drawRect(j*GridToScreenRatio + 1, i*GridToScreenRatio + 1, GridToScreenRatio - 2, GridToScreenRatio - 2);
            g.drawRect(j*GridToScreenRatio + 3, i*GridToScreenRatio + 3, GridToScreenRatio - 6, GridToScreenRatio - 6);
          }
        }
      }

      //- Draw some text based information

      g.drawString(currentSeason, matrix.length * GridToScreenRatio + smallPadding, smallPadding);

      g.setColor(Color.BLACK);
      g.drawString("Total Humans: " + Integer.toString(humanCount), matrix.length * GridToScreenRatio + smallPadding, maxY - smallPadding * 2);
      g.drawString("Total Plants: " + Integer.toString(plantCount), matrix.length * GridToScreenRatio + (bigPadding * 2), maxY - smallPadding * 2);
      g.drawString("Total Zombies: " + Integer.toString(zombieCount), matrix.length * GridToScreenRatio + (bigPadding * 3), maxY - smallPadding * 2);
      g.drawString("Total Turns: " + Integer.toString(turns), matrix.length * GridToScreenRatio + smallPadding, maxY - smallPadding * 3);

      g.drawString(currentWeather, matrix.length * GridToScreenRatio + (bigPadding * 2), smallPadding);

      //- Draw other GUI things
      drawGraph(g);
      drawSelectedEntity(g);
      drawStopButton(g);

      if (currentWeather.equals("Rainy")) {
        drawRaindrops(g);
        totalRaindrops = MIN_RAINDROPS + Simulation.randInt(-10, 30);
      }
    }

    /** drawGraph(g);
     * Draws a graph onto the GUI, based on the counts of each entity. This graph increases in size as the grid increases.
     * This graph will go out of bounds if gridsize is greater than [25, 25]
     * @param g, a Graphics object used for drawing.
     */
    private void drawGraph(Graphics g) {
      int xIndex = matrix.length * GridToScreenRatio + smallPadding - gridSize;
      int yIndex = smallPadding * 2;
      int currentGraphSize = graph.size();

      if (graph.size() > GRAPH_SIZE) { //- This is to ensure we don't go out of bounds during the drawing
        graph.remove(0);
      }
      
      for (int x = 0; x < currentGraphSize; x++) {
        xIndex += gridSize;                                   //- For every data set, just add 1 to x
        yIndex = smallPadding * 2;                            //- Reset y
        for (int y = 0; y < graph.get(x).length; y++) {
          for (int z = 0; z < graph.get(x)[y]; z++) {
            g.setColor(colors[y]);                            //- Get the respective colour for that data point
            g.fillRect(xIndex, yIndex, gridSize, gridSize);

            yIndex += 1;                                      //- Add one to y for next data point
          }
        }
      }
      
      if (graph.size() > 0) {
        //- Get the percents and total and display them to the right of the graph
        int lastPoint = graph.size() - 1;
        int[] lastDataSet = graph.get(lastPoint);
        int rightBorder = (matrix.length * GridToScreenRatio + smallPadding) + (gridSize * GRAPH_SIZE) + 2;
        int buffer = smallPadding * 2;
        
        double total = humanCount + plantCount + zombieCount;
        int malePercent = (int)(Math.round((maleHumanCount / total) * 100));
        int femalePercent = (int)(Math.round((femaleHumanCount / total) * 100));
        int plantPercent = (int)(Math.round((plantCount / total) * 100));
        int zombiePercent = (int)(Math.round((zombieCount / total) * 100));
        
        int[] percents = {malePercent, femalePercent, plantPercent, zombiePercent};
        
        for (int x = 0; x < lastDataSet.length; x++) {
          
          if (lastDataSet[x] > 0) { //- if there's no count, there's no point in displaying it
            g.setColor(colors[x]);
            
            g.drawString(Integer.toString(lastDataSet[x]) + " ("+ percents[x] + "%)", rightBorder, buffer + lastDataSet[x]);
            buffer += lastDataSet[x];
          }
        }
      }

      g.setColor(Color.BLACK);
      
      g.drawRect(matrix.length * GridToScreenRatio + smallPadding, smallPadding * 2, gridSize * GRAPH_SIZE, matrix.length * matrix[0].length);
    }

    /** drawStopButton(g); 
     * Draws an image of a button, which switches based on this.stopSimulation.
     * @param g, a Graphics object used for drawing.
     */
    private void drawStopButton(Graphics g) {
      Image button;
      if (stopSimulation) {
        button = Toolkit.getDefaultToolkit().getImage("./images/resume.png");
      } else {
        button = Toolkit.getDefaultToolkit().getImage("./images/stop.png");
      }

      g.drawImage(button, buttonStartX, buttonStartY, this);
    }

    /** drawSelectedEntity(g); 
     * If there is a selected entity, find out all its information depending on what it is and draw it onto the GUI.
     * @param g, a Graphics object used for drawing.
     */
    private void drawSelectedEntity(Graphics g) {
      int gridLength = matrix.length * GridToScreenRatio;

      //- Get the starting locations of the box
      int boxWidth = gridLength - (smallPadding * 2);
      int boxStartX = smallPadding;
      int boxStartY = gridLength + smallPadding;
      int boxEndX = boxStartX + boxWidth;
      g.setColor(Color.DARK_GRAY);

      g.fillRect(boxStartX, boxStartY, boxWidth, (maxY - gridLength) - smallPadding * 4);

      g.setColor(Color.WHITE);

      if (selectedEntity == null) { //- Don't display anything important if there's nothing to display
        g.drawString("No entity selected.", boxStartX + smallPadding, boxStartY + smallPadding);
      } else {
        g.drawString("Entity selected.", boxStartX + smallPadding, boxStartY + smallPadding);

        String entityType, pos, name;
        int[] currentPos;

        if (selectedEntity instanceof Plant) {
          //- Get information that will be used and displayed
          Plant selectedPlant = (Plant)selectedEntity;

          name = selectedPlant.getName();
          currentPos = selectedPlant.getLocation();
          pos = "[" + currentPos[1] + "," + currentPos[0] + "]";
          entityType = "Plant";

          g.setColor(plantColor);

        } else if (selectedEntity instanceof Zombie) {
          //- Get information that will be used and displayed
          Zombie selectedZombie = (Zombie)selectedEntity;
          String kills = Integer.toString(selectedZombie.getKills());
          String status = selectedZombie.getCreatedBy();

          currentPos = selectedZombie.getLocation();
          pos = "[" + currentPos[1] + "," + currentPos[0] + "]";
          name = selectedZombie.getName();
          entityType = "Zombie";

          g.setColor(zombieColor);
          
          //- Specific things only Zombies have
          g.drawString("Kills: " + kills, boxEndX - smallPadding * 3, boxStartY + smallPadding * 5);
          g.drawString(status, boxEndX - smallPadding * 3, boxStartY + smallPadding * 6);
          
        } else {
          //- Get information that will be used and displayed
          Human selectedHuman = (Human)selectedEntity;
          String age = Integer.toString(selectedHuman.getAge());
          String aliveKids = Integer.toString(selectedHuman.getAmountOfAliveKids());
          String parentNames = Arrays.toString(selectedHuman.getParentNames());
          
          name = selectedHuman.getName();
          currentPos = selectedHuman.getLocation();
          pos = "[" + currentPos[1] + "," + currentPos[0] + "]";

          entityType = "Human - ";
          if (selectedHuman.getGender() == 'M') {
            entityType = entityType + "Male - "; //- Change the entity type based on what it is
            if (selectedHuman.getAge() < Town.MIN_REPRODUCTION_AGE) {
              entityType = entityType + "Child";
              g.setColor(maleChildColor);

            } else {
              entityType = entityType + "Adult"; //- Change the entity type based on what it is
              g.setColor(maleColor);

              g.drawString("Alive Children: " + aliveKids, boxEndX - smallPadding * 5, boxStartY + smallPadding * 5);
            }

          } else {
            entityType = entityType + "Female - "; //- Change the entity type based on what it is
            if (selectedHuman.getAge() < Town.MIN_REPRODUCTION_AGE) {
              entityType = entityType + "Child";
              g.setColor(femaleChildColor);

            } else {
              entityType = entityType + "Adult"; //- Change the entity type based on what it is
              g.setColor(femaleColor);

              g.drawString("Alive Children: " + aliveKids, boxEndX - smallPadding * 5, boxStartY + smallPadding * 5);
            }
          }

          //- Specific things that only humans have
          g.drawString(name, boxStartX + smallPadding, boxStartY + smallPadding * 2);
          g.drawString("Age: " + age, boxEndX - smallPadding * 3, boxStartY + smallPadding * 3);
          g.drawString("Parents: " + parentNames, boxStartX + smallPadding, boxStartY + smallPadding * 6);
        }
        
        //- Common aspects that they all share
        g.drawString(entityType, boxStartX + smallPadding, boxStartY + smallPadding * 5);
        g.drawString(name, boxStartX + smallPadding, boxStartY + smallPadding * 2);

        g.drawString("HP: " + selectedEntity.getHealth(), boxEndX - smallPadding * 3, boxStartY + smallPadding * 2);
        g.drawString("Pos: " + pos, boxEndX - smallPadding * 3, boxStartY + smallPadding * 4);

        g.fillRect(boxStartX + smallPadding, boxStartY + smallPadding * 3, 19, 19);
      }
    }

    /** drawRaindrops(g); 
     * Takes g and draws a random assortment of "raindrops" onto the grid when it's raining.
     * @param g, a Graphics object used for drawing.
     */
    private void drawRaindrops(Graphics g) {
      for (int x = 0; x < totalRaindrops; x++) {
        Color rain = new Color(0, 0, 150 + Simulation.randInt(0, 55)); 
        g.setColor(rain);
        
        int raindropWidth = Simulation.randInt(3, 8);
        g.fillOval(Simulation.randInt(10, matrix[0].length * GridToScreenRatio - 10), Simulation.randInt(10, matrix.length * GridToScreenRatio  - 10), raindropWidth, raindropWidth);
      }
    }
  }
  
  //Mouse Listener 
  class MatrixPanelMouseListener implements MouseListener{ 
     //Mouse Listner Stuff
    /** mousePressed(e); 
     * This handles mouse pressed events, and does actions based on the results.
     * @param e, a MouseEvent object that represents a mouse pressed action.
     */
    public void mousePressed(MouseEvent e) {
      int pointX = e.getPoint().x;
      int pointY = e.getPoint().y;

      //- Check if the stop/resume button was clicked
      if ((pointX >= buttonStartX) && (pointX < buttonStartX + buttonLength) && 
          (pointY >= buttonStartY) && (pointY < buttonStartY + buttonWidth)) {
        if (stopSimulation) {
          stopSimulation = false;
        } else {
          stopSimulation = true;
        }
      }

      //- Converts the points to proper grid coordinates
      int xPos = e.getPoint().x / GridToScreenRatio;
      int yPos = e.getPoint().y / GridToScreenRatio;
      int[] pos = {yPos, xPos};
      
      //- If they are within the grid, then add them to mouseClicks
      if (((pos[0] >= 0) && (pos[0] < matrix.length)) && ((pos[1] >= 0) && (pos[1] < matrix[0].length))) {
        mouseClicks.add(pos);
      }
    }

    /** mouseReleased(e); 
     * This handles mouse released events.
     * @param e, a MouseEvent object that represents a mouse released action.
     */
    public void mouseReleased(MouseEvent e) {
    }

    /** mouseEntered(e); 
     * This handles mouse entered events.
     * @param e, a MouseEvent object that represents a mouse entered action.
     */
    public void mouseEntered(MouseEvent e) {
    }

    /** mouseExited(e); 
     * This handles mouse exited events.
     * @param e, a MouseEvent object that represents a mouse exited action.
     */
    public void mouseExited(MouseEvent e) {
    }

    /** mouseClicked(e); 
     * This handles mouse clicked events.
     * @param e, a MouseEvent object that represents a mouse clicked action.
     */
    public void mouseClicked(MouseEvent e) {
    }
  }

  /** getSelectedEntity(); 
   * Gets the current selected entity.
   * @return an Entity, which is the current selected entity.
   */
  public Entity getSelectedEntity() {
    return this.selectedEntity;
  }

  /** shouldStopSimulation(); 
   * Gets and returns the stopSimulation value to determine if the simulation should be stopped.
   * @return a boolean, which is this.stopSimulation, which changes depending on whether simulation should be stopped or not.
   */
  public boolean shouldStopSimulation() {
    return this.stopSimulation;
  }
  
  /** getMouseClickLocations(); 
   * Gets the current mouse clicks list.
   * @return an ArrayList of int arrays containing all the mouse click positions.
   */
  public ArrayList<int[]> getMouseClickLocations() {
    return mouseClicks;
  }

  /** updateCounts(maleHumanCount, femaleHumanCount, humanCount, zombieCount, plantCount); 
   * Updates all the counts stored with the new numbers passed in.
   * @param maleHumanCount, an int which contains the new male human total.
   * @param femaleHumanCount, an int which contains the new female human total.
   * @param humanCount, an int which contains the new overall human total.
   * @param zombieCount, an int which contains the new zombie total.
   * @param plantCount, an int which contains the new plant total.
   */
  public void updateCounts(int maleHumanCount, int femaleHumanCount, int humanCount, int zombieCount, int plantCount) {
    this.maleHumanCount = maleHumanCount;
    this.femaleHumanCount = femaleHumanCount;
    this.zombieCount = zombieCount;
    this.plantCount = plantCount;
    this.humanCount = humanCount;

  }

  /** updateGraph(); 
   * If the simulation is not stopped, takes the current count numbers and adds it as a new data set to the graph.
   * Also removes a dataset if the graph size is greater than the maximum.
   */
  public void updateGraph() {
    if (!(this.stopSimulation)) {
      int maleHumanNum = this.maleHumanCount;
      int femaleHumanNum = this.femaleHumanCount;
      int plantNum = this.plantCount;
      int zombieNum = this.zombieCount;
      int[] newData = {maleHumanNum, femaleHumanNum, plantNum, zombieNum}; 
      
      this.graph.add(newData);

      if (graph.size() > GRAPH_SIZE) {
        graph.remove(0);
      }
      
    }
  }

  /** updateClimate(currentWeather, currentSeason); 
   * Updates the current weather and season to the ones passed in.
   * @param currentWeather, a String that contains the new current weather.
   * @param currentSeason, a String that contains the new current season.
   */
  public void updateClimate(String currentWeather, String currentSeason) {
    this.currentWeather = currentWeather;
    this.currentSeason = currentSeason;
  }

  /** updateTurns(turns); 
   * Updates the total turns passed to the passed in int turns.
   * @param turns. an int with the new total turns passed.
   */
  public void updateTurns(int turns) {
    this.turns = turns;
  }

  /** updateSelectedEntity(selectedEntity); 
   * Updates this selectedEntity with a provided entity.
   * @param selectedEntity, an Entity that is the new selected entity.
   */
  public void updateSelectedEntity(Entity selectedEntity) {
    this.selectedEntity = selectedEntity;
  }

  /** selectedEntityExists(); 
   * Checks to see if this.selectedEntity exists.
   * @return true if this.selectedEntity is not null, otherwise false
   */
  public boolean selectedEntityExists() {
    if (this.selectedEntity == null) {
      return false;
    }
    return true;
  }
}