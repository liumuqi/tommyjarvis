crate thread_test;
use std::thread;

pub fn test_thread() {
    let v = vec![];
    let handle = thread::spawn(move || {
        println!("vector: {:?}", v);
    });
    handle.join();
}