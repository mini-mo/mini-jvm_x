package cls;

import core.Resolver;

import static core.Const.*;

public class ClassReader {

  public static ClassFile read(String name, byte[] raw) {
    int cur = 0;
    var magic = Resolver.u4(raw);
    cur += 4;
    var minor = Resolver.u2(raw, cur);
    cur += 2;
    var major = Resolver.u2(raw, cur);
    cur += 2;
    var constantPoolCount = Resolver.u2(raw, cur);
    cur += 2;
    var cp = new CpInfo[constantPoolCount];

    // cp
    for (int i = 1; i < constantPoolCount; i++) {
      int tag = Resolver.u1(raw, cur++);
      switch (tag) {
        case CONSTANT_NameAndType, CONSTANT_Methodref, CONSTANT_Fieldref, CONSTANT_InterfaceMethodref -> {
          cp[i] = new CpInfo(
              tag,
              Resolver.raw(raw, cur, 4));
          cur += 4;
        }
        case CONSTANT_Class -> {
          cp[i] = new CpInfo(tag, Resolver.raw(raw, cur, 2));
          cur += 2;
        }
        case CONSTANT_Long -> {
          cp[i] = new CpInfo(tag, Resolver.raw(raw, cur, 8));
          cur += 8;
          i++;
        }
        case CONSTANT_Utf8 -> {
          var len = Resolver.u2(raw, cur);
          cur += 2;
          cp[i] = new CpInfo(tag, Resolver.raw(raw, cur, len));
          cur += len;
        }
        default -> {
          throw new RuntimeException("tag " + tag);
        }
      }
    }
    // end cp
    var access = Resolver.u2(raw, cur);
    cur += 2;
    var self = Resolver.u2(raw, cur);
    cur += 2;
    var parent = Resolver.u2(raw, cur);
    cur += 2;
    // interface
    var interfaceCount = Resolver.u2(raw, cur);
    cur += 2;
    var interfaces = new int[interfaceCount];
    for (int i = 0; i < interfaceCount; i++) {
      interfaces[i] = Resolver.u2(raw, cur);
      cur += 2;
    }

    // field
    var fieldCount = Resolver.u2(raw, cur);
    cur += 2;
    var fields = new FieldInfo[fieldCount];
    for (int i = 0; i < fieldCount; i++) {
      var faf = Resolver.u2(raw, cur);
      cur += 2;
      var fni = Resolver.u2(raw, cur);
      cur += 2;
      var fdi = Resolver.u2(raw, cur);
      cur += 2;
      var fac = Resolver.u2(raw, cur);
      cur += 2;
      var fais = new AttributeInfo[fac];
      for (int fi = 0; fi < fac; fi++) {
        var attributeNameIndex = Resolver.u2(raw, cur);
        cur += 2;
        var size = Resolver.u4(raw, cur);
        cur += 4;
        var rb = Resolver.raw(raw, cur, size);
        cur += size;
        fais[fi] = new AttributeInfo(attributeNameIndex, rb);
      }
      fields[i] = new FieldInfo(faf, fni, fdi, fais);
    }
    // method
    var methodCount = Resolver.u2(raw, cur);
    cur += 2;
    var methods = new MethodInfo[methodCount];
    for (int i = 0; i < methodCount; i++) {
      var maf = Resolver.u2(raw, cur);
      cur += 2;
      var mni = Resolver.u2(raw, cur);
      cur += 2;
      var mdi = Resolver.u2(raw, cur);
      cur += 2;
      var mac = Resolver.u2(raw, cur);
      cur += 2;
      var mais = new AttributeInfo[mac];
      for (var mi = 0; mi < mac; mi++) {
        var attributeNameIndex = Resolver.u2(raw, cur);
        cur += 2;
        var size = Resolver.u4(raw, cur);
        cur += 4;
        var rb = Resolver.raw(raw, cur, size);
        cur += size;
        mais[mi] = new AttributeInfo(attributeNameIndex, rb);
      }
      methods[i] = new MethodInfo(maf, mni, mdi, mais);
    }
    // attributes
    var attributesCount = Resolver.u2(raw, cur);
    cur += 2;
    var attributes = new AttributeInfo[attributesCount];
    for (int i = 0; i < attributesCount; i++) {
      var attributeNameIndex = Resolver.u2(raw, cur);
      cur += 2;
      var size = Resolver.u4(raw, cur);
      cur += 4;
      var rb = Resolver.raw(raw, cur, size);
      cur += size;
      attributes[i] = new AttributeInfo(attributeNameIndex, rb);
    }

    return new ClassFile(name, magic,
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
