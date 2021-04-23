package com.starcom.paint.tools;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import com.starcom.math.Point2i;
import com.starcom.paint.Frame;
import com.starcom.paint.PaintObject;

public class SizeTool extends CropToolAbstract
{
  WritableImage image;
  Point2i newSize = new Point2i();
  Popup win;
  
  @Override
  void doActivateGizmo(PaintObject headlessPaintObject)
  {
    showSelectFrame(pane);
  }

  private void showSelectFrame(Node sourceN)
  {
    if (win != null && win.isShowing()) { win.hide(); }
    win = new Popup();
    win.setAutoHide(true);
    TextField textX = new TextField();
    TextField textY = new TextField();
    textX.setText("" + ((int)pane.getWidth()));
    textY.setText("" + ((int)pane.getHeight()));
    CheckBox cbox = new CheckBox("Scale");
    Button okButton = new Button("OK");
    VBox box = new VBox();
    box.getChildren().add(textX);
    box.getChildren().add(textY);
    box.getChildren().add(cbox);
    box.getChildren().add(okButton);
    okButton.setOnAction((ev) -> setCanvasSize(textX.getText(), textY.getText(), cbox.isSelected(), win));
    win.getContent().add(box);
    Point pos = MouseInfo.getPointerInfo().getLocation();
    win.show(sourceN, pos.x, pos.y);
  }

  private void setCanvasSize(String textX, String textY, boolean b_scale, Popup win)
  {
    win.hide();
    newSize.x = Integer.parseInt(textX);
    newSize.y = Integer.parseInt(textY);
    image = new WritableImage((int)pane.getWidth(), (int)pane.getHeight());
    pane.snapshot(null, image);
    if (b_scale)
    {
      Frame.openPix(pane, getScaledInstance(image, newSize.x, newSize.y, false));
    }
    else
    {
      if (newSize.x < pane.getWidth()) { newSize.x = (int)pane.getWidth(); }
      if (newSize.y < pane.getHeight()) { newSize.y = (int)pane.getHeight(); }
      Frame.openEmptyPix(pane, newSize.x, newSize.y);
      headlessPaintObject.setGizmoActive(pane, true);
    }
  }
  
  public static Image getScaledInstance(Image source, int targetWidth, int targetHeight, boolean preserveRatio)
  {
    ImageView imageView = new ImageView(source);
    imageView.setPreserveRatio(preserveRatio);
    imageView.setFitWidth(targetWidth);
    if (!preserveRatio)
    {
      imageView.setFitHeight(targetHeight);
    }
    return imageView.snapshot(null, null);
}


  @Override
  void doUpdateGizmoInitPositions(ArrayList<Node> gizmoList)
  {
    Rectangle r = (Rectangle)gizmoList.get(POS_BORDER_T);
    updateRect(r, 0.0, 0.0, newSize.x, 0.0);
    r = (Rectangle)gizmoList.get(POS_BORDER_B);
    updateRect(r, 0.0, image.getHeight(), newSize.x, newSize.y - image.getHeight());
    r = (Rectangle)gizmoList.get(POS_BORDER_L);
    updateRect(r, 0.0, 0.0, 0.0, image.getHeight());
    r = (Rectangle)gizmoList.get(POS_BORDER_R);
    updateRect(r, image.getWidth(), 0.0, newSize.x - image.getWidth(), image.getHeight());
    
    Circle c = (Circle)gizmoList.get(POS_GIZMO_C);
    c.setCenterX(image.getWidth() * 0.5);
    c.setCenterY(image.getHeight() * 0.5);
  }

  @Override
  void updateFinish(double x, double y, double w, double h)
  {
    ImageView iv = new ImageView(image);
    iv.setX(x);
    iv.setY(y);
    pane.getChildren().add(iv);
    headlessPaintObject.setGizmoActive(pane, false);
  }


  @Override
  void moveGizmos(ArrayList<Node> tmpGizmoList2, double x, double y, double w, double h)
  {
    moveCircle(((Circle)tmpGizmoList.get(POS_GIZMO_C)), x + (w * 0.5), y + (h * 0.5));
  }
  
  @Override
  void appendGizmoCircles(ArrayList<Node> gizmoList)
  {
    gizmoList.add(PaintObject.createGizmoCircle(GIZMO_C));
  }

  @Override
  public void onSelected()
  {
    showSelectFrame(pane);
  }

  @Override
  public void onDeselected()
  {
  }
  
}
