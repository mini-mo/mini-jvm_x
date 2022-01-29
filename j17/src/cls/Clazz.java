package cls;

import core.Const;

public class Clazz {

  public int state;
  public int flags;

  public String name;
  public Field[] fields;
  public Method[] methods;
  public Method[] imethods;
  public CpInfo[] cp;

  public Clazz spr;
  public Clazz[] interfaces;

  public int size;

  // Clazz 实例在虚拟机的引用地址
  public int offset;

  public Clazz(
      String name,
      Clazz spr,
      Clazz[] interfaces,
      Field[] fields,
      Method[] methods,
      CpInfo[] cp
  ) {
    this.name = name;
    this.spr = spr;
    this.interfaces = interfaces;
    this.fields = fields;
    this.methods = methods;
    this.cp = cp;
    this.state = Const.CLASS_LOADED;
  }
}
