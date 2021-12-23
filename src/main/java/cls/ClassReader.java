package cls;

import static core.Const.*;

public class ClassReader {

  private final byte[] raw;
  private final int len;
  private int cur = 0;

  public ClassReader(byte[] raw) {
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
    var bytes = new byte[size];
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

  public ClassFile read() {
    var magic = u4();
    var minor = u2();
    var major = u2();

    var constantPoolCount = u2();
    var cp = new CpInfo[constantPoolCount];

    // cp
    for (int i = 1; i < constantPoolCount; i++) {
      int tag = u1();
      switch (tag) {
        case CONSTANT_NameAndType, CONSTANT_Methodref, CONSTANT_Fieldref, CONSTANT_InterfaceMethodref -> cp[i] = new CpInfo(
            tag,
            raw(4));
        case CONSTANT_Class -> cp[i] = new CpInfo(tag, raw(2));
        case CONSTANT_Utf8 -> {
          var len = u2();
          cp[i] = new CpInfo(tag, raw(len));
        }
        default -> {
          throw new RuntimeException("tag " + tag);
        }
      }
    }
    // end cp
    var access = u2();
    var self = u2();
    var parent = u2();

    // interface
    var interfaceCount = u2();
    var interfaces = new int[interfaceCount];
    for (int i = 0; i < interfaceCount; i++) {
      interfaces[i] = u2();
    }

    // field
    var fieldCount = u2();
    var fields = new FieldInfo[fieldCount];
    for (int i = 0; i < fieldCount; i++) {
      var faf = u2();
      var fni = u2();
      var fdi = u2();
      var fac = u2();
      var fais = new AttributeInfo[fac];
      for (int fi = 0; fi < fac; fi++) {
        fais[fi] = new AttributeInfo(u2(), raw(u4()));
      }
      fields[i] = new FieldInfo(faf, fni, fdi, fais);
    }
    // method
    var methodCount = u2();
    var methods = new MethodInfo[methodCount];
    for (int i = 0; i < methodCount; i++) {
      var maf = u2();
      var mni = u2();
      var mdi = u2();
      var mac = u2();
      var mais = new AttributeInfo[mac];
      for (var mi = 0; mi < mac; mi++) {
        mais[mi] = new AttributeInfo(u2(), raw(u4()));
      }
      methods[i] = new MethodInfo(maf, mni, mdi, mais);
    }
    // attributes
    var attributesCount = u2();
    var attributes = new AttributeInfo[attributesCount];
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
