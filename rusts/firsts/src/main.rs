extern crate firsts;

use std::io;

fn main() {
    firsts::hello();
    println!("Hello, world!");
    let mut ss = String::new();
    let result = io::stdin().read_line(&mut ss);
    println!("result:{:?},your input:{}",result,ss)
}

