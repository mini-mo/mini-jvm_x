# Mini-jvm_17

mini-jvm 重写版，使用 jdk17。

## 实现约束

- 只依赖 java.base
- 尽量少的依赖标准库
- 尽量简单的代码

## 开发环境

- jdk17 (zulu)
- macos arm

## 实现

### Javap

```bash

java -cp target/classes Javap misc/Hello.class
```

### Java

```bash

java -cp classes Java Hello 1 10
```

factorial

```bash

java -cp classes Java Factorial 10

```

loop for add to n

```bash

java -cp classes Java Loop 100000

```

basic obj

```bash

java -cp classes Java Obj
```