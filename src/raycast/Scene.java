/** 
 *  Title of project
 * 
 *  Date of completion
 * 
 *  This program was created under the collaboration of Nathan Grimsey, Eric Lumpkin, Dylan Gibbons-Churchward, and Matthew McGuinn
 *  for Martin Hock's CS143 class in the Fall quarter of 2020.
 * 
 *  This code may be found at https://github.com/CS143-Raycasting-Project/Raycast along with documentation.
 */

package raycast;

import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class Scene extends JPanel {
    //Points were causing rounding issues, so I just made the coords 2 separate doubles.
    private double playerX;
    private double playerY;
    private int playerRotation = 0; //This is in degrees so that I can just use an int.
    private int miniMapSize = Main.cellSize / 4; //Scales the minimap
    public static Maze maze = new Maze(Main.mazeSize, Main.mazeSize);
    private int[][] mazeWalls = maze.getMaze();
    private int rayCastScreenPixelColumns = 1280;
    public Scene(double x, double y) {
        this.playerX = x;
        this.playerY = y;
    }
    public void move(String direction) { //I use some simple trig here to change how the movement is done depending on rotation.
        if (direction.equals("left")) {
            playerX -= Math.cos(Math.toRadians(playerRotation));
            playerY -= Math.sin(Math.toRadians(playerRotation));
        }
        else if (direction.equals("right")) {
            playerX += Math.cos(Math.toRadians(playerRotation));
            playerY += Math.sin(Math.toRadians(playerRotation));
        }
        else if (direction.equals("forwards")) {
            playerX += Math.sin(Math.toRadians(playerRotation));
            playerY -= Math.cos(Math.toRadians(playerRotation));
        }
        else if (direction.equals("backwards")) {
            playerX -= Math.sin(Math.toRadians(playerRotation));
            playerY += Math.cos(Math.toRadians(playerRotation));
        }
        
    }

    public void rotate(int angle) {
        playerRotation += angle;
    }

    public void renderFrame() {
        repaint();
    }
    @Override
    public void paintComponent(Graphics g) {
        double start = System.nanoTime();
        super.paintComponent(g);
        this.setBackground(Color.BLACK);
        Graphics2D g2d = (Graphics2D) g;
        // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); /* This is antialiasing. We can turn this on later if necessary */
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < Main.mazeSize; i++) { //This displays the maze graphically
            for (int j = 0; j < Main.mazeSize; j++) {
                if (Main.raymap.findTurfByIndex(i,j).turfType == 1) {
                    g2d.fillRect(Main.windowX + j * miniMapSize, i * miniMapSize, miniMapSize, miniMapSize);
                }
            }
        }//*/
        Ray pixel;
        double collision;
        int columnHeight;
        //This does the collision calculations and renders the scene in 3D
        for (int x = 0; x < rayCastScreenPixelColumns; x++) {
            double cameraX = 2 * x / (double)rayCastScreenPixelColumns - 1;
            pixel = new Ray(playerY / (double)Main.cellSize, playerX / (double)Main.cellSize, Math.toRadians(180-playerRotation), cameraX);
            collision = pixel.findCollision();
            //How tall the column of pixels will be at x. We use the inverse of the collision distance because as the distance increases,
            //the height of the column should decrease. This is then multiplied by the window height and scaled by 10
            columnHeight = (int)(1 / collision / Main.cellSize * Main.windowY * 10);
            if(255 - (int)(collision * 15) >= 0) { //This if statement makes sure that the lowest brightness a color can be is black
                g2d.setColor(new Color(255 - (int)(collision * 15), 0, 0));
            }
            else {
                g2d.setColor(Color.BLACK);
            }
            //This draws the column of pixels on the x value; it's on based on the distance from the collision
            g2d.drawLine(x, Main.windowY / 2 - columnHeight, x, Main.windowY / 2 + columnHeight);
            //as of right now you need to switch x and y, i dont know why. you also need to subtract player rotation from 180 degrees
            //and turn it to radians
        }
        g2d.setColor(Color.RED);
        //Rotates the player on the 2D map graphic
        g2d.rotate(Math.toRadians(playerRotation), 1280 + playerX / 4 + miniMapSize / 2, playerY / 4 + miniMapSize / 2);
        //Draws the player on the 2D map graphic
        g2d.fillRect((int)(1280 + playerX / 4), (int)(playerY / 4), miniMapSize, miniMapSize);
        g2d.drawLine((int)(1280 + playerX / 4) + miniMapSize / 2, (int)(playerY / 4) + miniMapSize / 2, (int)(1280 + playerX / 4) + miniMapSize / 2, (int)(playerY / 4) - miniMapSize / 2);
        double end = System.nanoTime();
        //System.out.println((double)(end - start)/1000000); //with 4000 rays it should take between 0.8 and 1.3 MILLISECONDS per frame
    }

}
