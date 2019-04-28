#[derive(Debug)]
struct User<'a> {
    username: &'a str,
    email: &'a str,
    sign_in_count: u64,
    active: bool,
}

#[test]
fn main_test_own_ship() {
    let user1 = User {
        email: "someone@example.com",
        username: "someusername123",
        active: true,
        sign_in_count: 1,
    };
    // 编译通过，4、5两行交换就编译不过。
    let mut i = 0;
    let r = &mut i;
    let rf: &mut u32 = r; // 和 let rf = r; 行为不同。
    *rf = 3;
    *r = 2;
}

#[test]
fn ship_main() {

    let b = foo("world");
    println!("b:{}", b);

}

fn foo(x: &str) -> String {
    let a = "Hello, ".to_string() + x;
    a
}