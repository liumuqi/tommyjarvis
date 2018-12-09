fn a_interface() {}

fn is_copy<T>(_val: T) where T: Copy {}

pub fn is_copy_main() {
    is_copy(true);
    is_copy('c');
    is_copy(42i8);
    is_copy(42i64);
    is_copy(42u64);
    is_copy(42isize);
    is_copy("hello");
    is_copy(a_interface);
    print!("test");
}