pub use custom_thread::test_thread;

mod custom_thread;
mod other_thread;

pub fn cus_th() {
    custom_thread::test_thread();
}