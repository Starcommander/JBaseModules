package com.starcom;

import static com.starcom.debug.LoggingSystem.*;
import java.lang.reflect.Method;

public class Run
{
  static long id = -1;

  /** Load class dynamically. Returns null on error. **/
  public static Object loadClass(String className)
  {
    try
    {
      Class<?> c = Class.forName(className);
      return c.newInstance();
    } catch (Exception e) { severe(Run.class, e);}
    return null;
  }

  public static void close(Object closeable)
  {
    call(closeable, "close");
  }

  /** Call a method from dynamic class!
   *  @param callable The object got from loadClass(ClassName)
   *  @param method The method name
   *  @param args The arguments for the method. **/
  public static Object call(Object callable, String method, Object... args)
  {
    if (callable==null) { return null; }
    try
    {
      Method createdMethod = callable.getClass().getDeclaredMethod(method);
      return createdMethod.invoke(callable, args);
    } catch( Exception e ) { severe( Run.class, e); }
    return null;
  }

  public static Object getFieldValueAsObject(Object clazz, String fieldName)
  {
    try
    {
      return clazz.getClass().getField(fieldName).get(clazz);
    } catch( Exception e ) { severe( Run.class, e); }
    return null;
  }

  /** Returns an unique threadsave id. (Simple counter) **/
  public synchronized static long getId()
  {
    id++;
    return id;
  }

}
