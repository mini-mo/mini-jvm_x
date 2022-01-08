# 实战 Rust

1. 文件读取全部字节

```rust
let mut f = File::open("misc/Hello.class").unwrap();
let len = f.metadata().unwrap().len();
let mut buf = Vec::<u8>::with_capacity(len as usize);
```

2. 字节迭代器

```rust
let mut seq = buf.into_iter();
```
