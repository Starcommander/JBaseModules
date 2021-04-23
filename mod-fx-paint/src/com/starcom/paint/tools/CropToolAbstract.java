package com.starcom.paint.tools;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import com.starcom.paint.PaintObject;

public abstract class CropToolAbstract implements ITool
{
  public static final String GIZMO_NW = "Gizmo_nw";
  public static final String GIZMO_C = "Gizmo_c";
  public static final String GIZMO_SO = "Gizmo_so";
  public static final int POS_BORDER_T = 0;
  public static final int POS_BORDER_B = 1;
  public static final int POS_BORDER_L = 2;
  public static final int POS_BORDER_R = 3;
  public static final int POS_GIZMO_C = 4;
  public static final int POS_GIZMO_NW = 5;
  public static final int POS_GIZMO_SO = 6;
  static double opacity = 0.2;
  Pane pane;
  Node selectedGizmo;
  PaintObject headlessPaintObject;
  ArrayList<Node> tmpGizmoList = new ArrayList<Node>();

  static Rectangle makeRect(String text)
  {
    Rectangle r = new Rectangle();
    r.setOpacity(opacity);
    r.setFill(Color.RED);
    r.setUserData(text);
    return r;
  }
  
  static void updateRect(Rectangle r, double x, double y, double w, double h)
  {
    r.setX(x);
    r.setY(y);
    r.setWidth(w);
    r.setHeight(h);
  }
  
  @Override
  public void init(Pane pane)
  {
    this.pane = pane;
    initHeadlessPaintObject();
  }

  @Override
  public void handle(EventType evType, MouseEvent event)
  {
    EventHandle:
    if (evType == EventType.DRAG)
    {
      if (selectedGizmo==null) { break EventHandle; }
      double posX = event.getX();
      double posY = event.getY();
      update(posX, posY);
    }
    else if (evType == EventType.CLICK)
    {
      if (headlessPaintObject.isGizmoActive())
      {
        headlessPaintObject.appendGizmoList(tmpGizmoList);
        double posX = event.getX();
        double posY = event.getY();
        selectedGizmo = null;
        for (Node curGizmo : tmpGizmoList)
        {
          if ((curGizmo instanceof Circle) && (curGizmo.intersects(posX, posY, 1, 1)))
          {
            System.out.println("Gizmo selected: " + curGizmo.getUserData());
            selectedGizmo = curGizmo;
            break;
          }
        }
        if (selectedGizmo==null)
        {
          updateFinish();
        }
        tmpGizmoList.clear();
      }
      else
      {
        doActivateGizmo(headlessPaintObject);
      }
    }
  }

  abstract void appendGizmoCircles(ArrayList<Node> gizmoList);
  
  abstract void doActivateGizmo(PaintObject headlessPaintObject);
  
  abstract void moveGizmos(ArrayList<Node> tmpGizmoList2, double x, double y, double w, double h);
  
  abstract void doUpdateGizmoInitPositions(ArrayList<Node> gizmoList);
  
  abstract void updateFinish(double x, double y, double w, double h);
  
  private void updateFinish()
  {
    headlessPaintObject.appendGizmoList(tmpGizmoList);
    double x = ((Rectangle)tmpGizmoList.get(POS_BORDER_L)).getWidth();
    double y = ((Rectangle)tmpGizmoList.get(POS_BORDER_T)).getHeight();
    double w = ((Rectangle)tmpGizmoList.get(POS_BORDER_R)).getX() - x;
    double h = ((Rectangle)tmpGizmoList.get(POS_BORDER_B)).getY() - y;
    tmpGizmoList.clear();
    updateFinish(x,y,w,h);
  }

  private void update(double posX, double posY)
  {
    headlessPaintObject.appendGizmoList(tmpGizmoList);
    double x = ((Rectangle)tmpGizmoList.get(POS_BORDER_L)).getWidth();
    double y = ((Rectangle)tmpGizmoList.get(POS_BORDER_T)).getHeight();
    double w = ((Rectangle)tmpGizmoList.get(POS_BORDER_R)).getX() - x;
    double h = ((Rectangle)tmpGizmoList.get(POS_BORDER_B)).getY() - y;
    Circle c = (Circle) selectedGizmo;
    double movX = posX - c.getCenterX();
    double movY = posY - c.getCenterY();
    double oldX = x;
    double oldY = y;
    if (selectedGizmo.getUserData().equals(GIZMO_C))
    {
      x = x + movX;
      y = y + movY;
      if (x < 0) { x = 0.0; }
      else if (x > (pane.getWidth()-w)) { x = pane.getWidth()-w; }
      if (y < 0) { y = 0.0; }
      else if (y > (pane.getHeight()-h)) { y = pane.getHeight()-h; }
    }
    else if (selectedGizmo.getUserData().equals(GIZMO_NW))
    {
      x = x + movX;
      y = y + movY;
      if (x < 0) { x = 0.0; }
      else if (x > oldX + w) { x = oldX + w; }
      else { w = w - movX; }
      if (y < 0) { y = 0.0; }
      else if (y > oldY + h) { y = oldY + h; }
      else { h = h - movY; }
    }
    else if (selectedGizmo.getUserData().equals(GIZMO_SO))
    {
      w = w + movX;
      h = h + movY;
      if (w < 0) { w = 0.0; }
      else if (w > (pane.getWidth()-x)) { w = pane.getWidth()-x; }
      if (h < 0) { h = 0.0; }
      else if (h > (pane.getHeight()-y)) { h = pane.getHeight()-y; }
    }
    updateRect(((Rectangle)tmpGizmoList.get(POS_BORDER_T)), 0.0, 0.0, pane.getWidth(), y);
    updateRect(((Rectangle)tmpGizmoList.get(POS_BORDER_B)), 0.0, y + h, pane.getWidth(), pane.getHeight() - (y + h));
    updateRect(((Rectangle)tmpGizmoList.get(POS_BORDER_L)), 0.0, y, x, h);
    updateRect(((Rectangle)tmpGizmoList.get(POS_BORDER_R)), x + w, y, pane.getWidth() - (x + w), h);
    moveGizmos(tmpGizmoList, x, y, w, h);
    tmpGizmoList.clear();
  }

  public static void moveCircle(Circle circle, double posX, double posY)
  {
    circle.setCenterX(posX);
    circle.setCenterY(posY);
  }

  /** Create a dummy object for activating panel-crop-frames **/
  private void initHeadlessPaintObject()
  {
    headlessPaintObject = new PaintObject()
    {
      @Override
      public void moveGizmo(Node gizmo, double posX, double posY)
      {
        String s_giz = gizmo.getUserData().toString();
        System.out.println("Moving Gizmo: " + s_giz);
      }
      
      @Override
      public void appendGizmos(ArrayList<Node> gizmoList)
      {
        gizmoList.add(makeRect("Top"));
        gizmoList.add(makeRect("Bottom"));
        gizmoList.add(makeRect("Left"));
        gizmoList.add(makeRect("Right"));
        appendGizmoCircles(gizmoList);
      }


      @Override
      public void updateGizmoPositions(ArrayList<Node> gizmoList)
      {
        doUpdateGizmoInitPositions(gizmoList);
      }

    };
  }
}
