package com.starcom.paint;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public abstract class AbstractPaintObject
{
  public static final String GIZMO_START = "GIZMO_START";
  public static final String GIZMO_END = "GIZMO_END";
  public static final String GIZMO_CENTER = "GIZMO_CENTER";

  public static IPaintBoard paintBoard = createSimplePaintboard();
  /** The list with polygons of this AbstractPaintObject. **/
  ArrayList<Node> nodeList = new ArrayList<Node>();
  boolean b_gizmoActive = false;
  
  public AbstractPaintObject(Node... nodes)
  {
    for (Node node : nodes)
    {
      nodeList.add(node);
    }
    paintBoard.getPaintObjects().add(this);
  }
  
  public ArrayList<Node> getNodeList() { return nodeList; }
  
  public static AbstractPaintObject findObjectOf(Node node)
  {
    for (AbstractPaintObject obj : paintBoard.getPaintObjects())
    {
      if (obj.nodeList.contains(node)) { return obj; }
    }
    return null;
  }
  
  public static AbstractPaintObject findObjectOfGizmo(Node node)
  {
    if (paintBoard.getGizmoList().contains(node))
    {
      for (AbstractPaintObject obj : paintBoard.getPaintObjects())
      {
        if (obj.b_gizmoActive) { return obj; }
      }
    }
    return null;
  }
  
  public static void clearFocusObject(Pane pane)
  {
    AbstractPaintObject paintObj = getFocusObject();
    if (paintObj!=null)
    {
      paintObj.setGizmoActive(pane, false);
      for (Node child : paintObj.getNodeList())
      {
        pane.getChildren().remove(child);
      }
      paintBoard.getPaintObjects().remove(paintObj);
    }
  }

  public static AbstractPaintObject getFocusObject()
  {
    for (AbstractPaintObject obj : paintBoard.getPaintObjects())
    {
      if (obj.b_gizmoActive)
      {
        return obj;
      }
    }
    return null;
  }

  public static void clearAllObjects(Pane pane)
  {
    while (paintBoard.getPaintObjects().size() > 0)
    {
      AbstractPaintObject paintObj = paintBoard.getPaintObjects().get(0);
      paintObj.setGizmoActive(pane, false);
      for (Node child : paintObj.getNodeList())
      {
        pane.getChildren().remove(child);
      }
      paintBoard.getPaintObjects().remove(paintObj);
    }
  }
  
  public static Circle createGizmoCircle(Object userObject)
  {
    Circle sphere = new Circle(15.0);
    sphere.setOpacity(0.6);
    sphere.setUserData(userObject);
    return sphere;
  }
  
  /** Clear all gizmos from panel. **/
  public static void clearGizmos(Pane panel)
  {
    for (AbstractPaintObject obj : paintBoard.getPaintObjects())
    {
      obj.setGizmoActive(panel, false);
    }
  }
  
  /** Set Gizmo active from this AbstractPaintObject on panel. **/
  public void setGizmoActive(Pane panel, boolean active)
  {
    if (active)
    {
      if (b_gizmoActive) { return; }
      if (paintBoard.getGizmoList().size()!=0) { clearGizmos(panel); }
      appendGizmos(paintBoard.getGizmoList());
      updateGizmoPositions(paintBoard.getGizmoList());
      for (Node gizmo : paintBoard.getGizmoList())
      {
        panel.getChildren().add(gizmo);
      }
    }
    else
    {
      if (!b_gizmoActive) { return; }
      for (Node gizmo : paintBoard.getGizmoList())
      {
        panel.getChildren().remove(gizmo);
      }
      paintBoard.getGizmoList().clear();
    }
    b_gizmoActive = active;
  }
  
  public boolean isGizmoActive() { return b_gizmoActive; }
  
  public void updateGizmoPositions() { updateGizmoPositions(paintBoard.getGizmoList()); }
  
  public abstract void moveGizmo(Node gizmo, double posX, double posY);
  
  /** Create Gizmos, set size and add to List. **/
  public abstract void appendGizmos(ArrayList<Node> gizmoList);
  
  public abstract void updateGizmoPositions(ArrayList<Node> gizmoList);
  
  public abstract String saveObj();
  public abstract void loadObj(String data);

  /** Adds all current gizmos into tmp gizmoList. **/
  public void appendGizmoList(ArrayList<Node> tmpGizmoList)
  {
    tmpGizmoList.addAll(paintBoard.getGizmoList());
  }

  private static IPaintBoard createSimplePaintboard()
  {
    return new IPaintBoard()
    {
      ArrayList<AbstractPaintObject> paintObjects = new ArrayList<AbstractPaintObject>();
      ArrayList<Node> gizmoList = new ArrayList<Node>();

      @Override
      public ArrayList<AbstractPaintObject> getPaintObjects() { return paintObjects; }
      @Override
      public ArrayList<Node> getGizmoList() { return gizmoList; }
    };
  }
}
