package com.starcom.paint;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public abstract class PaintObject
{
  public static String GIZMO_START = "GIZMO_START";
  public static String GIZMO_END = "GIZMO_END";
  public static String GIZMO_CENTER = "GIZMO_CENTER";
  static ArrayList<PaintObject> paintObjects = new ArrayList<PaintObject>();
  static ArrayList<Node> gizmoList = new ArrayList<Node>();
  
  ArrayList<Node> nodeList = new ArrayList<Node>();
  boolean b_gizmoActive = false;
  
  public PaintObject(Node... nodes)
  {
    for (Node node : nodes)
    {
      nodeList.add(node);
    }
    paintObjects.add(this);
  }
  
  public ArrayList<Node> getNodeList() { return nodeList; }
  
  public static PaintObject findObjectOf(Node node)
  {
    for (PaintObject obj : paintObjects)
    {
      if (obj.nodeList.contains(node)) { return obj; }
    }
    return null;
  }
  
  public static PaintObject findObjectOfGizmo(Node node)
  {
    if (gizmoList.contains(node))
    {
      for (PaintObject obj : paintObjects)
      {
        if (obj.b_gizmoActive) { return obj; }
      }
    }
    return null;
  }
  
  public static void clearFocusObject(Pane pane)
  {
    PaintObject paintObj = getFocusObject();
    if (paintObj!=null)
    {
      paintObj.setGizmoActive(pane, false);
      for (Node child : paintObj.getNodeList())
      {
        pane.getChildren().remove(child);
      }
      paintObjects.remove(paintObj);
    }
  }

  public static PaintObject getFocusObject()
  {
    for (PaintObject obj : paintObjects)
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
    while (paintObjects.size() > 0)
    {
      PaintObject paintObj = paintObjects.get(0);
      paintObj.setGizmoActive(pane, false);
      for (Node child : paintObj.getNodeList())
      {
        pane.getChildren().remove(child);
      }
      paintObjects.remove(paintObj);
    }
  }
  
  public static Circle createGizmoCircle(Object userObject)
  {
    Circle sphere = new Circle(15.0);
    sphere.setOpacity(0.6);
    sphere.setUserData(userObject);
    return sphere;
  }
  
  public static void clearGizmos(Pane panel)
  {
    for (PaintObject obj : paintObjects)
    {
      obj.setGizmoActive(panel, false);
    }
  }
  
  public void setGizmoActive(Pane panel, boolean active)
  {
    if (active)
    {
      if (b_gizmoActive) { return; }
      if (gizmoList.size()!=0) { clearGizmos(panel); }
      appendGizmos(gizmoList);
      updateGizmoPositions(gizmoList);
      for (Node gizmo : gizmoList)
      {
        panel.getChildren().add(gizmo);
      }
    }
    else
    {
      if (!b_gizmoActive) { return; }
      for (Node gizmo : gizmoList)
      {
        panel.getChildren().remove(gizmo);
      }
      gizmoList.clear();
    }
    b_gizmoActive = active;
  }
  
  public boolean isGizmoActive() { return b_gizmoActive; }
  
  public void updateGizmoPositions() { updateGizmoPositions(gizmoList); }
  
  public abstract void moveGizmo(Node gizmo, double posX, double posY);
  
  /** Create Gizmos, set size and add to List. **/
  public abstract void appendGizmos(ArrayList<Node> gizmoList);
  
  public abstract void updateGizmoPositions(ArrayList<Node> gizmoList);

  /** Adds all current gizmos into tmp gizmoList. **/
  public void appendGizmoList(ArrayList<Node> tmpGizmoList)
  {
    tmpGizmoList.addAll(gizmoList);
  }
}
