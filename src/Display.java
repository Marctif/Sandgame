        import java.awt.*;
        import java.awt.event.*;
        import java.awt.geom.Ellipse2D;
        import java.awt.geom.Line2D;
        import java.util.ArrayList;
        import java.util.Queue;

        import javax.swing.BorderFactory;
        import javax.swing.JPanel;
        import javax.swing.JFrame;
        import javax.swing.JMenu;
        import javax.swing.JMenuBar;
        import javax.swing.JMenuItem;
        import javax.swing.SwingUtilities;
        import javax.swing.Timer;

public class Display extends JFrame {
    static int count = 0;
    public static int xVal;
    public static int numLevels = 10;
    public static int yVal;
    public static int xVal2;
    public static int yVal2;
    public static int tempXD, tempYD, tempXE, tempYE;
    public static int xA, xB, xC, xD;
    public static int yA, yB, yC, yD;
    public static int xAB, yAB;
    public static int yE, xE;


    private static final long serialVersionUID = 1L;


    public static void main(String[] args) {

        // creates and shows the gui
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();

            }
        });

    }

    public static void createAndShowGUI() {

        // The inner workings of creating the specific window of the GUI

        System.out.println("Created GUI on EDT? "
                + SwingUtilities.isEventDispatchThread());

        // create a JFrame object, the window will be called square example.
        // tutorial for making a menu via oracle's website:
        // https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("file");
        menuBar.add(menu);
        JMenuItem menuItem2 = new JMenuItem("Quit");
        JMenuItem menuItem3 = new JMenuItem("Sand");
        JMenuItem menuItem4 = new JMenuItem("Wall");
        menu.add(menuItem2);
        menu.add(menuItem3);
        menu.add(menuItem4);

        // https://stackoverflow.com/questions/9778621/how-to-make-a-jmenu-item-do-something-when-its-clicked
        // for making a menu do something when clicked

        menuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);

            }

        });


        JFrame frame = new JFrame("Sand Falling Game");
        frame.setJMenuBar(menuBar);

        // When we close out of the application, we will exit it using this
        // line.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // display the window

        final sqFrame sm = new sqFrame();
        menuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                sm.gameWorld.changePenType("Sand");

            }

        });
        menuItem4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                sm.gameWorld.changePenType("Wall");

            }

        });



        sm.repaint();

        frame.add(sm);

        //Timer for repetitive updating
        int timeSlice = 50;  // updates the every assigned number of milliseconds
        Timer timer = new Timer(timeSlice,  (e) -> sm.repaint());

        // use pack to size the window appropriately (or can be manually set).
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // display the window
        frame.setVisible(true);




        timer.start();
    }

}

//Reads mouse input and paints user-display for the sand box
class sqFrame extends Canvas implements MouseListener, MouseMotionListener{
    private static final long serialVersionUID = 1L;
    int xWindow = 1240;
    int yWindow = 800;
    int[] xSPoints = new int[4];
    int[] ySPoints = new int[4];
    int[] tXPoints = new int[3];
    int[] tYPoints = new int[3];

    private static final double RADIUS    = 15.0;
    private static final double DIAMETER  = 2.0 * RADIUS;
    private static final Color  XOR_COLOR = Color.yellow;

    private static Shape m_circle = null;

    ArrayList<Point> dragPointQueue = new ArrayList<Point>();

    //game logic object
    GameWorld gameWorld = new GameWorld();

    //Mouse-drag tracking variables
    boolean mouseBeenDragging;//True while the user is holding the mouse down with drag-mode on
    int startX, startY;//(x,y) coordinates of the point at which the user started holding the mouse down


    public sqFrame(){
        addMouseListener(this);
        addMouseMotionListener(this);
        mouseBeenDragging = false;
        startX = -1;
        startY = -1;
    }

    // generate the size of the window
    public Dimension getPreferredSize()
    {
        return new Dimension(xWindow, yWindow);
    }

    public void paint(Graphics gfx){

        //Updates each pixel on-screen, bottom to top, in horizontal layers

        /*for(int iY = gameWorld.PIXEL_MAP_HEIGHT-1; iY >= 0; iY--)
        {
            for(int iX = 0; iX < gameWorld.PIXEL_MAP_WIDTH; iX++)
            {
                drawPixel(gameWorld.pixelMap[iX][iY], gfx);
            }
        }*/
        for(Particle p : gameWorld.existingParticles)
        {
            drawPixel(p,gfx);
        }

        //Updates game variables
        gameWorld.update();
        Graphics2D g2     = (Graphics2D) getGraphics();
        g2.draw(m_circle);
    }



    //Draws pixel of the given type in the alloted pixelMap coordinate
    public void drawPixel(Particle thisParticle, Graphics gfx)
    {
        //Determines pixel color
        gfx.setColor(thisParticle.color);
        gfx.fillRect(thisParticle.x*4, thisParticle.y*4, 4, 4);
        gfx.setColor(Color.black);
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e)
    {
        if(gameWorld.penDragMode)
        {
            mouseBeenDragging = true;
            Point pos = e.getPoint();
            startX = pos.x;
            startY = pos.y;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if(mouseBeenDragging)
        {
            mouseBeenDragging = false;
            Point pos = e.getPoint();
            gameWorld.mouseDragged(startX/4, startY/4, pos.x/4, pos.y/4);



        }
        else if(!gameWorld.penDragMode)
        {
            Point pos = e.getPoint();
            gameWorld.mouseClicked(pos.x/4, pos.y/4);
            System.out.println("Clicked: " + pos.x/4 + " " + pos.y/4);
            System.out.println(gameWorld.pixelMap[pos.x/4][pos.y/4].name);
            //gameWorld.mouseWasClicked = false;


        }
    }

    public void mouseMoved(MouseEvent e)
    {
        Graphics2D g2     = (Graphics2D) getGraphics();
        Point      p      = e.getPoint();
        Shape      circle = new Ellipse2D.Double(p.getX() - RADIUS, p.getY() - RADIUS, DIAMETER, DIAMETER);

        clearCircle(g2);

        g2.setXORMode(XOR_COLOR);
        g2.draw(circle);
        g2.setPaintMode();

        m_circle = circle;
    }

    private void clearCircle(Graphics2D g2)
    {
        if (m_circle != null)
        {
            g2.setXORMode(XOR_COLOR);
            g2.draw(m_circle);
            g2.setPaintMode();

            m_circle = null;
        }
    }
    public void mouseDragged(MouseEvent e)
    {
        Graphics2D g2     = (Graphics2D) getGraphics();
        Point      p      = e.getPoint();
        Shape      circle = new Ellipse2D.Double(p.getX() - RADIUS, p.getY() - RADIUS, DIAMETER, DIAMETER);

        clearCircle(g2);

        g2.setXORMode(XOR_COLOR);
        g2.draw(circle);
        g2.setPaintMode();

        m_circle = circle;
        Point pixel = e.getPoint();
        //dragPointQueue.add(pixel);
        //System.out.print(pixel.getX());
        if(pixel.x/4 < xWindow && pixel.x/4 > 0 && pixel.y/4 < yWindow && pixel.y/4 > 0)
            gameWorld.mouseClicked(pixel.x/4,pixel.y/4);
    }


    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}

