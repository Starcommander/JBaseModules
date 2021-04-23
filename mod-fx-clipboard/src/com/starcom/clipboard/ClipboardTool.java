package com.starcom.clipboard;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import com.starcom.debug.LoggingSystem;

public class ClipboardTool
{
  private Clipboard clip;

  /** Get the ClipboardTool from jnlp or awt. **/
  public ClipboardTool()
  {
    clip = Clipboard.getSystemClipboard();
  }

  public void putImageToClipboard(Image image)
  {
    ClipboardContent content = new ClipboardContent();
    content.putImage(image);
    clip.setContent(content);
  }

  public Image getImageFromClipboard()
  {
    return clip.getImage();
  }
}
