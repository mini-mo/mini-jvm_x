package cls;

public class MethodInfo {

  public final int accessFlags;
  public final int nameIndex;
  public final int descriptorIndex;
  public final AttributeInfo[] attributes;

  public MethodInfo(
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
