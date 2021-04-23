package com.starcom.paint.tools;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public interface ITool
{
  enum EventType {CLICK, RELEASE, DRAG, MOVE};
  public void init(Pane pane);
  public void handle(EventType type, MouseEvent event);
  public void onSelected();
  public void onDeselected();
}
