package com.starcom.paint.tools;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import com.starcom.paint.PaintObject;
import com.starcom.paint.Frame;

public class FrameTool implements ITool
{
  static int line_thick = 5;
  static double opacity = 0.8;
  Pane pane;
  Rectangle r;

  void makeRect()
  {
    r = new Rectangle();
    r.setX(10);
    r.setY(10);
    r.setWidth(20);
    r.setHeight(20);
    r.setArcWidth(5);
    r.setArcHeight(5);
    r.setOpacity(opacity);
    r.setStrokeWidth(line_thick);
    r.setFill(null);
    r.setStroke(Frame.color);
    pane.getChildren().add(r);
  }
  
  void makeShape()
  {
    makeRect();
  }

  @Override
  public void init(Pane pane)
  {
    this.pane = pane;
  }

  @Override
  public void handle(EventType evType, MouseEvent event)
  {
    EventHandle:
    if (evType == EventType.DRAG)
    {
      if (r==null) { break EventHandle; }
      double posX = event.getX();
      double posY = event.getY();
      update(r, r.getX(),r.getY(),posX,posY);
    }
    else if (evType == EventType.CLICK)
    {
      makeShape();
      double posX = event.getX();
      double posY = event.getY();
      update(r, posX,posY,posX,posY);
    }
    else if (evType == EventType.RELEASE)
    {
      createPaintObject(r);
      r = null;
    }
  }

  private void createPaintObject(Rectangle r)
  {
    new PaintObject(r)
    {
      @Override
      public void moveGizmo(Node gizmo, double posX, double posY)
      {
        String s_giz = gizmo.getUserData().toString();
        Rectangle r = (Rectangle)getNodeList().get(0);
        if (s_giz.equals(GIZMO_START))
        {
          update(r, posX, posY, r.getX() + r.getWidth(), r.getY() + r.getHeight());
        }
        else if (s_giz.equals(GIZMO_END))
        {
          update(r, r.getX(), r.getY(), posX, posY);
        }
        if (s_giz.equals(GIZMO_CENTER))
        {
          double cx = r.getX() + (r.getWidth()/2.0);
          double cy = r.getY() + (r.getHeight()/2.0);
          double movX = posX -cx;
          double movY = posY -cy;
          double ex = r.getX() + r.getWidth() + movX;
          double ey = r.getY() + r.getHeight() + movY;
          update(r, r.getX() + movX, r.getY() + movY, ex, ey);
        }
      }
      
      @Override
      public void appendGizmos(ArrayList<Node> gizmoList)
      {
        gizmoList.add(PaintObject.createGizmoCircle(GIZMO_START));
        gizmoList.add(PaintObject.createGizmoCircle(GIZMO_END));
        gizmoList.add(PaintObject.createGizmoCircle(GIZMO_CENTER));
      }

      @Override
      public void updateGizmoPositions(ArrayList<Node> gizmoList)
      {
        Rectangle r = (Rectangle)getNodeList().get(0);
        double w = r.getWidth();
        double h = r.getHeight();
        Circle gizmo = (Circle)gizmoList.get(0);
        gizmo.setCenterX(r.getX());
        gizmo.setCenterY(r.getY());
        gizmo = (Circle)gizmoList.get(1);
        gizmo.setCenterX(r.getX() + w);
        gizmo.setCenterY(r.getY() + h);
        gizmo = (Circle)gizmoList.get(2);
        gizmo.setCenterX(r.getX() + (w/2.0));
        gizmo.setCenterY(r.getY() + (h/2.0));
      }
    };
  }

  private static void update(Rectangle r, double x1, double y1, double x2, double y2)
  {
    r.setX(x1);
    r.setY(y1);
    double w = x2-x1;
    if (w<20) { w = 20; }
    r.setWidth(w);
    double h = y2-y1;
    if (h<20) { h = 20; }
    r.setHeight(h);
  }

  @Override
  public void onSelected()
  {
  }

  @Override
  public void onDeselected()
  {
  }
}
