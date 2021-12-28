package cls;

public class AttributeInfo {

  public final int attributeNameIndex;
  public final byte[] info;

  public AttributeInfo(
      int attributeNameIndex,
      byte[] info
  ) {
    this.attributeNameIndex = attributeNameIndex;
    this.info = info;
  }
}
