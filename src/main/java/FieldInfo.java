public class FieldInfo {

  final int accessFlags;
  final int nameIndex;
  final int descriptorIndex;
  final AttributeInfo[] attributes;

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