use std::fs::File;
use std::io::{BufRead, BufReader};

pub fn test_file_op() {
    let file = File::open("src/lib.rs").expect("file not found");
    let buf_reader = BufReader::new(file);

    for line in buf_reader.lines() {
        println!("{}", line.unwrap());
    };
// let x1 = file!("src/other_test.rs");

//    fs::create_dir("some_dir").expect("Can not create the directry!");
//    File::create("some_dir/empty.txt").expect("Can not create the file!");
}