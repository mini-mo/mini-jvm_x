public class ClassReader {

  private final byte[] raw;
  private final int len;
  private int cur = 0;

  ClassReader(byte[] raw) {
    this.raw = raw;
    this.len = this.raw.length;
  }

  int u1() {
    check(1);
    return raw[cur++];
  }

  int u2() {
    check(2);
    return (raw[cur++] << 8) + (raw[cur++] << 0);
  }

  int u4() {
    check(4);
    return (((raw[cur++] & 0xff) << 24) + ((raw[cur++] & 0xff) << 16) + ((raw[cur++] & 0xff) << 8) + (
        (raw[cur++] & 0xff) << 0));
  }

  byte[] raw(int size) {
    check(size);
    final byte[] bytes = new byte[size];
    for (int i = 0; i < size; i++) {
      bytes[i] = raw[cur++];
    }
    return bytes;
  }

  private void check(int size) {
    if (cur + size > len) {
      throw new RuntimeException();
    }
  }

  ClassFile read() {
    final int magic = u4();
    final int minor = u2();
    final int major = u2();

    final int constantPoolCount = u2();
    CpInfo[] cp = new CpInfo[constantPoolCount];

    // cp
    for (int i = 1; i < constantPoolCount; i++) {
      int tag = u1();
      switch (tag) {
        case Const.CONSTANT_NameAndType, Const.CONSTANT_Methodref, Const.CONSTANT_Fieldref, Const.CONSTANT_InterfaceMethodref -> cp[i] = new CpInfo(
            tag,
            raw(4));
        case Const.CONSTANT_Class -> cp[i] = new CpInfo(tag, raw(2));
        case Const.CONSTANT_Utf8 -> {
          final int len = u2();
          cp[i] = new CpInfo(tag, raw(len));
        }
        default -> {
          throw new RuntimeException("tag " + tag);
        }
      }
    }
    // end cp
    final int access = u2();
    final int self = u2();
    final int parent = u2();

    // interface
    final int interfaceCount = u2();
    final int[] interfaces = new int[interfaceCount];
    for (int i = 0; i < interfaceCount; i++) {
      interfaces[i] = u2();
    }

    // field
    final int fieldCount = u2();
    final FieldInfo[] fields = new FieldInfo[fieldCount];
    for (int i = 0; i < fieldCount; i++) {
      int faf = u2();
      int fni = u2();
      int fdi = u2();
      int fac = u2();
      final AttributeInfo[] fais = new AttributeInfo[fac];
      for (int fi = 0; fi < fac; fi++) {
        fais[fi] = new AttributeInfo(u2(), raw(u4()));
      }
      fields[i] = new FieldInfo(faf, fni, fdi, fais);
    }
    // method
    final int methodCount = u2();
    final MethodInfo[] methods = new MethodInfo[methodCount];
    for (int i = 0; i < methodCount; i++) {
      int maf = u2();
      int mni = u2();
      int mdi = u2();
      int mac = u2();
      final AttributeInfo[] mais = new AttributeInfo[mac];
      for (int mi = 0; mi < mac; mi++) {
        mais[mi] = new AttributeInfo(u2(), raw(u4()));
      }
      methods[i] = new MethodInfo(maf, mni, mdi, mais);
    }
    // attributes
    final int attributesCount = u2();
    final AttributeInfo[] attributes = new AttributeInfo[attributesCount];
    for (int i = 0; i < attributesCount; i++) {
      attributes[i] = new AttributeInfo(u2(), raw(u4()));
    }

    return new ClassFile(magic,
                         minor,
                         major,
                         constantPoolCount,
                         cp,
                         access,
                         self,
                         parent,
                         interfaces,
                         fields,
                         methods,
                         attributes);
  }
}
