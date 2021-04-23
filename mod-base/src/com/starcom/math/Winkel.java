package com.starcom.math;


// Winkelberechnung!

/**
                                 ----|
                           ------    |
                c    ------          |
               ------                |a
         ------                      |
   ------ alpha                    . |
  -----------------------------------
                 b
**/
public class Winkel
{
  public static double bogenX(double radius, double bogenY)
  {
    if (bogenY>radius) {return 0.0f;}
    double wert = (radius*radius)-(bogenY*bogenY);
    return java.lang.Math.sqrt(wert);
  }
  public static double getA(double b, double c) {return java.lang.Math.sqrt((c*c)-(b*b));}
  public static double getB(double a, double c) {return java.lang.Math.sqrt((c*c)-(a*a));}
  public static double getC(double a, double b) {return java.lang.Math.sqrt((a*a)+(b*b));}
  public static double getAFromAlphaAndC (double alpha, double c)
  {
    double sin = java.lang.Math.cos((alpha*java.lang.Math.PI)/180.0f);
    return c*sin;
  }
  public static double getAlpha(double a, double b)
  {
    double wert = a/b;//(a*a)+(b*b);
    wert = java.lang.Math.toDegrees(java.lang.Math.atan(wert));
    return wert;
  }
  public static double getAlphaRad(double a, double b)
  {
    double wert = a/b;//(a*a)+(b*b);
    wert = java.lang.Math.atan(wert);
    return wert;
  }



}