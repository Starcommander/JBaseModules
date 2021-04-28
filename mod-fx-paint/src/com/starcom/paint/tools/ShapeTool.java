package com.starcom.paint.tools;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import com.starcom.paint.Frame;
import com.starcom.paint.AbstractPaintObject;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class ShapeTool implements ITool
{
  static int tail_len = 30;
  static int line_thick = 10;
  static double opacity = 0.8;
  Pane pane;
  Line line;
  Circle circle;
  
  void makeShape()
  {
    line = new Line();
    line.setStroke(Frame.color);
    line.setStrokeWidth(line_thick);
    line.setStrokeLineCap(StrokeLineCap.BUTT);
    line.setOpacity(opacity);
    pane.getChildren().add(line);
    circle = new Circle(10);
    circle.setStroke(Frame.color.darker());
    circle.setFill(Frame.color);
    circle.setOpacity(opacity);
    pane.getChildren().add(circle);
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
    if (evType == EventType.MOVE || evType == EventType.DRAG)
    {
      if (line==null) { break EventHandle; }
      System.out.println("MM=Move!");
      double posX = event.getX();
      double posY = event.getY();
      update(line, circle, line.getStartX(),line.getStartY(),posX,posY);
    }
    else if (evType == EventType.CLICK)
    {
      System.out.println("MM=Click!");
      if (line!=null && circle!=null)
      {
        System.out.println("MM=Click.paint!");
        createPaintObject(line, circle);
        line = null;
        circle = null;
      }
      makeShape();
      double posX = event.getX();
      double posY = event.getY();
      update(line, circle, posX,posY,posX,posY);
    }
  }

  private void createPaintObject(Line line2, Circle circle)
  {
    new AbstractPaintObject(line, circle)
    {
      @Override
      public void moveGizmo(Node gizmo, double posX, double posY)
      {
        String s_giz = gizmo.getUserData().toString();
        Line line = (Line)getNodeList().get(0);
        Circle circle = (Circle)getNodeList().get(1);
        if (s_giz.equals(GIZMO_START))
        {
          update(line, circle, posX, posY, circle.getTranslateX(), circle.getTranslateY());
        }
        else if (s_giz.equals(GIZMO_END))
        {
          update(line, circle, line.getStartX(), line.getStartY(), posX, posY);
        }
        if (s_giz.equals(GIZMO_CENTER))
        {
          double cx = line.getStartX() - line.getEndX();
          double cy = line.getStartY() - line.getEndY();
          cx = line.getEndX() + (cx/2.0);
          cy = line.getEndY() + (cy/2.0);
          double movX = posX -cx;
          double movY = posY -cy;
          double ex = circle.getTranslateX() + movX;
          double ey = circle.getTranslateY() + movY;
          update(line, circle, line.getStartX() + movX, line.getStartY() + movY, ex, ey);
        }
      }
      
      @Override
      public void appendGizmos(ArrayList<Node> gizmoList)
      {
        gizmoList.add(AbstractPaintObject.createGizmoCircle(GIZMO_START));
        gizmoList.add(AbstractPaintObject.createGizmoCircle(GIZMO_END));
        gizmoList.add(AbstractPaintObject.createGizmoCircle(GIZMO_CENTER));
      }

      @Override
      public void updateGizmoPositions(ArrayList<Node> gizmoList)
      {
        Line l = (Line)getNodeList().get(0);
        double ex = l.getEndX();
        double ey = l.getEndY();
        double sx = l.getStartX();
        double sy = l.getStartY();
        double lx = (ex -sx) / 2.0;
        double ly = (ey -sy) / 2.0;
        Circle gizmo = (Circle)gizmoList.get(0);
        gizmo.setCenterX(sx);
        gizmo.setCenterY(sy);
        gizmo = (Circle)gizmoList.get(1);
        gizmo.setCenterX(ex);
        gizmo.setCenterY(ey);
        gizmo = (Circle)gizmoList.get(2);
        gizmo.setCenterX(sx + lx);
        gizmo.setCenterY(sy + ly);
      }

      @Override
      public String saveObj()
      {
        // TODO Class instead of anonym class, and complete this function.
        return null;
      }

      @Override
      public void loadObj(String data)
      {
        // TODO Class instead of anonym class, and complete this function.
      }
    };
  }

  private static void update(Line line, Circle circle, double x1, double y1, double x2, double y2)
  {
    /* Line start end. */
    line.setStartX(x1);
    line.setStartY(y1);
    line.setEndX(x2);
    line.setEndY(y2);
    
    /* Shape rotate move. */
    double a = line.getEndX() - line.getStartX();
    double b = line.getEndY() - line.getStartY();
    if (a==0) { a = 1; }
    if (b==0) { b = 1; }
    circle.setTranslateX(line.getEndX());
    circle.setTranslateY(line.getEndY());
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
