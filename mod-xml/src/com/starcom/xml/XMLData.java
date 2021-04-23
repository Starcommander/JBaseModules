package com.starcom.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.FileInputStream;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

import com.starcom.Run;
import static com.starcom.debug.LoggingSystem.*;

public class XMLData
{
  Document doc;

  private XMLData() throws ParserConfigurationException
  {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    doc = docBuilder.newDocument();
  }

  private XMLData(String data, boolean isFile) throws Exception
  {
    InputStream is = null;
    try
    {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      docFactory.setNamespaceAware(true);
      if (isFile) { is = new FileInputStream(data); }
      else { is = new ByteArrayInputStream(data.getBytes("UTF-8")); }
      doc = docBuilder.parse(is);
    }
    catch (Exception e) { throw e; }
    finally
    {
      Run.close(is);
    }
  }

  /** Creates new xml data.<br>Use createElement("RootElementName",null) to create the rootElement **/
  public static XMLData newInstance()
  {
    try { return new XMLData(); }
    catch (Exception e) { severe(XMLData.class, e, "Error creating new XML!"); }
    return null;
  }

  public static XMLData loadInstance(String data, boolean isFile)
  {
    try { return new XMLData(data, isFile); }
    catch (Exception e) { severe(XMLData.class, e, "Error loading XML: "+ data); }
    return null;
  }

  /** @param parent NULL for root Element (Just one Element at root allowed) **/
  public Element createElement(String name, Element parent)
  {
    Element child = doc.createElement(name);
    if (parent==null)
    {
      doc.appendChild(child);
    }
    else
    {
      parent.appendChild(child);
    }
    return child;
  }
  
  public Element getRootElement()
  {
    return doc.getDocumentElement();
  }
  
  /** Enters the childs and returns the Element.
   *  @param tagNames The tagNames of childs to enter, without root!
   *  @return The Element, or null if not exists. **/
  public Element getSingleElement(String... tagNames)
  {
    Element rootElement = getRootElement();
    for (String tagName : tagNames)
    {
      rootElement = getSingleElement(rootElement, tagName);
      if (rootElement==null) { return null; }
    }
    return rootElement;
  }
  
  public Node renameNode(Node node, String newTagName)
  {
    return doc.renameNode(node, node.getNamespaceURI(), newTagName);
  }
  
  /** Nur fuer kompatiblitaet mit DataBlock und PrimBlock! **/
  public String getXmlString()
  {
    return getString().replace('"', '\'');
  }

  /** Create a structure from RootNode.
   *  @return The Structure as String. **/
  public String getString()
  {
    try
    {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT,"yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
      DOMSource source = new DOMSource(doc);
      StringWriter sw = new StringWriter();
      StreamResult result = new StreamResult(sw);
      transformer.transform(source, result);
      return sw.toString();
    } catch (Exception e) { severe(XMLData.class, e, "Error getting String from XML!"); }
    return null;
  }
  
  /** Create a structure from Node, independent from Document.
   *  @return The Structure as String. **/
  public static String getNodeString(Node node) { return getNodeString(node, new StringBuilder(), "").toString(); }
  private static StringBuilder getNodeString(Node node, StringBuilder sb, String indent)
  {
    sb.append(indent+"<"+node.getNodeName());
    NamedNodeMap attribs = node.getAttributes();
    if (attribs!=null)
    {
      for (int i=0; i<attribs.getLength(); i++)
      {
        Node curAtt = attribs.item(i);
        sb.append(" "+curAtt.getNodeName()+"='"+curAtt.getNodeValue()+"'");
      }
    }
    if (node.getChildNodes().getLength()==0)
    {
      sb.append("/>\n");
    }
    else
    {
      sb.append(">\n");
      for (int i=0; i<node.getChildNodes().getLength(); i++)
      {
        getNodeString(node.getChildNodes().item(i), sb, indent+"  ");
      }
      sb.append(indent+"</"+node.getNodeName()+">\n");
    }
    return sb;
  }
  
  /** Useful for testing isEqualNode(Node) on xml data Element. **/
  public static void clearTextNodes(Element el)
  {
    for (int i=0; i<el.getChildNodes().getLength(); i++)
    {
      Node node = el.getChildNodes().item(i);
      if (node instanceof org.w3c.dom.Text)
      {
        el.removeChild(node);
      }
      else if (node instanceof Element)
      {
        clearTextNodes((Element)node);
      }
    }
  }

  /** Useful for testing isEqualNode(Node) on xml data Element. **/
  public static void sortElements(Element el)
  {
    ArrayList<Element> elList = new ArrayList<Element>();
    for (int i=0; i<el.getChildNodes().getLength(); i++)
    {
      Node node = el.getChildNodes().item(i);
      if (node instanceof Element)
      {
        insertToOrderPosition(elList, (Element)node);
      }
    }
    for (Element curEl : elList)
    {
      el.removeChild(curEl);
      sortElements(curEl);
      el.appendChild(curEl);
    }
  }

  /** Insert to list in sorted order **/
  private static void insertToOrderPosition(ArrayList<Element> elList, Element node)
  {
    int position = 0;
    for (Element curEl : elList)
    {
      if (node.getTagName().compareTo(curEl.getTagName()) > 0) { position++; }
      else { break; }
    }
    elList.add(position,(Element)node);
  }
  
  public static Element getSingleElement(Element rootElement, String tagName)
  {
    NodeList list = rootElement.getChildNodes();
    for (int i=0; i<list.getLength(); i++)
    {
      Node curNode = list.item(i);
      if (curNode instanceof Element)
      {
        if (curNode.getNodeName().equals(tagName)) { return (Element)curNode; }
      }
    }
    return null;
  }
  
  /** Builds an ArrayList with all next childElements. **/
  public static ArrayList<Element> getElements(Element rootElement)
  {
    NodeList nodeList = rootElement.getChildNodes();
    ArrayList<Element> childElements = new ArrayList<Element>();
    for (int i=0; i<nodeList.getLength(); i++)
    {
      Node node = nodeList.item(i);
      if (!(node instanceof Element)) { continue; }
      childElements.add((Element)node);
    }
    return childElements;
  }
  
  public static Element getElementWithAttr(Element rootElement, String name, String value)
  {
    NodeList elList = rootElement.getChildNodes();
    for (int i=0; i<elList.getLength(); i++)
    {
      Node curElement = elList.item(i);
      if (! (curElement instanceof Element)) { continue; }
      if (((Element)curElement).getAttribute(name).equals(value))
      {
        return (Element)curElement;
      }
    }
    return null;
  }
  
  /** Used to import foreign Node!
   *  @return The new Node to import! **/
  @Deprecated // Use importNode() or replaceNode() instead!
  public static Node getImportableNode(XMLData target, Node externalNode)
  {
    return target.doc.importNode(externalNode,true);
  }

  /** Used to import foreign Node as clone! **/
  public static void importNode(Node source, Node target)
  {
    Node newSource = target.getOwnerDocument().importNode(source,true);
    target.appendChild(newSource);
  }

  /** Used to import foreign Node as clone!
   *  @param oldNode The old Node to replace from his parent. **/
  public static void replaceNode(Node oldNode, Node newNode)
  {
    Node parent = oldNode.getParentNode();
    parent.removeChild(oldNode);
    importNode(newNode,parent);
  }
  
  /** Used to import foreign Node as clone!
   *  @param newNode The new RootNode. **/
  public void replaceRootNode(Node newNode)
  {
    newNode = doc.importNode(newNode,true);
    if (doc.getDocumentElement()==null)
    {
      doc.appendChild(newNode);
    }
    else
    {
      doc.replaceChild(newNode, doc.getDocumentElement());
    }
  }
  
  /** Nicht vorhandene Elemente und Attribute werden in target erzeugt. **/
  public static void syncNodes(Element target, Element source)
  {
    for (int i=0; i<source.getAttributes().getLength(); i++)
    {
      org.w3c.dom.Attr curAttr = (org.w3c.dom.Attr)source.getAttributes().item(i);
      if (!target.hasAttribute(curAttr.getName()))
      {
        target.setAttribute(curAttr.getName(), curAttr.getValue());
      }
    }
    for (int i=0; i<source.getChildNodes().getLength(); i++)
    {
      Node curNode = source.getChildNodes().item(i);
      if (curNode instanceof Element)
      {
        Element curElement = (Element)curNode;
        if (getSingleElement(target,curElement.getTagName())==null)
        {
          importNode(curElement, target);
        }
        else
        {
          syncNodes(getSingleElement(target,curElement.getTagName()), curElement);
        }
      }
    }
  }

  /** Creates a separated String with the ElementTagNames of baseElement and all parents. **/
  public static StringBuilder buildTagNames(Element baseElement, char sep)
  {
    StringBuilder sb = new StringBuilder();
    Node curNode = baseElement;
    String sepS = "";
    while(curNode != null)
    {
      if (curNode instanceof Element)
      {
        Element curElement = (Element)curNode;
        sb.insert(0, sepS).insert(0, curElement.getTagName());
        sepS = "" + sep;
      }
      curNode = curNode.getParentNode();
    }
    return sb;
  }

}
