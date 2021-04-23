package com.starcom.pix.tagger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.starcom.file.Path;
import com.starcom.xml.XMLData;
import static com.starcom.debug.LoggingSystem.*;

public class ImageTagger
{
  public final static String STD_METADATA_FORMAT = IIOMetadataFormatImpl.standardMetadataFormatName;
  
  Properties imageTags = new Properties();
  BufferedImage image;
  IIOMetadata metadata;
  
  /** Creates a new ImageTagger.
   *  <br>Currently only png is supported.
   *  <br>JPEG makes some problems, TIFF is impossible.
   *  <br>Better using specific metadata instead? **/
  public ImageTagger() {}

  public Properties getMetadataProp() { return imageTags; }
  public IIOMetadata getMetadata() { return metadata; }
  
  public void writeMetadataImage(String targetFile) throws Exception
  {
    if (image==null) { readImageFromFile(targetFile); }
    String extension = Path.getExtension(targetFile, ".png").substring(1);
    ImageWriter writer = ImageIO.getImageWritersByFormatName(extension).next();
    ImageWriteParam writeParam = writer.getDefaultWriteParam();
    ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(image.getType());
    IIOMetadataNode root = new IIOMetadataNode(STD_METADATA_FORMAT);
    if (metadata!=null && clearTextBlock(metadata)!=null)
    { // TODO: On JPG duplicate of tag: <app0JFIF> causes Exception, and clearing metadata not possible.
      info(ImageTagger.class, "Clearing metadata successful.");
    }
    else
    {
      IIOMetadata newMetadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
      metadata = newMetadata;
    }
    if (imageTags.size()>0)
    {
      IIOMetadataNode text = new IIOMetadataNode("Text");
      for(Entry<Object, Object> curEntry : imageTags.entrySet())
      { // TODO: On JPG keyword is forced "comment"
        IIOMetadataNode textEntry = new IIOMetadataNode("TextEntry");
        textEntry.setAttribute("keyword", curEntry.getKey().toString());
        textEntry.setAttribute("value", curEntry.getValue().toString());
        textEntry.setAttribute("encoding", "UTF-8");
        textEntry.setAttribute("language", "EN");
        textEntry.setAttribute("compression", "none");
        text.appendChild(textEntry);
      }
      root.appendChild(text);
    }
    metadata.mergeTree(STD_METADATA_FORMAT, root);
    try(ImageOutputStream stream = ImageIO.createImageOutputStream(new File(targetFile)))
    {
      writer.setOutput(stream);
      writer.write(metadata, new IIOImage(image, null, metadata), writeParam);
    }
  }

  public void readImageFromFile(String srcFile) throws IOException
  {
    image = ImageIO.read(new File(srcFile));
  }
  
  public void readMetadataFromFile(String srcFile) throws IOException
  {
    String extension = Path.getExtension(srcFile, ".png").substring(1);
    ImageReader imageReader = ImageIO.getImageReadersByFormatName(extension).next();
    try(FileInputStream fis = new FileInputStream(srcFile))
    {
      imageReader.setInput(ImageIO.createImageInputStream(fis), true);
      metadata = imageReader.getImageMetadata(0);
    }
    Node mainNode = metadata.getAsTree(STD_METADATA_FORMAT);
    NodeList childs = mainNode.getChildNodes();
    for (int i=0; i<childs.getLength(); i++)
    {
      Node curNode = childs.item(i);
      if (curNode.getNodeName().equals("Text"))
      {
        NodeList txtChilds = curNode.getChildNodes();
        for (int t=0; t<txtChilds.getLength(); t++)
        { // TODO: On JPG keyword is forced "comment"
          Node txtNode = txtChilds.item(t);
          String keyword = txtNode.getAttributes().getNamedItem("keyword").getNodeValue();
          String value = txtNode.getAttributes().getNamedItem("value").getNodeValue();
          imageTags.put(keyword, value);
        }
        break;
      }
    }
  }
  
  /** Delete custom Text **/
  private Node clearTextBlock(IIOMetadata metadata)
  {
    try
    {
        Node mainNode = metadata.getAsTree(STD_METADATA_FORMAT);
        NodeList childs = mainNode.getChildNodes();
        for (int i=0; i<childs.getLength(); i++)
        {
          Node curNode = childs.item(i);
          if (curNode.getNodeName().equals("Text"))
          {
            mainNode.removeChild(curNode); // Delete "Text".
            break;
          }
        }
        metadata.setFromTree(STD_METADATA_FORMAT, mainNode);
        return mainNode;
    }
    catch (IIOInvalidTreeException|IllegalStateException e)
    {
      severe(ImageTagger.class, "Cannot clear metadata");
      return null;
    }
  }

  public static void main(String[] args) throws Exception
  {
    if (args.length<2) { printUsage("Args count wrong!"); }
    else if (!new File(args[1]).isFile()) { printUsage("No File!"); }
    else if (args[0].equals("read"))
    {
      ImageTagger imageTagger = new ImageTagger();
      imageTagger.readMetadataFromFile(args[1]);
      for (Entry<Object, Object> curEntry : imageTagger.getMetadataProp().entrySet())
      {
        System.out.println("Entry: key="+curEntry.getKey()+" value="+curEntry.getValue());
      }
    }
    else if (args[0].equals("print"))
    {
      ImageTagger imageTagger = new ImageTagger();
      imageTagger.readMetadataFromFile(args[1]);
      String txt = XMLData.getNodeString(imageTagger.getMetadata().getAsTree(STD_METADATA_FORMAT));
      System.out.println(txt);
    }
    else if (args[0].equals("write"))
    {
      if (args.length<4) { printUsage("No key- and value-arg to write!"); }
      else if (args.length>4) { printUsage("Too many arguments!"); }
      else
      {
        ImageTagger imageTagger = new ImageTagger();
        imageTagger.readMetadataFromFile(args[1]);
        imageTagger.readImageFromFile(args[1]);
        imageTagger.getMetadataProp().put(args[2], args[3]);
        imageTagger.writeMetadataImage(args[1]);
      }
    }
    else if (args[0].equals("clear"))
    {
      ImageTagger imageTagger = new ImageTagger();
      imageTagger.readMetadataFromFile(args[1]);
      imageTagger.readImageFromFile(args[1]);
      imageTagger.getMetadataProp().clear();
      imageTagger.writeMetadataImage(args[1]);
    }
    else { printUsage("Command unknown"); }
  }

  private static void printUsage(String txt)
  {
    System.out.println(txt);
    System.out.println("Usage args: read fileName");
    System.out.println("Usage args: print fileName");
    System.out.println("Usage args: write fileName key value");
    System.out.println("Usage args: clear fileName");
  }

}
