public class AttributeInfo {

  final int attributeNameIndex;
  final byte[] info;

  public AttributeInfo(
      int attributeNameIndex,
      byte[] info
  ) {
    this.attributeNameIndex = attributeNameIndex;
    this.info = info;
  }
}
