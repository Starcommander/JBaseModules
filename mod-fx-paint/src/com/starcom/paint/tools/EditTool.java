package com.starcom.paint.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import com.starcom.paint.AbstractPaintObject;
import com.starcom.paint.events.IntersectEvent;
import com.starcom.listener.ObjListener2;

public class EditTool implements ITool
{
  static int line_thick = 5;
  static double opacity = 0.8;
  static Node curGizmo;
  static AbstractPaintObject curObj;
  static ObjListener2 postUpdateHook;
  Pane pane;

  @Override
  public void init(Pane pane)
  {
    this.pane = pane;
  }

  public Pane getPane() { return pane; }

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
      AbstractPaintObject oldObj = curObj;
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
    if (postUpdateHook!=null) { postUpdateHook.actionPerformed(evType, event); }
  }

  /** Empty class for overriding, returns true on consume. **/
  public static void setPostUpdateHook(ObjListener2 postUpdateHookNew) { postUpdateHook = postUpdateHookNew; }

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
    AbstractPaintObject obj = AbstractPaintObject.findObjectOfGizmo(child);
    if (obj!=null)
    {
      curGizmo = child;
      curObj = obj;
    }
    obj = AbstractPaintObject.findObjectOf(child);
    if (obj!=null)
    {
      curObj = obj;
    }
    if (isShapeSearch) { return curGizmo==null; }
    return curObj==null;
  }

  public AbstractPaintObject getCurPaintObj() { return curObj; }
  public Node getCurGizmo() { return curGizmo; }

  @Override
  public void onSelected()
  {
  }

  @Override
  public void onDeselected()
  {
  }

}
