use std::fs::File;
use std::io::Read;

use demo::cf::ClassFile;


pub struct Frame {
    pub locals: Vec<i32>,
    pub stacks: Vec<i32>,
    pub code: Vec<u8>,
}

// pub struct ExecEnv {
//     pub frames: Vec<Frame>,
// }

fn main() {
    let args: Vec<String> = std::env::args().collect();

    let cls = String::from("misc/{}.class").replace("{}", args.get(1).unwrap());
    // println!("{}", cls);

    let mut f = File::open(cls).unwrap();
    let len = f.metadata().unwrap().len();
    let mut buf = Vec::<u8>::with_capacity(len as usize);
    f.read_to_end(&mut buf).unwrap();

    //println!("{:?}", buf)

    // let mut seq = buf.into_iter();

    // println!("{:?}", seq.len());
    // println!("0x{:X}", U4::read(&mut seq));

    // let cf = demo::read(&mut seq);

    let cf = ClassFile::from(buf);

    // println!("{:?}\n-------------\n", cf);

    let m = cf.methods.get(1).unwrap();
    // println!("method: {:?}", m);
    // let s = demo::cf::resolve_utf8(m.name_index as usize, &cf.cp);
    // println!("method: {}", s);

    let mut locals:Vec<i32> = Vec::new();
    locals.resize(m.max_locals as usize, 0);
    let stacks:Vec<i32> = Vec::new();
    locals.resize(m.max_locals as usize, 0);
    // let mut locals = Vec::<i32>::with_capacity(m.max_locals as usize);
    // let mut stacks = Vec::<i32>::with_capacity(m.max_stacks as usize);

    // args.into_iter().for_each(|x| println!("{}", x));

    let p1 = args.get(2).unwrap().parse::<i32>().unwrap();
    let p2 = args.get(3).unwrap().parse::<i32>().unwrap();

    // println!("p {} {}", p1, p2);

    locals[0] = p1;
    locals[1] = p2;

    let code = m.code.clone();
    let mut f = Frame {
        locals,
        stacks,
        code,
    };

    let mut pc: i32 = 0;
    while (pc as usize) < f.code.len() {
        let opc = f.code[pc as usize];
        // println!("opc: {}", opc);
        pc += 1;
        match opc {
            3 => { // isonst0
                f.stacks.push(0);
            }
            26 => { // iload0
                f.stacks.push(f.locals[0])
            }
            27 => { // iload1
                f.stacks.push(f.locals[1])
            }
            28 => { // iload2
                f.stacks.push(f.locals[2])
            }
            29 => { // iload3
                f.stacks.push(f.locals[3])
            }
            61 => { // istore2
                f.locals[2] = f.stacks.pop().unwrap();
            }
            62 => { // istore3
                f.locals[3] = f.stacks.pop().unwrap();
            }
            96 => { // iadd
                let v = f.stacks.pop().unwrap() + f.stacks.pop().unwrap();
                f.stacks.push(v);
            }
            132 => { // iinc
                let idx = demo::cf::resolve_u1(&f.code, pc as usize);
                pc += 1;
                let step = demo::cf::resolve_s1(&f.code, pc as usize);
                pc += 1;
                f.locals[idx as usize] += step as i32;
            }
            163 => { // if_icmpgt
                let v2 = f.stacks.pop().unwrap();
                let v1 = f.stacks.pop().unwrap();
                let next = demo::cf::resolve_s2(&f.code, pc as usize);
                if v1 > v2 {
                    pc = pc + next as i32 - 1;
                    continue;
                }
                pc += 2;
            }
            167 => { // goto
                let offset = demo::cf::resolve_s2(&f.code, pc as usize);
                pc = pc + offset as i32 - 1;
            }
            172 => { // ireturn
                println!("{}", f.stacks.pop().unwrap());
                break;
            }
            _ => {
                println!("unknown opc : {}", opc);
                panic!("unknown opc");
            }
        }
    }
}
