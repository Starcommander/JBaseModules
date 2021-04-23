package com.starcom.paint.tools;

import java.util.ArrayList;
import java.util.Optional;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.starcom.paint.PaintObject;
import com.starcom.paint.Frame;

public class TxtTool implements ITool
{
  static int line_thick = 4;
  static double opacity = 0.8;
  Pane pane;
  Rectangle r;
  Text t;
  String s_txt;
  Node curGizmo;
  PaintObject curObj;
  
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
    r.setFill(Frame.color.brighter());
    r.setStroke(Frame.color.darker());
    pane.getChildren().add(r);
  }
  
  static String askText(String preText, Pane pane)
  {
    if (preText == null) { preText = "This is a default text"; }
    TextInputDialog dialog = new TextInputDialog(preText);
    dialog.setTitle("Text");
    dialog.setHeaderText("Enter some Text!");

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent())
    {
      if (pane != null)
      {
        pane.setCursor(Cursor.CROSSHAIR);
      }
      return result.get();
    }
    return null;
  }
  
  void makeText()
  {
    DropShadow ds = new DropShadow();
    ds.setOffsetX(2.0f);
    ds.setOffsetY(5.0f);
    ds.setColor(Color.BLACK);
    t = new Text();
    t.setX(10);
    t.setY(10);
    t.setText(s_txt);
    t.setFont(Font.font ("Verdana", 20));
    t.setEffect(ds);
    t.setFill(Color.BLACK);
    pane.getChildren().add(t);
    s_txt = null;
  }
  
  void makeShape()
  {
    makeRect();
    makeText();
  }

  @Override
  public void init(Pane pane)
  {
    this.pane = pane;
  }

  @Override
  public void handle(EventType evType, MouseEvent event)
  {
    if (evType == EventType.DRAG)
    {
      if (curGizmo!=null)
      {
        double posX = event.getX();
        double posY = event.getY();
        curObj.moveGizmo(curGizmo, posX, posY);
      }
      else if (r!=null)
      {
        double posX = event.getX();
        double posY = event.getY();
        update(r, t, r.getX(),r.getY(),posX,posY);
      }
    }
    else if (evType == EventType.CLICK)
    {
      boolean wasGizmoNull = PaintObject.getFocusObject()==null;
      curObj = null;
      curGizmo = null;
      
      if (s_txt!=null)
      { // New Shape
        makeShape();
        double posX = event.getX();
        double posY = event.getY();
        update(r, t, posX,posY,posX,posY);
      }
      else if (reText(event))
      { // Re-Text or Gizmo-Edit
        if (curGizmo == null)
        { // Re-Text
          Text t = (Text)curObj.getNodeList().get(1);
          String newText = askText(t.getText(), null);
          if (newText != null)
          {
            t.setText(newText);
            curObj.setGizmoActive(pane, true);
          }
        }
      }
      else
      { // New Text
        if (wasGizmoNull)
        {
          s_txt = askText(null, pane);
        }
      }
    }
    else if (evType == EventType.RELEASE)
    {
      if (curGizmo!=null)
      { // RefreshGizmo
        curObj.updateGizmoPositions();
      }
      else if (curObj==null)
      { // Finish Object
        createPaintObject(r, t);
        r = null;
        t = null;
        pane.setCursor(Cursor.DEFAULT);
      }
    }
  }

  private boolean reText(MouseEvent event)
  {
    EditTool.findCursorIntersectionShape(pane, (child) -> onIntersection(child), event.getX(), event.getY());
    if (curGizmo == null) { PaintObject.clearGizmos(pane); }
    if (curObj!=null) { return true; }
    return false;
  }

  /** Intersection with mouse.
   *  @see IntersectEvent.onIntersect(c,b) **/
  private boolean onIntersection(Node child)
  {
    PaintObject pObj = PaintObject.findObjectOf(child);
    if (pObj!=null)
    {
      if (pObj.getNodeList().size()<2) { return true; }
      if (pObj.getNodeList().get(1) instanceof Text)
      {
        curObj = pObj;
        return true;
      }
    }
    pObj = PaintObject.findObjectOfGizmo(child);
    if (pObj!=null)
    {
      curObj = pObj;
      curGizmo = child;
      return false;
    }
    return true;
  }

  private void createPaintObject(Rectangle r, Text t)
  {
    new PaintObject(r, t)
    {
      @Override
      public void moveGizmo(Node gizmo, double posX, double posY)
      {
        String s_giz = gizmo.getUserData().toString();
        Rectangle r = (Rectangle)getNodeList().get(0);
        Text t = (Text)getNodeList().get(1);
        if (s_giz.equals(GIZMO_START))
        {
          update(r, t, posX, posY, r.getX() + r.getWidth(), r.getY() + r.getHeight());
        }
        else if (s_giz.equals(GIZMO_END))
        {
          update(r, t, r.getX(), r.getY(), posX, posY);
        }
        if (s_giz.equals(GIZMO_CENTER))
        {
          double cx = r.getX() + (r.getWidth()/2.0);
          double cy = r.getY() + (r.getHeight()/2.0);
          double movX = posX -cx;
          double movY = posY -cy;
          double ex = r.getX() + r.getWidth() + movX;
          double ey = r.getY() + r.getHeight() + movY;
          update(r, t, r.getX() + movX, r.getY() + movY, ex, ey);
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

  private static void update(Rectangle r, Text t, double x1, double y1, double x2, double y2)
  {
    updateRect(r, x1, y1, x2, y2);
    updateText(t, x1, y1, x2, y2);
  }

  private static void updateRect(Rectangle r, double x1, double y1, double x2, double y2)
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
  
  private static void updateText(Text t, double x1, double y1, double x2, double y2)
  {
    t.setX(x1 + 5);
    t.setY(y1 + 20);
    double w = (x2-x1) - 10;
    if (w<20) { w = 20; }
    t.setWrappingWidth(w);
  }

  @Override
  public void onSelected()
  {
    s_txt = askText(s_txt, pane);
  }

  @Override
  public void onDeselected()
  {
    pane.setCursor(Cursor.DEFAULT);
  }
}
