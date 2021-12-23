package cls;


public class FieldInfo {

  public final int accessFlags;
  public final int nameIndex;
  public final int descriptorIndex;
  public final AttributeInfo[] attributes;

  public FieldInfo(
      int accessFlags,
      int nameIndex,
      int descriptorIndex,
      AttributeInfo[] attributes
  ) {
    this.accessFlags = accessFlags;
    this.nameIndex = nameIndex;
    this.descriptorIndex = descriptorIndex;
    this.attributes = attributes;
  }
}