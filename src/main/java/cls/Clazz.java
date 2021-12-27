package cls;

public class Clazz {

  public String name;
  public Method[] methods;
  public CpInfo[] cp;

  public Clazz(String name, Method[] methods, CpInfo[] cp) {
    this.name = name;
    this.methods = methods;
    this.cp = cp;
  }
}
