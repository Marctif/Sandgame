import java.awt.Color;
import java.util.Random;
import java.util.*;


//TODO: Add more pixelTypes. Update ELEMENT_COUNT

//Class responsible for managing game world objects
public class GameWorld
{
    //Map of all the pixels on-screen
    //Send this to user interface component to update user-display
    //2Darray indices represent (x,y) coordinates of each pixels
    ArrayList<Particle> existingParticles;
    final int PIXEL_MAP_HEIGHT = 800/4;
    final int PIXEL_MAP_WIDTH = 1240/4;
    Particle[][] pixelMap;//(0,0) coordinates are located at the top-left of the 2D array

    //General-use random variable
    Random randy = new Random();

    //Associates an element name with an array index.
    //Determines name of particle on pixelMap where
    //pixelMap's stored integer represents an index in the pixelType array
    final int ELEMENT_COUNT = 5;

    String[] pixelNameList = new String[ELEMENT_COUNT];

    //Boolean variables to keep track of mouse operations by user
    boolean mouseWasClicked;//Turns "true" when the visual component indicates the mouse was clicked
    int clickedX, clickedY;//Stores x,y coordinates of the last point the mouse was clicked
    boolean mouseWasDragged;//Turns "true" when the visual component indicates the mouse was dragged
    int draggedX1, draggedX2, draggedY1, draggedY2;//x,y coordinates of dragging mouse's the starting point and ending point

    //Variables to keep track of custom options
    String penElement;
    boolean penDragMode;
    boolean wallCollision;

    //Default constructor
    public GameWorld()
    {
        //Initializes pixelMap with "Nothing" in all indices
        pixelMap = new Particle[PIXEL_MAP_WIDTH  ][PIXEL_MAP_HEIGHT ];
        existingParticles = new ArrayList<Particle>();
        for(int iY = PIXEL_MAP_HEIGHT-1; iY >= 0; iY--){
            for(int iX = 0; iX < PIXEL_MAP_WIDTH; iX++){
                pixelMap[iX][iY] = new Particle("Nothing", Color.black, true, iX, iY);
            }
        }

        //Initializes pixelType with every element to be used in-game
        pixelNameList[0] = "Nothing";
        pixelNameList[1] = "Sand";//Completely unreactive non-static particle
        pixelNameList[2] = "Wall";//Completely unreactive static paticle
        pixelNameList[3] = "Water";//flowing particle that reacts with plant
        pixelNameList[4] = "Plant";//Seed that will drop then grow on contact to water

        //Initializes mouse variables
        mouseWasClicked = false;
        mouseWasDragged = false;

        //Initializes toggled option variables
        penElement = "Wall";
        penDragMode = false;
        wallCollision = false;
    }

    //Assesses the situation and determines the next course of action for the game
    //We must find a way to call this repeatedly during gamepay
    public void update(){

        //Updates each pixel on-screen, bottom to top, in horizontal layers

        for(int iY = PIXEL_MAP_HEIGHT-1; iY >= 0; iY--){
            for(int iX = 0; iX < PIXEL_MAP_WIDTH; iX++){

                updatePixel(pixelMap[iX][iY]);

                //Removes any pixels at the screen edge if wall-collision is off
                if(!wallCollision && (iX == 0 || iY == 0 || iX == PIXEL_MAP_WIDTH-1 || iY == PIXEL_MAP_HEIGHT-1))
                {
                    pixelMap[iX][iY] = new Particle(iX,iY);
                }
            }
        }
        /*for(Particle p : existingParticles)
        {
            updatePixel(p);
            if(!wallCollision && (p.x == 0 || p.y == 0 || p.x == PIXEL_MAP_WIDTH-1 || p.y == PIXEL_MAP_HEIGHT-1))
            {
                pixelMap[p.x][p.y] = new Particle(p.x,p.y);
            }
        }*/

        //Handles response to a mouse-click
        if(mouseWasClicked)
        {
            switch(penElement)
            {
                case "Nothing":
                    //If there's something in the target spot, replace it with nothing
                    if(!pixelMap[clickedX][clickedY].name.equals("Nothing"))
                    {
                        Particle p = new Particle(clickedX, clickedY);
                        pixelMap[clickedX][clickedY] = p;
                       // existingParticles.add(p);
                    }
                    break;
                case "Sand":
                    //If there's nothing in the target spot, replace it with sand
                    if(pixelMap[clickedX][clickedY].name.equals("Nothing"))
                    {
                        Particle p =  new Particle("Sand", Color.ORANGE, false, clickedX, clickedY);
                        pixelMap[clickedX][clickedY] = p;
                        existingParticles.add(p);
                    }
                    break;
                case "Wall":
                    //If there's nothing in the target spot, replace it with wall
                    if(pixelMap[clickedX][clickedY].name.equals("Nothing"))
                    {
                        Particle p =  new Particle("Wall", Color.darkGray, true, clickedX, clickedY);
                        pixelMap[clickedX][clickedY] = p;
                        existingParticles.add(p);
                    }
                    break;
                case "Water":
                    //If there's nothing in the target spot, replace it with wall
                    if(pixelMap[clickedX][clickedY].name.equals("Nothing"))
                    {
                        Particle p =  new Particle("Water", Color.blue, false, clickedX, clickedY);
                        pixelMap[clickedX][clickedY] = p;
                        existingParticles.add(p);
                    }
                    break;
                case "Plant":
                    //If there's nothing in the target spot, replace it with wall
                    if(pixelMap[clickedX][clickedY].name.equals("Nothing"))
                    {
                        Particle p =  new Particle("Plant", Color.green, false, clickedX, clickedY);
                        pixelMap[clickedX][clickedY] = p;
                        existingParticles.add(p);
                    }
                    break;
            }
            mouseWasClicked = false;

        }
    }

    //Updates the pixel space with the given particle
    //only considers pixel spaces below the given particle
    public void updatePixel(Particle thisPixel){
        //Change nothing if there's nothing in the pixel space
        if(thisPixel.y + 1 >= 200)
            return;
        if(thisPixel.name.equals("Nothing"))
        {
            return;
        }
        //Change nothing if particle doesn't fall
        else if(thisPixel.isStatic)
        {
            return;
        }
        //Moves particle 1 pixel downward if there's nothing in the pixel space below and the particle is not at the bottom of the screen
        else if(pixelMap[thisPixel.x][thisPixel.y + 1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1){
            pixelMap[thisPixel.x][thisPixel.y+1] = pixelMap[thisPixel.x][thisPixel.y];
            pixelMap[thisPixel.x][thisPixel.y] = new Particle(thisPixel.x, thisPixel.y);
            pixelMap[thisPixel.x][thisPixel.y+1].y++;
        }
        else{
            //Checks if the particle below can interect with thisParticle
            if(canParticleInteract(pixelMap[thisPixel.x][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
            {
                particleInteract(pixelMap[thisPixel.x][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
            }
            //Since there's an unreactive particle directly below, thisPixel randomly decides to try to slide left or slide right
            else if(randy.nextInt(2) == 0)
            {//Checks right side first
                if(pixelMap[thisPixel.x+1][thisPixel.y+1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1 && thisPixel.x+1 < PIXEL_MAP_WIDTH-1)
                {
                    pixelMap[thisPixel.x+1][thisPixel.y+1] = pixelMap[thisPixel.x][thisPixel.y];
                    pixelMap[thisPixel.x][thisPixel.y] = new Particle(thisPixel.x, thisPixel.y);
                    pixelMap[thisPixel.x+1][thisPixel.y+1].x++;
                    pixelMap[thisPixel.x+1][thisPixel.y+1].y++;
                }
                //Checks if the particle below+right can interect with thisParticle
                else if(canParticleInteract(pixelMap[thisPixel.x+1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
                {
                    particleInteract(pixelMap[thisPixel.x+1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
                }
                //Checking left side
                else if(pixelMap[thisPixel.x-1][thisPixel.y+1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1 && thisPixel.x-1 > 0)
                {
                    pixelMap[thisPixel.x-1][thisPixel.y+1] = pixelMap[thisPixel.x][thisPixel.y];
                    pixelMap[thisPixel.x][thisPixel.y] = new Particle(thisPixel.x, thisPixel.y);
                    pixelMap[thisPixel.x-1][thisPixel.y+1].y++;
                    pixelMap[thisPixel.x-1][thisPixel.y+1].x--;
                }
                //Checks if the particle below+left can interect with thisParticle
                else if(canParticleInteract(pixelMap[thisPixel.x-1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
                {
                    particleInteract(pixelMap[thisPixel.x-1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
                }
            }
            else
            {//Checks left side first
                if(pixelMap[thisPixel.x-1][thisPixel.y+1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1 && thisPixel.x-1 > 0)
                {
                    pixelMap[thisPixel.x-1][thisPixel.y+1] = pixelMap[thisPixel.x][thisPixel.y];
                    pixelMap[thisPixel.x][thisPixel.y] = new Particle(thisPixel.x, thisPixel.y);
                    pixelMap[thisPixel.x-1][thisPixel.y+1].y++;
                    pixelMap[thisPixel.x-1][thisPixel.y+1].x--;
                }
                //Checks if the particle below+left can interect with thisParticle
                else if(canParticleInteract(pixelMap[thisPixel.x-1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
                {
                    particleInteract(pixelMap[thisPixel.x-1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
                }
                //Checking right side
                else if(pixelMap[thisPixel.x+1][thisPixel.y+1].name.equals("Nothing") && thisPixel.y+1 < PIXEL_MAP_HEIGHT-1 && thisPixel.x+1 < PIXEL_MAP_WIDTH-1)
                {
                    pixelMap[thisPixel.x+1][thisPixel.y+1] = pixelMap[thisPixel.x][thisPixel.y];
                    pixelMap[thisPixel.x][thisPixel.y] = new Particle(thisPixel.x, thisPixel.y);
                    pixelMap[thisPixel.x+1][thisPixel.y+1].x++;
                    pixelMap[thisPixel.x+1][thisPixel.y+1].y++;
                }
                //Checks if the particle below+right can interect with thisParticle
                else if(canParticleInteract(pixelMap[thisPixel.x+1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]))
                {
                    particleInteract(pixelMap[thisPixel.x+1][thisPixel.y+1], pixelMap[thisPixel.x][thisPixel.y]);
                }
            }

            //If there's no empty space and no reactive elements underneath, no changes should be made to the particle in-question
        }



    }

    //Updates pixelMap after particleA and particleB interact with each other
    public void particleInteract(Particle particleA, Particle particleB)
    {

    }

    //Returns true if particleA and particleB can interact with each other
    public boolean canParticleInteract(Particle particleA, Particle particleB)
    {
        /*if((particleA.name == "1" && particleB.name == "2") || (particleA.name == "2" && particleB.name == "1"))
        {
            return true;
        }
        */
        return false;
    }

    //Called by Display when the mouse is clicked at a specific location on the pixel map
    //parameters accept the pixelMap coordinate at which the mouse was clicked
    public void mouseClicked(int x, int y){
        clickedX = x;
        clickedY = y;
        mouseWasClicked = true;
        update();
    }

    //Called by Display when the mouse was dragged at a specific location on the pixel map
    //parameters accept the pixelMap coordinate at which the mouse started dragging and stopped dragging
    public void mouseDragged(int x1, int y1, int x2, int y2){
        draggedX1 = x1;
        draggedY1 = y1;
        draggedX2 = x2;
        draggedY2 = y2;
        mouseWasDragged = true;
    }

    public void changePenType(String type)
    {
        penElement = type;
    }

    public void test()
    {

    }

}
