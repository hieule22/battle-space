package edu.truman.leh;

import java.awt.Point;
import java.awt.Shape;
import java.awt.Rectangle;

/**
 * A shape representable in a vector field.
 * @author Hieu Le
 * @version December 5th, 2015
 */
public abstract class BaseShape
{
   // The instance variables
   private boolean alive;
   private double x;
   private double y;
   private double velX;
   private double velY;
   private double moveAngle;
   private double faceAngle;
   
   /**
    * Constructs a BaseShape object.
    */
   public BaseShape()
   {
      alive = false;
      x = y = 0.0;
      velX = velY = 0.0;
      moveAngle = faceAngle = 0.0;
   }
   
   public boolean isAlive()
   {
      return alive;
   }
   
   public double getX()
   {
      return x;
   }
   
   public double getY()
   {
      return y;
   }
   
   public Point getCenter()
   {
      return new Point((int)x, (int)y);
   }
   
   public double getVelX()
   {
      return velX;
   }
   
   public double getVelY()
   {
      return velY;
   }
   
   public double getMoveAngle()
   {
      return moveAngle;
   }
   
   public double getFaceAngle()
   {
      return faceAngle;
   }
   
   public void setAlive(boolean alive)
   {
      this.alive = alive;
   }
   
   public void setX(double x) 
   {
      this.x = x;
   }
   
   public void setY(double y)
   {
      this.y = y;
   }
   
   public void incX(double dx)
   {
      x += dx;
   }
   
   public void incY(double dy)
   {
      y += dy;
   }
   
   public void setVelX(double velX)
   {
      this.velX = velX;
   }
   
   public void setVelY(double velY)
   {
      this.velY = velY;
   }
   
   public void incVelX(double dvx)
   {
      velX += dvx;
   }
   
   public void incVelY(double dvy)
   {
      velY += dvy;
   }
   
   public void setMoveAng(double moveAngle)
   {
      this.moveAngle = moveAngle;
   }
   
   public void setFaceAngle(double faceAngle)
   {
      this.faceAngle = faceAngle;
   }
   
   public void incFaceAngle(double delta)
   {
      faceAngle += delta;
   }
   
   /**
    * Returns the shape of this instance.
    * @return a Shape object.
    */
   public abstract Shape getShape();
   
   /**
    * Returns the rectangular bound of this instance.
    * @return a Rectangle object.
    */
   public abstract Rectangle getBounds();
}
