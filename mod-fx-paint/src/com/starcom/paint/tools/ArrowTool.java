package com.starcom.paint.tools;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import com.starcom.paint.AbstractPaintObject;
import com.starcom.paint.Frame;
import com.starcom.math.Winkel;

public class ArrowTool implements ITool
{
  static int tail_len = 30;
  static int line_thick = 10;
  static double opacity = 0.8;
  Pane pane;
  Line line;
  Polygon polygon;
  
  void makeShape()
  {
    line = new Line();
    line.setStroke(Frame.color);
    line.setStrokeWidth(line_thick);
    line.setStrokeLineCap(StrokeLineCap.BUTT);
    line.setOpacity(opacity);
    pane.getChildren().add(line);
    
    polygon = new Polygon();
    polygon.getPoints().addAll(new Double[]{
        0.0, 0.0,
        0.0 - line_thick, 0.0 - tail_len,
        0.0 + line_thick, 0.0 - tail_len });
    polygon.setStroke(Frame.color.darker());
    polygon.setFill(Frame.color);
    polygon.setOpacity(opacity);
    pane.getChildren().add(polygon);
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
      if (line==null) { break EventHandle; }
      double posX = event.getX();
      double posY = event.getY();
      update(line, polygon, line.getStartX(),line.getStartY(),posX,posY);
    }
    else if (evType == EventType.CLICK)
    {
      makeShape();
      double posX = event.getX();
      double posY = event.getY();
      update(line, polygon, posX,posY,posX,posY);
    }
    else if (evType == EventType.RELEASE)
    {
      createPaintObject(line, polygon);
      line = null;
      polygon = null;
    }
  }

  private void createPaintObject(Line line2, Polygon polygon2)
  {
    new AbstractPaintObject(line, polygon)
    {
      @Override
      public void moveGizmo(Node gizmo, double posX, double posY)
      {
        String s_giz = gizmo.getUserData().toString();
        Line line = (Line)getNodeList().get(0);
        Polygon polygon = (Polygon)getNodeList().get(1);
        if (s_giz.equals(GIZMO_START))
        {
          update(line, polygon, posX, posY, polygon.getTranslateX(), polygon.getTranslateY());
        }
        else if (s_giz.equals(GIZMO_END))
        {
          update(line, polygon, line.getStartX(), line.getStartY(), posX, posY);
        }
        if (s_giz.equals(GIZMO_CENTER))
        {
          double cx = line.getStartX() - line.getEndX();
          double cy = line.getStartY() - line.getEndY();
          cx = line.getEndX() + (cx/2.0);
          cy = line.getEndY() + (cy/2.0);
          double movX = posX -cx;
          double movY = posY -cy;
          double ex = polygon.getTranslateX() + movX;
          double ey = polygon.getTranslateY() + movY;
          update(line, polygon, line.getStartX() + movX, line.getStartY() + movY, ex, ey);
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

  private static void update(Line line, Polygon polygon, double x1, double y1, double x2, double y2)
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
    double alpha = Winkel.getAlpha(b, a) + 90.0;
    if (a>0) { alpha += 180.0; }
    polygon.getTransforms().clear();
    polygon.getTransforms().add(new Rotate(alpha, 0.0, 0.0, 0.0, Rotate.Z_AXIS));
    polygon.setTranslateX(line.getEndX());
    polygon.setTranslateY(line.getEndY());

    /* Line reduce end. */
    double c = Winkel.getC(a, b);
    if (c<1) { c=1; }
    double mult = tail_len/c;
    a = a * mult;
    b = b * mult;
    line.setEndX(line.getEndX() - a);
    line.setEndY(line.getEndY() - b);
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
