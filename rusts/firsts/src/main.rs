extern crate firsts;

use std::io;

fn main() {
    firsts::hello();
    println!("Hello, world!");
    let mut ss = String::new();
    let result = io::stdin().read_line(&mut ss);
    println!("result:{:?},your input:{}",result,ss)
}
#[no_mangle]
exten "C" fn fast_blank(buf: Buf) -> bool {
    buf.as_slice().chars().all(|c| c.is_whitespace())
}

