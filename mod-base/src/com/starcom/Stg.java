package com.starcom;

import java.util.*;
import java.io.UnsupportedEncodingException;

/**
* Programme zum Bearbeiten von Strings!
**/

public class Stg
{
  static final String[] HTML_TAGS = {"&amp;","&lt;","&gt;","<br>","&quot;","&apos;"};
  static final String[] TXT_TAGS = {"&","<",">","\n","\"","\'"};
  static final Comparator<String> ABC_COMPARATOR = new Comparator<String>()
  {
    @Override
    public int compare(String s1, String s2)
    {
      return s1.compareToIgnoreCase(s2);
    }
  };

  public static String toString(byte[] b)
  { // Konvertiert zum String in UTF_8, wenn supported!
    try
    {
      return new String(b,com.starcom.IO.UTF_8);
    } catch (UnsupportedEncodingException e) { e.printStackTrace(); }
    return null;
  }

  public static byte[] toByteArray(String s)
  { // Konvertiert zum ByteArray in UTF_8, wenn supported!
    try
    {
      return s.getBytes(com.starcom.IO.UTF_8);
    } catch (UnsupportedEncodingException e) { e.printStackTrace(); }
    return null;
  }

  /** Sortiert nach dem ABC **/
  public static String[] sortABC(String w[])
  {
    Arrays.sort(w);
    return w;
  }

  /** Sortiert nach dem ABC **/
  public static ArrayList<String> sortABC(ArrayList<String> w)
  {
    Collections.sort(w, ABC_COMPARATOR);
    return w;
  }
  
  /** Replace text without using Patterns. **/
  public static String doReplaceFirst(String fullText, String key, String replacement)
  {
    int txtReplIndex = fullText.indexOf(key);
    int txtReplIndexLast = txtReplIndex + key.length();
    if (txtReplIndex != -1)
    {
      fullText = fullText.substring(0,txtReplIndex)
                 + replacement
                 + fullText.substring(txtReplIndexLast);
    }
    return fullText;
  }

  /** Filtert einen StringArray! <br>
      (Array, startsWith(oder null), true(um Strings welche starten zu erhalten), endsWith, true(--"--)) **/
  public static String[] filter(String toFilter[], String startsWith, boolean sWith, String endsWith, boolean eWith)
  {
    ArrayList<String> ret = new ArrayList<String>();
    for (int i=0; i<toFilter.length; i++)
    {
      ret.add(toFilter[i]);
      if (startsWith!=null)
      {
        boolean s = toFilter[i].startsWith(startsWith);
        if (sWith && !s) {ret.remove(toFilter[i]); continue;}
        else if (!sWith && s) {ret.remove(toFilter[i]); continue;}
      }
      if (endsWith!=null)
      {
        boolean e = toFilter[i].endsWith(endsWith);
        if (eWith && !e) {ret.remove(toFilter[i]);}
        else if (!eWith && e) {ret.remove(toFilter[i]);}
      }
    }
    String retA[] = new String[ret.size()];
    for (int i=0; i<ret.size(); i++) {retA[i] = ret.get(i);}
    return retA;
  }

  public static String ersetzen (String stg, String keyword, String ersatz)
  {
    return stg.replace(keyword, ersatz);
  }

  /** Ersetzt Keywoerter mit dazugehoerigem Ersatz **/
  public static String ersetzen (String stg, String keyword[], String ersatz[])
  { //TODO: Performance-Test im Vergleich zu String.replace in einer Schleife.
    char array[]=stg.toCharArray();
    StringBuilder newS = new StringBuilder();
    for (int a=0; a<array.length; a++)
    {
      newS.append(array[a]);
      for (int k=0; k<keyword.length; k++)
      {
        int endPos = newS.length();
        int startPos = (newS.length()-keyword[k].length());
        if (startPos<0) { continue; }
        if (newS.substring(startPos,endPos).equals(keyword[k]))
        {
          newS.replace(startPos,endPos,ersatz[k]);
        }
      }
    }
    return newS.toString();
  }

  public static String toHtml(String txt)
  {
    return ersetzen(txt, TXT_TAGS, HTML_TAGS);
  }
  
  /** Ersetzt XML Zeichen mit aehnlichen Zeichen. **/
  public static String toXmlValue(String txt)
  {
    txt = txt.replace('\'', '°');
    txt = txt.replace('"', '°');
    txt = txt.replace('<', '(');
    txt = txt.replace('>', ')');
    return txt;
  }
  
  public static String fromHtml(String txt)
  {
    return ersetzen(txt, HTML_TAGS, TXT_TAGS);
  }

  /** True, wenn String aus reiner Zahl besteht. (Kein Buchstabe erlaubt) **/
  public static boolean zahl(String zahl1)
  {
    if (zahl1.equals("")) {return false;}
    char ziff[] = zahl1.toCharArray();
    for (int i=0; i<ziff.length; i++)
    {
      if (!zahl(ziff[i])) {return false;}
    }
  return true;
  }

  /** True, wenn 'ziff' aus Ziffer besteht. **/
  public static boolean zahl(char ziff)
  {
    if (ziff=='1') {return true;}
    else if (ziff=='2') {return true;}
    else if (ziff=='3') {return true;}
    else if (ziff=='4') {return true;}
    else if (ziff=='5') {return true;}
    else if (ziff=='6') {return true;}
    else if (ziff=='7') {return true;}
    else if (ziff=='8') {return true;}
    else if (ziff=='9') {return true;}
    else if (ziff=='0') {return true;}
    return false;
  }

  /** Testet, ob 'string' mit 'endung' endet. **/
  public static boolean endsWithIgnoreCase(String string, String endung)
  {
    if (string.length() < endung.length()) {return false;}
    String testEndung = string.substring(string.length()-endung.length(), string.length());
    return testEndung.equalsIgnoreCase(endung);
  }

  /** Testet, ob 'string' mit einem der 'endungs' endet.
      Hier wird angenommen, dass alle Endungen gleich lang sind!
      @param endLen Ist ueblicherweise 4. Beispiel bei '.txt'
  **/
  public static boolean endsWithIgnoreCase(String string, int endLen, String... endungs)
  {
    if (string.length() < endLen) {return false;}
    String testEndung = string.substring(string.length()-endLen);
    for (int i=0; i<endungs.length; i++)
    {
      if (testEndung.equalsIgnoreCase(endungs[i])) { return true; }
    }
    return false;
  }

  /** Erhoeht die Zahl am Ende von name! Wenn keine Zahl, dann 1. **/
  public static String extensionUp(String name)
  {
    if (name.length()==0) {return "1";}
    int ziffern = 0;
    for (int i=0; i<name.length(); i++)
    {
      String ziffer = name.substring(name.length()-(1+i),name.length()-i);
      if (zahl(ziffer)) {ziffern++;}
      else {break;}
    }
    if (ziffern==0) {return name+"1";}
    String zahlStg = name.substring(name.length()-ziffern,name.length());
    int zahl = Integer.parseInt(zahlStg);
    zahl++;
    return name.substring(0,name.length()-ziffern)+zahl;
  }

  /** Entfernt die Zahl am Ende von name! **/
  public static String extensionDelete(String name)
  {
    char n[] = name.toCharArray();
    int index = n.length;
    for (int i=n.length-1; i>=0; i--)
    {
      if (!com.starcom.Stg.zahl(n[i])) { break; }
      index = i;
    }
    return name.substring(0,index);
  }

  public static ArrayList<String> toArrayList(String txt)
  {
    StringTokenizer sTok = new StringTokenizer(txt);
    ArrayList<String> list = new ArrayList<String>();
    while(sTok.hasMoreTokens())
    {
      list.add(sTok.nextToken());
    }
    return list;
  }

} // Ende Klasse
