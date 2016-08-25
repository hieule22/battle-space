package edu.truman.leh;

import java.awt.Shape;
import java.awt.Rectangle;

/**
 * A two-dimensional bullet.
 * @author Hieu Le
 * @version December 6th, 2015
 */
public class Bullet extends BaseShape
{
   private Rectangle shape;
   
   private static final int WIDTH = 2;
   private static final int HEIGHT = 2;
   
   /**
    * Constructs a bullet with a predefined shape.
    */
   public Bullet()
   {
      shape = new Rectangle(0, 0, WIDTH, HEIGHT);
   }
   
   @Override
   public Shape getShape()
   {
      return shape;
   }
   
   @Override
   public Rectangle getBounds()
   {
      return new Rectangle((int)getX(), (int)getY(), WIDTH, HEIGHT);
   }
}
