package edu.truman.leh;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Main program.
 * @author Hieu Le
 * @version December 5th, 2015
 */
public class Asteroids extends JComponent implements Runnable, KeyListener
{
   // Game thread and state flags
   private Thread gameThread;
   private boolean playing;
   private boolean started;
   private boolean winning;
   private boolean losing;
   
   // Game elements
   private Battleship ship;
   private List<Asteroid> asteroids;
   private List<Bullet> bullets;
   private int asteroidLeft;
   private int bulletLeft;
   
   // Ship control flags
   private boolean left;
   private boolean right;
   private boolean up;
   private boolean down;
   
   // Random number generator
   private Random rand;
   
   // Graphic components
   private Graphics2D g2d;
   private JFrame frame;
   private boolean showBounds;
   
   // Constants
   private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
   private static final int DELAY = 20;
   private static final int FRAME_WIDTH = 800;
   private static final int FRAME_HEIGHT = 700;
   private static final int DEFAULT_ASTEROIDS = 20;
   private static final int ROTATION_ANGLE = 10;
   private static final double SHIP_ACCELERATION = 0.1;
   private static final int EDGE_OFFSET = 10;
   private static final double BULLET_VELOCITY = 2.5;
   private static final String GAME_NAME = "SPACE BATTLE";
   
   
   /**
    * Constructs a SpaceBattle game.
    */
   public Asteroids()
   {
      playing = started = winning = false;
      left = right = up = down = false;
      showBounds = false;
      rand = new Random();
      frame = new JFrame(GAME_NAME);
      frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.add(this);
      frame.setVisible(true);
      addKeyListener(this);
      requestFocusInWindow();
   }
   
   /**
    * Initiate a new game.
    */
   public void initiate()
   {
      gameThread = new Thread(this);
      gameThread.start();
   }
   
   @Override
   public void run()
   {
      while (Thread.currentThread() == gameThread)
      {
         try
         {
            if (started)
               updateGame();
            Thread.sleep(DELAY);
         }
         catch(InterruptedException e)
         {
            e.printStackTrace();
         }
         repaint();
      }
   }
   
   private void updateGame()
   {
      updateShip();
      updateBullets();
      updateAsteroids();
      checkCollisions();
      
      // Check for termination condition
      if (winning || losing)
         return;
      
      if (!ship.isAlive() || asteroidLeft == 0)
      {
         playing = false;
         if (asteroidLeft == 0) 
            winning = true;
         else 
            losing = true;
         endGame();
      }
      else if (bulletLeft == 0)
      {
         playing = false;
         for (Bullet bullet : bullets)
         {
            if (bullet.isAlive())
               playing = true;
         }
         if (!playing)
         {
            losing = true;
            endGame();
         }
      }
   }
   
   private void startGame()
   {
      // Create game elements
      createNewGame();
      started = playing = true;
      winning = losing = false;
   }
   
   private void endGame()
   {
      // Clean up game elements
      ship.setAlive(false);
      for (Asteroid ast : asteroids)
         ast.setAlive(false);
      for (Bullet bullet : bullets)
         bullet.setAlive(false);
      
      asteroidLeft = bulletLeft = 0;
   }
   
   private void createNewGame()
   {
      createShip();
      createAsteroids();
      createBullets();
   }
   
   private void createShip()
   {
      // Create a ship in the middle of the map
      ship = new Battleship();
      ship.setAlive(true);
      ship.setX(FRAME_WIDTH / 2);
      ship.setY(FRAME_HEIGHT / 2);
   }
   
   private void createAsteroids()
   {
      if (asteroidLeft < 1)
         asteroidLeft = DEFAULT_ASTEROIDS;
      
      asteroids = new ArrayList<Asteroid>();
      for (int i = 0; i < asteroidLeft; i++)
      {
         Asteroid ast = new Asteroid();
         if (rand.nextBoolean())
         {
            if (rand.nextBoolean())
               ast.setX(0);
            else {
               ast.setX(FRAME_WIDTH);
            }
            ast.setY(rand.nextDouble() * FRAME_HEIGHT);
         }
         else
         {
            if (rand.nextBoolean())
            {
               ast.setY(0);
            }
            else
            {
               ast.setY(FRAME_HEIGHT);
            }
            ast.setX(rand.nextDouble() * FRAME_WIDTH);
         }
         ast.setMoveAng(rand.nextInt(360));
         double angle = ast.getMoveAngle() - 90;
         ast.setVelX(calcAngleMoveX(angle));
         ast.setVelY(calcAngleMoveY(angle));
         ast.setAlive(true);
         asteroids.add(ast);
      }
   }
   
   private void createBullets()
   {
      if (asteroidLeft < 5)
         bulletLeft = asteroidLeft * 3;
      else if (asteroidLeft < 20)
         bulletLeft = asteroidLeft * 2;
      else 
         bulletLeft = (int)(asteroidLeft * 1.5);
      
      
      bullets = new ArrayList<Bullet>(bulletLeft);
      for (int i = 0; i < bulletLeft; i++)
         bullets.add(new Bullet());
   }
   
   private void updateShip()
   {
      if (!ship.isAlive())
         return;
      
      if (left)
      {
         ship.incFaceAngle(-ROTATION_ANGLE);
         if (ship.getFaceAngle() < 0)
            ship.setFaceAngle(360 - ROTATION_ANGLE);
      }
      
      if (right)
      {
         ship.incFaceAngle(ROTATION_ANGLE);
         if (ship.getFaceAngle() > 360)
            ship.setFaceAngle(ROTATION_ANGLE);
      }
      
      if (up || down)
      {
         if (up)
            ship.setMoveAng(ship.getFaceAngle() - 90);
         else 
            ship.setMoveAng(ship.getFaceAngle() + 90);
         ship.incVelX(calcAngleMoveX(ship.getMoveAngle()) * SHIP_ACCELERATION);
         ship.incVelY(calcAngleMoveY(ship.getMoveAngle()) * SHIP_ACCELERATION);
      }
      else 
      {
         // Stop ship if no move key is pressed
         ship.setVelX(0);
         ship.setVelY(0);
      }
      
      // Update ship around position
      // Wrap around when reaching edge
      ship.incX(ship.getVelX());
      if (ship.getX() < -EDGE_OFFSET)
         ship.setX(getSize().width + EDGE_OFFSET);
      else if (ship.getX() > getSize().width + EDGE_OFFSET)
         ship.setX(-EDGE_OFFSET);
      
      ship.incY(ship.getVelY());
      if (ship.getY() < -EDGE_OFFSET)
         ship.setY(getSize().height + EDGE_OFFSET);
      else if (ship.getY() > getSize().height + EDGE_OFFSET)
         ship.setY(-EDGE_OFFSET);
   }
   
   private void updateBullets()
   {
      for (Bullet bullet : bullets)
      {
         if (bullet.isAlive())
         {
            // Update bullet position. Remove if reaching an edge
            bullet.incX(bullet.getVelX());
            if (bullet.getX() < 0 || bullet.getX() > getSize().width)
               bullet.setAlive(false);
            bullet.incY(bullet.getVelY());
            if (bullet.getY() < 0 || bullet.getY() > getSize().height)
               bullet.setAlive(false);
         }
      }
   }
   
   private void updateAsteroids()
   {
      for (Asteroid ast : asteroids)
      {
         if (ast.isAlive())
         {
            // Update asteroid position. Wrap around when reaching edge
            ast.incX(ast.getVelX());
            if (ast.getX() < -2 * EDGE_OFFSET)
               ast.setX(getSize().width + 2 * EDGE_OFFSET);
            else if (ast.getX() > getSize().width + 2 * EDGE_OFFSET)
               ast.setX(-2 * EDGE_OFFSET);
            
            ast.incY(ast.getVelY());
            if (ast.getY() < -2 * EDGE_OFFSET)
               ast.setY(getSize().height + 2 * EDGE_OFFSET);
            else if (ast.getY() > getSize().height + 2 * EDGE_OFFSET)
               ast.setY(-2 * EDGE_OFFSET);
         }
      }
   }
   
   private void checkCollisions()
   {
      // Check for collisions between asteroids and ship/bullet
      for (int i = 0; i < asteroids.size(); i++)
      {
         Asteroid ast = asteroids.get(i);
         if (ast.isAlive())
         {
            for (int j = 0; j < bullets.size(); j++)
            {
               Bullet bullet = bullets.get(j);
               if (ast.isAlive() && bullet.isAlive() &&
                     ast.getBounds().contains(bullet.getCenter()))
               {
                  ast.setAlive(false);
                  bullet.setAlive(false);
                  if (asteroidLeft > 0) asteroidLeft--;
               }
               
               if (ast.isAlive() && ship.isAlive()
                     && ast.getBounds().intersects(ship.getBounds()))
               {
                  ship.setAlive(false);
               }
            }
         }
      }
      
      // Checkk for collisions between ship and its own bullets
      for (int i = 0; i < bullets.size(); i++)
      {
         Bullet bullet = bullets.get(i);
         if (bullet.isAlive() && ship.isAlive() &&
               bullet.getBounds().contains(ship.getCenter()))
         {
            ship.setAlive(false);
         }
      }
   }
   
   @Override
   protected void paintComponent(Graphics g)
   {
      g2d = (Graphics2D) g;
      // Reset transform to identity
      g2d.setTransform(IDENTITY_TRANSFORM);
      // Erase background
      g2d.setPaint(Color.WHITE);
      g2d.fillRect(0, 0, getSize().width, getSize().height);
      // Display number of remaining asteroids and bullets
      g2d.setColor(Color.BLACK);
      g2d.drawString("Asteroids: " + asteroidLeft, 5, 10);
      g2d.drawString("Bullets: " + bulletLeft, 700, 10);
      
      // Display game status 
      if (!playing)
      {
         g2d.setColor(Color.RED);
         String message;
         if (winning || losing)
         {
            if (winning)
               message = "VICTORIOUS";
            else
               message = "GAME OVER";
             g2d.drawString(message, FRAME_WIDTH / 2 - 20, FRAME_HEIGHT / 2);
         }
         else
         {
            message = "WELCOME TO SPACE BATTLE";
            g2d.drawString(message, FRAME_WIDTH / 2 - 75, FRAME_HEIGHT / 2);
         }
         
         g2d.drawString("Press S to start", FRAME_WIDTH / 2 - 35, 
               FRAME_HEIGHT / 2 + 15);
         g2d.drawString("Use arrows to adjust number of asteroids", 
               FRAME_WIDTH / 2 - 100, FRAME_HEIGHT / 2 + 30);
      }
      
      // Draw the game elements
      if (started)
      {
         drawShip();
         drawBullets();
         drawAsteroids();
      }
   }
   
   private void drawShip()
   {
      if (ship.isAlive() || ship.isExploding())
      {
         g2d.setTransform(IDENTITY_TRANSFORM);
         g2d.translate(ship.getX(), ship.getY());
         g2d.rotate(Math.toRadians(ship.getFaceAngle()));
         g2d.setColor(Color.ORANGE);
         g2d.fill(ship.getShape());
         
         if (showBounds)
         {
            g2d.setTransform(IDENTITY_TRANSFORM);
            g2d.setColor(Color.BLUE);
            g2d.draw(ship.getBounds());
         }
      }
   }
   
   private void drawBullets()
   {
      for (Bullet bullet : bullets)
      {
         if (bullet.isAlive())
         {
            g2d.setTransform(IDENTITY_TRANSFORM);
            g2d.translate(bullet.getX(), bullet.getY());
            g2d.setColor(Color.RED);
            g2d.draw(bullet.getShape());
         }
      }
   }
   
   private void drawAsteroids()
   {
      for (Asteroid ast : asteroids)
      {
         if (ast.isAlive() || ast.isExploding())
         {
            g2d.setTransform(IDENTITY_TRANSFORM);
            g2d.translate(ast.getX(), ast.getY());
            g2d.rotate(Math.toRadians(ast.getMoveAngle()));
            g2d.setColor(Color.DARK_GRAY);
            g2d.fill(ast.getShape());
            
            if (showBounds)
            {
               g2d.setTransform(IDENTITY_TRANSFORM);
               g2d.setColor(Color.BLUE);
               g2d.draw(ast.getBounds());
            }
         }
      }
   }
   
   @Override
   public void keyTyped(KeyEvent e) {}
   
   @Override
   public void keyPressed(KeyEvent e)
   {
      // Button configuration when not playing
      if (!playing || !started)
      {
         switch (e.getKeyCode())
         {
            case KeyEvent.VK_S:
               startGame();
               break;
               
            case KeyEvent.VK_UP:
               if (asteroidLeft < 50)
                  asteroidLeft++;
               break;
               
            case KeyEvent.VK_DOWN:
               if (asteroidLeft > 0)
               {
                  asteroidLeft--;
               }
               break;
    
            default:
               break;
         }
         return;
      }
      
      // Command configuration when playing
      switch (e.getKeyCode())
      {
         case KeyEvent.VK_LEFT:
            left = true;
            break;
            
         case KeyEvent.VK_RIGHT:
            right = true;
            break;
            
         case KeyEvent.VK_UP:
            up = true;
            break;
            
         case KeyEvent.VK_DOWN:
            down = true;
            break;
            
         case KeyEvent.VK_CONTROL:
         case KeyEvent.VK_ENTER:
         case KeyEvent.VK_SPACE:
            if (bulletLeft > 0)
            {
               // Fire a bullet
               Bullet bullet = bullets.get(bullets.size() - bulletLeft);
               // Point bullet in the same direction ship is facing
               // Fire bullet at the same angle as ship
               bullet.setAlive(true);
               bullet.setX(ship.getX());
               bullet.setY(ship.getY());
               bullet.setMoveAng(ship.getFaceAngle() - 90);
               
               double angle = bullet.getMoveAngle();
               double svx = Math.abs(ship.getVelX()) + BULLET_VELOCITY;
               double svy = Math.abs(ship.getVelY()) + BULLET_VELOCITY;
               bullet.setVelX(svx * calcAngleMoveX(angle) * 2);
               bullet.setVelY(svy * calcAngleMoveY(angle) * 2);
               bulletLeft--;
            }
            break;
            
         case KeyEvent.VK_B:
            // Toggle bounding rectanges
            showBounds = !showBounds;
            break;

         default:
            break;
      }
   }
   
   @Override
   public void keyReleased(KeyEvent e)
   {
      
      switch (e.getKeyCode())
      {
         case KeyEvent.VK_LEFT:  
            left = false;
            break;
            
         case KeyEvent.VK_RIGHT:
            right = false;
            break;
            
         case KeyEvent.VK_UP:
            up = false;
            break;
            
         case KeyEvent.VK_DOWN:
            down = false;
            break;

         default:
            break;
      }
   }
   
   /**
    * Returns the horizontal component of the unit vector aligned
    * at the given angle.
    * @param angle the aligning angle, in degrees
    * @return the length of the horizontal component
    */
   private static double calcAngleMoveX(double angle)
   {
      return Math.cos(Math.toRadians(angle));
   }
   
   /**
    * Returns the vertical component of the unit vector aligned
    * at the given angle.
    * @param angle the aligning angle, in degrees
    * @return the length of the vertical component
    */
   private static double calcAngleMoveY(double angle)
   {
      return Math.sin(Math.toRadians(angle));
   }
   
   /**
    * The game entry
    * @param args
    */
   public static void main(String[] args)
   {
      Asteroids game = new Asteroids();
      game.initiate();
   }
}
