use std::intrinsics::transmute;

#[derive(Debug, Clone)]
pub struct ConstantItem {
    pub tag: u8,
    pub raw: Vec<u8>,
}

#[derive(Debug, Clone)]
pub struct FieldInfo {
    pub access_flags: u16,
    pub name_index: u16,
    pub descriptor_index: u16,
    pub attributes: Vec<Attribute>,
}

#[derive(Debug, Clone)]
pub struct MethodInfo {
    pub access_flags: u16,
    pub name_index: u16,
    pub descriptor_index: u16,
    pub attributes: Vec<Attribute>,

    pub max_locals: u16,
    pub max_stacks: u16,
    pub code: Vec<u8>,
}

#[derive(Debug, Clone)]
pub struct Code {
    pub name_index: u16,
    pub raw: Vec<u8>,
}

#[derive(Debug, Clone)]
pub struct Attribute {
    pub name_index: u16,
    pub raw: Vec<u8>,
}

#[derive(Debug, Clone)]
pub struct ClassFile {
    pub magic: u32,
    pub minor_version: u16,
    pub major_version: u16,
    pub constant_pool_count: u16,
    pub cp: Vec<ConstantItem>,
    pub access_flags: u16,
    pub this_class: u16,
    pub super_class: u16,
    pub interfaces_count: u16,
    pub interfaces: Vec<u16>,
    pub fields_count: u16,
    pub fields: Vec<FieldInfo>,
    pub methods_count: u16,
    pub methods: Vec<MethodInfo>,
    pub attributes_count: u16,
    pub attributes: Vec<Attribute>,
}

pub fn resolve_u4(bytes: &[u8], offset: usize) -> u32 {
    let u4: u8 = bytes[offset];
    let u3: u8 = bytes[offset + 1];
    let u2: u8 = bytes[offset + 2];
    let u1: u8 = bytes[offset + 3];
    let buf = [u1, u2, u3, u4];
    unsafe { transmute::<[u8; 4], u32>(buf) }
}

pub fn resolve_u2(bytes: &[u8], offset: usize) -> u16 {
    let u2: u8 = bytes[offset];
    let u1: u8 = bytes[offset + 1];
    let buf = [u1, u2];
    unsafe { transmute::<[u8; 2], u16>(buf) }
}

pub fn resolve_s2(bytes: &[u8], offset: usize) -> i16 {
    let u2: u8 = bytes[offset];
    let u1: u8 = bytes[offset + 1];
    let buf = [u1, u2];
    unsafe { transmute::<[u8; 2], i16>(buf) }
}

pub fn resolve_u1(bytes: &[u8], offset: usize) -> u8 {
    return bytes[offset];
}

pub fn resolve_s1(bytes: &[u8], offset: usize) -> i8 {
    return (bytes[offset] & 0xff) as i8;
}

pub fn resolve_raw(bytes: &[u8], offset: usize, buf: &mut Vec<u8>) -> usize {
    let len = buf.capacity();
    for i in 0..len {
        buf.push(bytes[offset + i]);
    }
    len
}

pub fn resolve_utf8(index: usize, cp: &Vec<ConstantItem>) -> String {
    let ci = cp.get(index).unwrap();
    let s = std::str::from_utf8(&ci.raw);
    s.unwrap().to_string()
}

impl ClassFile {
    pub fn from(bytes: Vec<u8>) -> ClassFile {
        let mut cur: usize = 0;
        let magic = resolve_u4(&bytes, cur);
        cur += 4;

        let minor_version = resolve_u2(&bytes, cur);
        cur += 2;
        let major_version = resolve_u2(&bytes, cur);
        cur += 2;
        let cpc = resolve_u2(&bytes, cur);
        cur += 2;
        let mut cp = Vec::<ConstantItem>::with_capacity(cpc as usize);
        cp.push(ConstantItem { tag: 0, raw: vec![] }); // placeholder

        for _i in 0..(cpc as usize) - 1 {
            let tag = resolve_u1(&bytes, cur);
            cur += 1;
            match tag {
                10 => {
                    let mut buf = Vec::<u8>::with_capacity(4 as usize);
                    resolve_raw(&bytes, cur, &mut buf);
                    cur += 4;

                    let item = ConstantItem { tag, raw: buf };
                    // println!("{:?}", item);
                    cp.push(item);
                }
                12 => {
                    let mut buf = Vec::<u8>::with_capacity(4 as usize);
                    resolve_raw(&bytes, cur, &mut buf);
                    cur += 4;

                    let item = ConstantItem { tag, raw: buf };
                    // println!("{:?}", item);
                    cp.push(item);
                }
                7 => {
                    let mut buf = Vec::<u8>::with_capacity(2 as usize);
                    resolve_raw(&bytes, cur, &mut buf);
                    cur += 2;

                    let item = ConstantItem { tag, raw: buf };
                    // println!("{:?}", item);
                    cp.push(item);
                }
                1 => {
                    let len = resolve_u2(&bytes, cur);
                    cur += 2;

                    let mut buf = Vec::<u8>::with_capacity(len as usize);
                    resolve_raw(&bytes, cur, &mut buf);
                    cur += len as usize;

                    let item = ConstantItem { tag, raw: buf };
                    // println!("{:?}", item);
                    cp.push(item);
                }
                _ => {
                    println!("{}", tag);
                    panic!("unknown tag");
                }
            }
        }

        let access_flags = resolve_u2(&bytes, cur);
        cur += 2;
        let this_class = resolve_u2(&bytes, cur);
        cur += 2;
        let super_class = resolve_u2(&bytes, cur);
        cur += 2;

        let interfaces_count = resolve_u2(&bytes, cur);
        cur += 2;
        // interfaces
        let interfaces = Vec::<u16>::with_capacity(interfaces_count as usize);

        let fields_count = resolve_u2(&bytes, cur);
        cur += 2;
        let mut fields = Vec::<FieldInfo>::with_capacity(fields_count as usize);

        for _ in 0..(fields_count as usize) {
            let af = resolve_u2(&bytes, cur);
            cur += 2;
            let ni = resolve_u2(&bytes, cur);
            cur += 2;
            let di = resolve_u2(&bytes, cur);
            cur += 2;
            let ac = resolve_u2(&bytes, cur);
            cur += 2;

            let mut fas = Vec::<Attribute>::with_capacity(ac as usize);

            for _ in 0..ac {
                let ani = resolve_u2(&bytes, cur);
                cur += 2;
                let size = resolve_u4(&bytes, cur);
                cur += 4;

                let mut buf = Vec::<u8>::with_capacity(size as usize);
                resolve_raw(&bytes, cur, &mut buf);
                cur += size as usize;

                let ma = Attribute { name_index: ani, raw: buf };
                fas.push(ma);
            }

            let f = FieldInfo {
                access_flags: af,
                name_index: ni,
                descriptor_index: di,
                attributes: fas,
            };

            fields.push(f);
        }

        // methods
        let methods_count = resolve_u2(&bytes, cur);
        cur += 2;
        let mut methods = Vec::<MethodInfo>::with_capacity(methods_count as usize);

        for _ in 0..(methods_count as usize) {
            let af = resolve_u2(&bytes, cur);
            cur += 2;
            let ni = resolve_u2(&bytes, cur);
            cur += 2;
            let di = resolve_u2(&bytes, cur);
            cur += 2;
            let ac = resolve_u2(&bytes, cur);
            cur += 2;

            let mut mas = Vec::<Attribute>::with_capacity(ac as usize);

            for _ in 0..ac {
                let ani = resolve_u2(&bytes, cur);
                cur += 2;
                let size = resolve_u4(&bytes, cur);
                cur += 4;

                let mut buf = Vec::<u8>::with_capacity(size as usize);
                resolve_raw(&bytes, cur, &mut buf);
                cur += size as usize;

                let ma = Attribute { name_index: ani, raw: buf };
                mas.push(ma);
            }

            let mut max_locals: u16 = 0;
            let mut max_stacks: u16 = 0;
            let mut code: Vec<u8> = Vec::new();
            for i in 0..ac as usize {
                let ma = mas.get(i).unwrap();
                // println!("{:?}", ma);
                let name = resolve_utf8(ma.name_index as usize, &cp);
                // println!("{}", name);
                if name.eq(&String::from("Code")) {
                    let raw = ma.raw.clone();
                    max_stacks = resolve_u2(&raw, 0);
                    max_locals= resolve_u2(&raw, 2);
                    let len = resolve_u4(&raw, 4) as usize;
                    code = Vec::with_capacity(len);
                    resolve_raw(&raw, 8, &mut code);
                }
            }

            let m = MethodInfo {
                access_flags: af,
                name_index: ni,
                descriptor_index: di,
                attributes: mas,
                max_locals,
                max_stacks,
                code,
            };

            methods.push(m);
        }


        let attributes_count = resolve_u2(&bytes, cur);
        cur += 2;

        let mut attributes = Vec::<Attribute>::with_capacity(attributes_count as usize);

        for _ in 0..attributes_count {
            let ani = resolve_u2(&bytes, cur);
            cur += 2;
            let size = resolve_u4(&bytes, cur);
            cur += 4;

            let mut buf = Vec::<u8>::with_capacity(size as usize);
            resolve_raw(&bytes, cur, &mut buf);
            cur += size as usize;

            let a = Attribute { name_index: ani, raw: buf };
            attributes.push(a);
        }

        ClassFile {
            magic,
            minor_version,
            major_version,
            constant_pool_count: cpc,
            cp,
            access_flags,
            this_class,
            super_class,
            interfaces_count,
            interfaces,
            fields_count,
            fields,
            methods_count,
            methods,
            attributes_count,
            attributes,
        }
    }
}

