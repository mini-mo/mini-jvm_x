use std::fs::File;
use std::io::Read;
use demo::cf::ClassFile;


fn main() {
    let mut f = File::open("misc/Hello.class").unwrap();
    let len = f.metadata().unwrap().len();
    let mut buf = Vec::<u8>::with_capacity(len as usize);
    f.read_to_end(&mut buf).unwrap();

    //println!("{:?}", buf)

    // let mut seq = buf.into_iter();

    // println!("{:?}", seq.len());
    // println!("0x{:X}", U4::read(&mut seq));

    // let cf = demo::read(&mut seq);

    let cf = ClassFile::from(buf);

    println!("{:?}\n-------------\n", cf);


    println!("{:?}\n-------------\n", cf.methods.get(0));
}
