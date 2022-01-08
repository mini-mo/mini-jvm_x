package cls;

// https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.1
public final class ClassFile {

  public final int magic;
  public final int minorVersion;
  public final int majorVersion;
  public final int constantPoolCount;
  public final CpInfo[] cp;
  public final int accessFlags;
  public final int thisClass;
  public final int superClass;
  public final int[] interfaces;
  public final FieldInfo[] fields;
  public final MethodInfo[] methods;
  public final AttributeInfo[] attributes;

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
