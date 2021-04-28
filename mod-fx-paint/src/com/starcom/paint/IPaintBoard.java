package com.starcom.paint;

import java.util.ArrayList;
import javafx.scene.Node;

public interface IPaintBoard
{
  public ArrayList<AbstractPaintObject> getPaintObjects();
  public ArrayList<Node> getGizmoList();
}
