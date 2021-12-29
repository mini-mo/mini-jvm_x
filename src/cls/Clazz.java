package cls;

public class Clazz {

  public String name;
  public Field[] fields;
  public Method[] methods;
  public CpInfo[] cp;
  public int size;

  public Clazz(String name, Field[] fields, Method[] methods, CpInfo[] cp, int size) {
    this.name = name;
    this.fields = fields;
    this.methods = methods;
    this.cp = cp;
    this.size = size;
  }
}
