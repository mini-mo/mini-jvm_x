package cls;

import core.Resolver;
import java.io.File;
import java.io.FileInputStream;
import utils.Map;

public abstract class ClassLoader {

  private static Map<Clazz> bootClasses = new Map<>();

  // only dir
  private static String[] paths;

  public static boolean init(String bootClassPaths) {
    paths = bootClassPaths.split(":");
    return true;
  }

  public static Clazz findSystemClass(String name) {
    Clazz cache = bootClasses.get(name.getBytes());
    if (cache != null) {
      return cache;
    }

    Clazz cls = loadSystemClass(name);
    bootClasses.put(name.getBytes(), cls);
    return cls;
  }

  private static Clazz loadSystemClass(String name) {
    var cn = name.replace('.', '/');
    cn = cn.concat(".class");

    ClassFile cf = null;
    for (var path : paths) {
      var cp = path.concat("/").concat(cn);
      var f = new File(cp);
      if (!f.exists()) {
        continue;
      }
      try {
        var fis = new FileInputStream(f);
        var bytes = fis.readAllBytes();
        cf = ClassReader.read(bytes);
        break;
      } catch (Exception e) {
        e.printStackTrace();
        throw new IllegalStateException();
      }
    }

    if (cf == null) {
      throw new IllegalStateException("class not found, ".concat(name));
    }

    Clazz cls = defineClass(cf);
    return cls;
  }

  // initClass
  // linkClass

  public static Clazz defineClass(ClassFile cf) {
    Method[] methods = new Method[cf.methods.length];
    CpInfo[] cp = cf.cp;
    for (int i = 0; i < cf.methods.length; i++) {
      MethodInfo mi = cf.methods[i];
      Method m = new Method();
      m.accessFlags = mi.accessFlags;
      m.name = Resolver.utf8(mi.nameIndex, cp);
      m.descriptor = Resolver.utf8(mi.descriptorIndex, cp);

      // Code
      for (var attribute : mi.attributes) {
        final String name = Resolver.utf8(attribute.attributeNameIndex, cp);
        if (name.equals("Code")) {
          var raw = attribute.info;
          var offset = 0;
          var ms = Resolver.u2(raw, offset);
          offset += 2;
          var ml = Resolver.u2(raw, offset);
          offset += 2;

          var clen = Resolver.u4(raw, offset);
          offset += 4;
          byte[] code = Resolver.raw(raw, offset, clen);
          m.maxStacks = ms;
          m.maxLocals = ml;
          m.code = code;
        }
      }
      methods[i] = m;
    }

    // fields
    int size = 0;
    Field[] fields = new Field[cf.fields.length];
    for (int i = 0; i < cf.fields.length; i++) {
      FieldInfo fi = cf.fields[i];
      Field f = new Field();
      f.accessFlags = fi.accessFlags;
      f.name = Resolver.utf8(fi.nameIndex, cp);
      f.descriptor = Resolver.utf8(fi.descriptorIndex, cp);

      switch (f.descriptor) {
        case "I" -> {
          f.offset = size;
          size += 4;
        }
        default -> {
          throw new IllegalStateException();
        }
      }

      fields[i] = f;
    }

    return new Clazz(Resolver.className(cf.thisClass, cp), fields, methods, cp, size);
  }
}
