// https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1
public final class ClassFile {

  final int magic;
  final int minorVersion;
  final int majorVersion;
  final int constantPoolCount;
  final CpInfo[] cp;
  final int accessFlags;
  final int thisClass;
  final int superClass;
  final int[] interfaces;
  final FieldInfo[] fields;
  final MethodInfo[] methods;
  final AttributeInfo[] attributes;

  public ClassFile(
      int magic,
      int minorVersion,
      int majorVersion,
      int constantPoolCount,
      CpInfo[] cp,
      int accessFlags,
      int thisClass,
      int superClass,
      int[] interfaces,
      FieldInfo[] fields,
      MethodInfo[] methods,
      AttributeInfo[] attributes
  ) {
    this.magic = magic;
    this.minorVersion = minorVersion;
    this.majorVersion = majorVersion;
    this.constantPoolCount = constantPoolCount;
    this.cp = cp;
    this.accessFlags = accessFlags;
    this.thisClass = thisClass;
    this.superClass = superClass;
    this.interfaces = interfaces;
    this.fields = fields;
    this.methods = methods;
    this.attributes = attributes;
  }

}
