package com.starcom.system;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class ClassPath
{
  public static boolean isClassAvailable(String className)
  {
    try
    {
      ClassLoader.getSystemClassLoader().loadClass(className);
    } catch (Exception e) { return false; }
    return true;
  }

  public static void addToClassPath(String jar)
  {
    ArrayList<File> jars = new ArrayList<File>();
    jars.add(new File(jar));
    addToClassPath(jars);
  }

  public static void addToClassPath(ArrayList<File> jars)
  {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader instanceof URLClassLoader)
    {
      try
      {
        Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
        addUrlMethod.setAccessible(true);
        if (addUrlMethod!=null)
        {
          for (File jar : jars)
          {
            try
            {
              if (!jar.exists()) { throw(new java.io.FileNotFoundException("Classpath existiert nicht: "+jar)); }
              addUrlMethod.invoke(classLoader, jar.toURI().toURL());
            } catch (Exception e) { e.printStackTrace(); }
          }
        }
      } catch (Exception e) { e.printStackTrace(); }
    }
  }

  public static void addJarDirToClassPath(String jarDir)
  {
    String list[] = new File(jarDir).list();
    ArrayList<File> jars = new ArrayList<File>();
    for (String jar : list)
    {
      if (!jar.endsWith(".jar")) { continue; }
      jars.add(new File(jarDir+jar));
    }
    addToClassPath(jars);
  }

}