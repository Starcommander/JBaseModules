package com.starcom.paint.events;

import javafx.scene.Node;

public interface IntersectEvent
{
  /** Called on Intersection of Mouse with child.
   * @param child The intersected child.
   * @return Should continue searching. **/
  public boolean onIntersect(Node child);
}
