import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {

  /**
   * javap
   */
  public static class Javap {

    private static void echo(String msg) {
      System.out.println(msg);
    }

    public static void main(String[] args) throws IOException {
      final File file = new File(args[0]);
      final FileInputStream fis = new FileInputStream(file);
      final byte[] bytes = fis.readAllBytes();
      final ClassReader reader = new ClassReader(bytes);
      final ClassFile cf = reader.read();
      final CpInfo[] cp = cf.cp;

      // basic
      echo("public class ".concat(Resolver.className(cf.thisClass, cp)));
      echo("  minor version: " + cf.minorVersion);
      echo("  major version: " + cf.majorVersion);
      {
        String flags = String.format("  flags: (0x%04x)", cf.accessFlags);
        boolean comm = false;
        if (Flags.isAccPublic(cf.accessFlags)) {
          flags = flags.concat(" ACC_PUBLIC");
          comm = true;
        }
        if (Flags.isAccSuper(cf.accessFlags)) {
          if (comm) {
            flags = flags.concat(", ACC_SUPER");
          } else {
            flags = flags.concat(" ACC_SUPER");
            comm = true;
          }
        }
        echo(flags);
      }
      {
        final String ts = String.format("  %-40s", "this_class: ".concat("#").concat(String.format("%d", cf.thisClass)))
            .concat("// ").concat(Resolver.className(cf.thisClass, cp));
        final String sc = String.format("  %-40s",
                                        "super_class: ".concat("#").concat(String.format("%d", cf.superClass)))
            .concat("// ").concat(Resolver.className(cf.superClass, cp));
        echo(ts);
        echo(sc);
      }
      {
        echo(String.format("  interfaces: %d, fields: %d, methods: %d, attributes: %d",
                           cf.interfaces.length,
                           cf.fields.length,
                           cf.methods.length,
                           cf.attributes.length));
      }
      // cp
      {
        echo("Constant pool:");
        int pad = 2;
        int tmp = cp.length;
        while (tmp / 10 > 0) {
          pad++;
          tmp = tmp / 10;
        }
        final String bif = String.format("%d", pad);
        for (int i = 1; i < cp.length; i++) {
          final CpInfo ci = cp[i];
          final String h = "  ".concat(String.format("%".concat(bif).concat("s"), String.format("#%d", i)))
              .concat(" = ");
          switch (ci.tag) {
            case Const.CONSTANT_Methodref -> {
              String cm = String.format("%-26s", h.concat("Methodref"));
              String t = String.format("#%d", Resolver.u2(ci.info));
              t = t.concat(String.format(".#%d", Resolver.u2(ci.info, 2)));
              cm = cm.concat(String.format("%-15s", t));
              cm = cm.concat("// ").concat(Resolver.className(Resolver.u2(ci.info), cp)).concat(".");
              String mn = Resolver.methodName(Resolver.u2(ci.info, 2), cp);
              if (mn.startsWith("<")) {
                mn = "\"".concat(mn).concat("\"");
              }
              cm = cm.concat(mn).concat(":").concat(Resolver.methodDescriptor(Resolver.u2(ci.info, 2), cp));
              echo(cm);
            }
            case Const.CONSTANT_Class -> {
              String cc = String.format("%-26s", h.concat("Class"));
              cc = cc.concat(String.format("%-15s", String.format("#%d", Resolver.u2(ci.info))));
              cc = cc.concat("// ").concat(Resolver.className(i, cp));

              echo(cc);
            }
            case Const.CONSTANT_NameAndType -> {
              String cm = String.format("%-26s", h.concat("NameAndType"));
              String t = String.format("#%d", Resolver.u2(ci.info));
              t = t.concat(String.format(":#%d", Resolver.u2(ci.info, 2)));
              cm = cm.concat(String.format("%-15s", t));

              String mn = Resolver.utf8(Resolver.u2(ci.info), cp);
              if (mn.startsWith("<")) {
                mn = "\"".concat(mn).concat("\"");
              }
              cm = cm.concat("// ").concat(mn).concat(":").concat(Resolver.utf8(Resolver.u2(ci.info, 2), cp));

              echo(cm);
            }
            case Const.CONSTANT_Utf8 -> {
              String cm = String.format("%-26s", h.concat("Utf8"));
              cm = cm.concat(Resolver.utf8(i, cp));
              echo(cm);
            }
          }
        }
      }
      {
        echo("{");
        boolean blank = false;
        // fields

        // methods
        {
          for (MethodInfo method : cf.methods) {
            var h = "";
            boolean b = false;
            if (Flags.isAccPublic(method.accessFlags)) {
              h = h.concat("public");
              b = true;
            }
            if (Flags.isAccStatic(method.accessFlags)) {
              if (b) {
                h = h.concat(" ").concat("static");
              } else {
                h = h.concat("static");
                b = true;
              }
            }

            final String descriptor = Resolver.utf8(method.descriptorIndex, cp);
            final String p = descriptor.substring(1, descriptor.indexOf(")"));
            final String r = descriptor.substring(descriptor.indexOf(")") + 1);

            if (!r.equals("V")) {
              boolean comm = false;
              char c = p.charAt(0);
              if (c == 'I') {
                h = h.concat(" int");
              }
            }

            final String name = Resolver.utf8(method.nameIndex, cp);
            if (name.equals("<init>")) {
              h = h.concat(" ").concat(Resolver.className(cf.thisClass, cp));
            } else {
              h = h.concat(" ").concat(name);
            }

            int as = 1;
            if (p.equals("")) {
              h = h.concat("()");
            } else {
              h = h.concat("(");
              boolean comm = false;
              for (int i = 0; i < p.length(); i++) {
                if (i > 0) {
                  comm = true;
                }
                char c = p.charAt(i);
                if (c == 'I') {
                  if (comm) {
                    h = h.concat(", ");
                  }
                  h = h.concat("int");
                }

                as++;
              }
              h = h.concat(")");
            }
            h = h.concat(";");

            if (blank) {
              echo("\n  ".concat(h));
            } else {
              blank = true;
              echo("  ".concat(h));
            }

            // --
            echo("    descriptor: ".concat(descriptor));
            {
              String flags = String.format("    flags: (0x%04x)", method.accessFlags);
              boolean comm = false;
              if (Flags.isAccPublic(method.accessFlags)) {
                flags = flags.concat(" ACC_PUBLIC");
                comm = true;
              }
              if (Flags.isAccStatic(method.accessFlags)) {
                if (comm) {
                  flags = flags.concat(", ");
                } else {
                  flags = flags.concat(" ");
                  comm = true;
                }
                flags = flags.concat("ACC_STATIC");
                as--;
              }
              echo(flags);
            }

            // code
            for (AttributeInfo attribute : method.attributes) {
              final String an = Resolver.utf8(attribute.attributeNameIndex, cp);
              if (an.equals("Code")) {
                final byte[] raw = attribute.info;
                int offset = 0;
                final int ms = Resolver.u2(raw, offset);
                offset += 2;
                final int ml = Resolver.u2(raw, offset);
                offset += 2;

                final int clen = Resolver.u4(raw, offset);
                offset += 4;
                byte[] code = Resolver.raw(raw, offset, clen);
                offset += clen;

                final int etl = Resolver.u2(raw, offset);
                offset += 2;
                // TODO exception table
                final int ac = Resolver.u2(raw, offset);
                offset += 2;
                final AttributeInfo[] cais = new AttributeInfo[ac];
                for (int i = 0; i < ac; i++) {
                  final int ni = Resolver.u2(raw, offset);
                  offset += 2;
                  final int al = Resolver.u4(raw, offset);
                  offset += 4;
                  final byte[] ab = Resolver.raw(raw, offset, al);
                  offset += al;
                  cais[i] = new AttributeInfo(ni, ab);
                }

                echo("    Code:");
                var ch = String.format("stack=%d, locals=%d, args_size=%d", ms, ml, as);
                echo("      ".concat(ch));

                // instructions
                int co = 0;
                final int cl = code.length;
                while (co < cl) {
                  int flag = code[co++] & 0xff;
                  switch (flag) {
                    case 0x2a -> {
                      echo("      %4d: %s".formatted(co, "aload_0"));
                    }
                    case 0xb7 -> {
                      final int isi = Resolver.u2(code, co);
                      final CpInfo ci = cp[isi];
                      String cm = "// Method ".concat(Resolver.className(Resolver.u2(ci.info), cp)).concat(".");
                      String mn = Resolver.methodName(Resolver.u2(ci.info, 2), cp);
                      if (mn.startsWith("<")) {
                        mn = "\"".concat(mn).concat("\"");
                      }
                      cm = cm.concat(mn).concat(":").concat(Resolver.methodDescriptor(Resolver.u2(ci.info, 2), cp));
                      echo("      %4d: %-34s".formatted(co, "invokespecial #%d".formatted(isi)).concat(cm));
                      co += 2;
                    }
                    case 0xb1 -> {
                      echo("      %4d: %s".formatted(co, "return"));
                    }

                    case 0x1a -> {
                      echo("      %4d: %s".formatted(co, "iload_0"));
                    }
                    case 0x1b -> {
                      echo("      %4d: %s".formatted(co, "iload_1"));
                    }
                    case 0x60 -> {
                      echo("      %4d: %s".formatted(co, "iadd"));
                    }
                    case 0xac -> {
                      echo("      %4d: %s".formatted(co, "ireturn"));
                      co++;
                    }

                    default -> {
                    }
                  }
                }

                // attributions
                for (AttributeInfo ai : cais) {
                  final String cn = Resolver.utf8(ai.attributeNameIndex, cp);
                  if (cn.equals("LineNumberTable")) {
                    echo("      ".concat("LineNumberTable:"));
                    int o = 0;
                    final int lntl = Resolver.u2(ai.info, o);
                    o += 2;
                    for (int i = 0; i < lntl; i++) {
                      final int sp = Resolver.u2(ai.info, o);
                      o += 2;
                      final int ln = Resolver.u2(ai.info, o);
                      o += 2;
                      echo("        ".concat("line %d: %d".formatted(ln, sp)));
                    }
                  }
                }
              }
            }
          }
        }
        echo("}");
      }

      // attributes
      {
        for (AttributeInfo attribute : cf.attributes) {
          final String name = Resolver.utf8(attribute.attributeNameIndex, cp);
          switch (name) {
            case "SourceFile" -> {
              final byte[] raw = attribute.info;
              echo(name.concat(": \"").concat(Resolver.utf8(Resolver.u2(raw), cp)).concat("\""));
            }
          }
        }
      }
    }

  }

  /**
   * java
   */
  public static class Java {

    public static void main(String[] args) throws IOException {
    }
  }
}
