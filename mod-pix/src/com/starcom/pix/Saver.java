package com.starcom.pix;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import static com.starcom.debug.LoggingSystem.*;

public class Saver
{

  public static boolean save(Image bild, String ziel)
  {
    BufferedImage img = Converter.getBufferedImage(new ImageIcon(bild));
    return save(img, ziel);
  }
  public static boolean save(int bild[], int breit, String ziel)
  {
    String format = getFormatFromFileName(ziel);
    if (format==null)
    {
      warn(Saver.class, "No Pix Format: "+ziel);
      return false;
    }
    BufferedImage neuBild = Converter.arrayToBufferedImage(bild,breit);
    return save(neuBild, ziel);
  }

  public static boolean save(BufferedImage bild, String ziel)
  {
    String format = getFormatFromFileName(ziel);
    if (format==null)
    {
      warn(Saver.class, "No Pix Format: "+ziel);
      return false;
    }
    boolean ret = false;
    try
    {
      ret = javax.imageio.ImageIO.write(bild,format,new File(ziel));
    }
    catch (Exception e)
    {
      severe(Saver.class, "Error beim Speichern von "+ziel+": "+e);
      return false;
    }
    return ret;
  }

  static String getFormatFromFileName(String ziel)
  {
    if (com.starcom.Stg.endsWithIgnoreCase(ziel, ".jpg")) { return "JPEG"; }
    else if (com.starcom.Stg.endsWithIgnoreCase(ziel, ".jpeg")) { return "JPEG"; }
    else if (com.starcom.Stg.endsWithIgnoreCase(ziel, ".png")) { return "PNG"; }
    else if (com.starcom.Stg.endsWithIgnoreCase(ziel, ".bmp")) { return "BMP"; }
    else if (com.starcom.Stg.endsWithIgnoreCase(ziel, ".tiff")) { return "TIFF"; }
    else if (com.starcom.Stg.endsWithIgnoreCase(ziel, ".gif")) { return "GIF"; }
    else if (com.starcom.Stg.endsWithIgnoreCase(ziel, ".wbmp")) { return "WBMP"; } // wbmp?
    else { return null; }
  }


}