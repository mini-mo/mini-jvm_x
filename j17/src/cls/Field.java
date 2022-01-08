package cls;

/**
 * 字段
 */
public class Field {

  public String name;
  public String descriptor;

  public int accessFlags;

  public int offset;

  public Field(){}

  public Field(
      String name,
      String descriptor,
      int accessFlags,
      int offset
  ) {
    this.name = name;
    this.descriptor = descriptor;
    this.accessFlags = accessFlags;
    this.offset = offset;
  }
}
