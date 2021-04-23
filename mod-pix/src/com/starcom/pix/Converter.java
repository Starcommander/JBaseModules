package com.starcom.pix;

/**
* Um ein einfaches Bild zu konvertieren!
*/


import java.awt.*;
import java.awt.image.*;
import javax.swing.*;


public class Converter
{
  public static BufferedImage arrayToBufferedImage(int pix[], int breit)
  {
    int hoch = pix.length/breit;
    BufferedImage neuBild = new BufferedImage(breit, hoch, BufferedImage.TYPE_INT_ARGB);
    for (int i=0; i<hoch; i++)
    {
      for (int j=0; j<breit; j++)
      {
        neuBild.setRGB(j,i,pix[j+(breit*i)]);
      }
    }
    return neuBild;
  }

  public static BufferedImage getBufferedImage(Icon icon)
  {
    int w = icon.getIconWidth();
    int h = icon.getIconHeight();
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    BufferedImage image = gc.createCompatibleImage(w, h);
    Graphics2D g = image.createGraphics();
    icon.paintIcon(null, g, 0, 0);
    g.dispose();
    return image;
  }

}
