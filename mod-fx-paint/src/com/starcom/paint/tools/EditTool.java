package com.starcom.paint.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import com.starcom.paint.PaintObject;
import com.starcom.paint.events.IntersectEvent;

public class EditTool implements ITool
{
  static int line_thick = 5;
  static double opacity = 0.8;
  Pane pane;
  Node curGizmo;
  PaintObject curObj;
  
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
      if (curGizmo == null) { return; }
      if (curObj == null) { return; }
      double posX = event.getX();
      double posY = event.getY();
      curObj.moveGizmo(curGizmo, posX, posY);
    }
    else if (evType == EventType.CLICK)
    {
      PaintObject oldObj = curObj;
      double posX = event.getX();
      double posY = event.getY();
      findCursorIntersection(posX, posY);
      if (curObj != null && curGizmo == null)
      {
        curObj.setGizmoActive(pane, true);
      }
      else if (curObj != null)
      {
      }
      else if (oldObj != null)
      {
        oldObj.setGizmoActive(pane, false);
      }
    }
    else if (evType == EventType.RELEASE)
    {
      if (curGizmo!=null)
      {
        curObj.updateGizmoPositions();
      }
    }
  }
  
  /** Set curObj on intersection, and curGizmo on intersectionGizmo. **/
  private void findCursorIntersection(double posX, double posY)
  {
    curGizmo = null;
    curObj = null;
    findCursorIntersectionShape(pane, (child) -> onIntersection(child, true), posX, posY);
    if (curObj != null) { return; }
    findCursorIntersectionBound(pane, (child) -> onIntersection(child, false), posX, posY);
  }
  
  public static void findCursorIntersectionShape(Pane pane, IntersectEvent event, double posX, double posY)
  {
    Circle mousePoint = new Circle(posX, posY, 1);
    pane.getChildren().add(mousePoint);
    boolean continueSearch = true;
    for (Node child : pane.getChildren())
    {
      if (!(child instanceof Shape)) { continue; }
      Shape s = Shape.intersect((Shape)child, mousePoint);
      if (s.getBoundsInLocal().getWidth() != -1)
      {
        continueSearch = event.onIntersect(child);
        if (!continueSearch) { break; }
      }
    }
    pane.getChildren().remove(mousePoint);
  }
  
  public static void findCursorIntersectionBound(Pane pane, IntersectEvent event, double posX, double posY)
  {
    boolean continueSearch = true;
    for (Node child : pane.getChildren())
    {
      if (!(child instanceof Shape)) { continue; }
      if (child.intersects(posX, posY, 1, 1))
      {
        continueSearch = event.onIntersect(child);
        if (!continueSearch) { break; }
      }
    }
  }

  /** Intersection with mouse.
   *  @see IntersectEvent#onIntersect Same function from Interface. **/
  private boolean onIntersection(Node child, boolean isShapeSearch)
  {
    System.out.println("Selected: " + child);
    PaintObject obj = PaintObject.findObjectOfGizmo(child);
    if (obj!=null)
    {
      curGizmo = child;
      curObj = obj;
    }
    obj = PaintObject.findObjectOf(child);
    if (obj!=null)
    {
      curObj = obj;
    }
    if (isShapeSearch) { return curGizmo==null; }
    return curObj==null;
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
