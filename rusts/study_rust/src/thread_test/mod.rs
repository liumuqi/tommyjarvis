mod custom_thread;

pub use  custom_thread::test_thread;

pub fn cus_th() {
    custom_thread::test_thread();
}