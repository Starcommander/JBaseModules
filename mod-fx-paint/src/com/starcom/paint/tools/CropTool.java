package com.starcom.paint.tools;

import java.util.ArrayList;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import com.starcom.paint.Frame;
import com.starcom.paint.PaintObject;

public class CropTool extends CropToolAbstract
{
  @Override
  void doActivateGizmo(PaintObject headlessPaintObject)
  {
    headlessPaintObject.setGizmoActive(pane, true);
  }

  @Override
  void doUpdateGizmoInitPositions(ArrayList<Node> gizmoList)
  {
    double width4 = pane.getWidth() * 0.25;
    double height4 = pane.getHeight() * 0.25;
    
    Rectangle r = (Rectangle)gizmoList.get(POS_BORDER_T);
    updateRect(r, 0.0, 0.0, pane.getWidth(), height4);
    r = (Rectangle)gizmoList.get(POS_BORDER_B);
    updateRect(r, 0.0, height4 * 3.0, pane.getWidth(), height4);
    r = (Rectangle)gizmoList.get(POS_BORDER_L);
    updateRect(r, 0.0, height4, width4, height4 * 2.0);
    r = (Rectangle)gizmoList.get(POS_BORDER_R);
    updateRect(r, width4 * 3.0, height4, width4, height4 * 2.0);
    
    Circle c = (Circle)gizmoList.get(POS_GIZMO_NW);
    c.setCenterX(width4);
    c.setCenterY(height4);
    c = (Circle)gizmoList.get(POS_GIZMO_C);
    c.setCenterX(pane.getWidth() * 0.5);
    c.setCenterY(pane.getHeight() * 0.5);
    c = (Circle)gizmoList.get(POS_GIZMO_SO);
    c.setCenterX(width4 * 3.0);
    c.setCenterY(height4 * 3.0);
  }

  @Override
  void updateFinish(double x, double y, double w, double h)
  {
    Node curView = pane;
    if (Frame.fullShot!=null)
    {
      double ratioX = Frame.fullShot.getWidth() / pane.getWidth();
      double ratioY = Frame.fullShot.getHeight() / pane.getHeight();
      x = x * ratioX;
      y = y * ratioY;
      w = w * ratioX;
      h = h * ratioY;
      curView = new ImageView(Frame.fullShot);
      Frame.fullShot = null;
    }
    headlessPaintObject.setGizmoActive(pane, false);
    SnapshotParameters p = new SnapshotParameters();
    p.setViewport(new Rectangle2D(x + curView.getLayoutX(), y + curView.getLayoutY(), w, h));
    WritableImage contentPix = new WritableImage((int)w, (int)h);
    curView.snapshot(p, contentPix);
    Frame.openPix(pane, contentPix);
  }

  @Override
  void moveGizmos(ArrayList<Node> tmpGizmoList2, double x, double y, double w, double h)
  {
    moveCircle(((Circle)tmpGizmoList.get(POS_GIZMO_NW)), x, y);
    moveCircle(((Circle)tmpGizmoList.get(POS_GIZMO_C)), x + (w * 0.5), y + (h * 0.5));
    moveCircle(((Circle)tmpGizmoList.get(POS_GIZMO_SO)), x + w, y + h);
  }

  @Override
  void appendGizmoCircles(ArrayList<Node> gizmoList)
  {
    gizmoList.add(PaintObject.createGizmoCircle(GIZMO_C));
    gizmoList.add(PaintObject.createGizmoCircle(GIZMO_NW));
    gizmoList.add(PaintObject.createGizmoCircle(GIZMO_SO));
  }

  @Override
  public void onSelected()
  {
    headlessPaintObject.setGizmoActive(pane, true);
  }

  @Override
  public void onDeselected()
  {
  }
  
}
