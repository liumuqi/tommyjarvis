use libc::c_int;
use libc::c_void;
use libc::size_t;

///声明一个ffi库需要一个标记有#[link(name = "yourlib")]的extern块。name为对应的库(so/dll/dylib/a)的名字。 如：如果你需要snappy库(libsnappy.so/libsnappy.dll/libsnappy.dylib/libsnappy.a), 则对应的name为snappy。 在一个extern块中你可以声明任意多的函数和变量。
/// 声明完成后就可以进行调用了。 由于此函数来自外部的c库，所以rust并不能保证该函数的安全性。因此，调用任何一个ffi函数需要一个unsafe块。
/// 作为一个库作者，对外暴露不安全接口是一种非常不合格的做法。在做c库的rust binding时，我们做的最多的将是将不安全的c接口封装成一个安全接口。 通常做法是：在一个叫ffi.rs之类的文件中写上所有的extern块用以声明ffi函数。在一个叫wrapper.rs之类的文件中进行包装：
//ffi.rs
#[link(name = "yourlib")]
extern {
    // 声明ffi函数
    fn your_func(arg1: c_int, arg2: *mut c_void) -> size_t;
    fn your_func2(arg1: c_int, arg2: *mut c_void) -> size_t;
    // 声明ffi全局变量
    static ffi_global: c_int;
}

//wrapper.rs
fn your_func_wrapper(arg1: i32, arg2: &mut i32) -> isize {
    unsafe { your_func(1 as c_int, Box::into_raw(Box::new(3)) as *mut c_void) } as isize
}
