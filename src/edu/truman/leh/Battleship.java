package edu.truman.leh;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Rectangle;

/**
 * A 2-dimensional battleship capable of exploding.
 * @author Hieu Le
 * @version December 4th, 2015
 */
public class Battleship extends BaseShape implements Explosive
{
   // The coordinates of the vertices
   private int[] x;
   private int[] y;
   // The scale factor
   private double scaleFactor;
   
   private static final double SIZE_DECREASE = 0.05;
   private static final int WIDTH = 6;
   private static final int HEIGHT = 6;
   
   /**
    * Constructs a battleship with predetermined shape.
    */
   public Battleship()
   {
      x = new int[] {0, 4, 4, 10, 2, 0, -2, -10, -4, -4};
      y = new int[] {-20, -14, 0, 6, 6, 4, 6, 6, 0, -14};
      scaleFactor = 1.0;
   }
   
   @Override
   public Shape getShape()
   {
      // Decrease the size of the exploding ship
      if (isExploding()) 
         scaleFactor -= SIZE_DECREASE;
      Polygon shape = new Polygon();
      for (int i = 0; i < x.length; i++)
         shape.addPoint((int)(x[i] * scaleFactor), (int)(y[i] * scaleFactor));
      return shape;
   }
   
   @Override
   public Rectangle getBounds()
   {
      return new Rectangle((int)getX() - WIDTH, 
            (int)getY() - HEIGHT, 2 * WIDTH, 2 * HEIGHT);
   }
   
   @Override
   public boolean isExploding()
   {
      return !isAlive() && scaleFactor > 0;
   }
}
