package com.starcom.paint;

import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class Frame
{
  public static Color color = Color.RED;
  public static Image fullShot;

  public static void openPix(Pane pane, Image pix)
  {
    pane.getChildren().clear();
    pane.setMaxSize(pix.getWidth(), pix.getHeight());
    pane.getChildren().add(createImageView(pix));
  }

  public static void openEmptyPix(Pane pane, int sizeX, int sizeY)
  {
    PaintObject.clearAllObjects(pane);
    WritableImage contentPix = new WritableImage(sizeX, sizeY);
    Frame.openPix(pane, contentPix);
  }
  
  public static ImageView createImageView(Image image)
  {
    ImageView iv = new ImageView();
    iv.setImage(image);
    return iv;
  }
}
