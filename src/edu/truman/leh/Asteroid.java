package edu.truman.leh;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Rectangle;

/**
 * A 2-dimensional asteroid having random jagged edges and capable of 
 * exploding.
 * @author Hieu Le
 * @version December 5th, 2015
 */
public class Asteroid extends BaseShape implements Explosive
{
   // The asteroid edges and vertices
   private int edges;
   private int[] x;
   private int[] y;
   private double scaleFactor;
   private double averageRadius;
   
   private static final int MIN_EDGES = 6;
   private static final int MAX_EDGES = 16;
   private static final int MIN_RADIUS = 20;
   private static final int MAX_RADIUS = 40;
   private static final double SIZE_DECREASE = 0.05;
   
   /**
    * Constructs an asteroid with random jagged edges.
    */
   public Asteroid()
   {
      edges = MIN_EDGES + (int)(Math.random() * (MAX_EDGES - MIN_EDGES));
      x = new int[edges];
      y = new int[edges];
      scaleFactor = 1.0;
      
      double theta;
      double radius;
      double sum = 0;
      for (int i = 0; i < edges; i++)
      {
         theta = 2 * Math.PI / edges * i;
         radius = MIN_RADIUS + (int)(Math.random() * (MAX_RADIUS - MIN_RADIUS));
         sum += radius;
         x[i] = (int) -Math.round(radius * Math.sin(theta));
         y[i] = (int) Math.round(radius * Math.cos(theta));
      }
      averageRadius = sum / edges;
   }
   
   @Override
   public Shape getShape()
   {
      if (isExploding()) scaleFactor -= SIZE_DECREASE;
      Polygon shape = new Polygon();
      for (int i = 0; i < edges; i++)
         shape.addPoint((int)(x[i] * scaleFactor), (int)(y[i] * scaleFactor));
      return shape;
   }
   
   @Override
   public Rectangle getBounds()
   {
      return new Rectangle((int)(getX() - averageRadius), 
            (int)(getY() - averageRadius), (int)(2 * averageRadius), 
            (int)(2 * averageRadius));
   }
   
   @Override
   public boolean isExploding()
   {
      return !isAlive() && scaleFactor > 0;
   }
}
