pub fn c_test() {
    let mut x = 1_i32;
//    fn inner_add() -> i32 {
//        x + 1
//    }
    let mut inner_add=move ||{ //加上move 原来的x还是1,不加就是3 move代表所有权转移
        x=x+2;
        x+1
    };
    let x2 = inner_add();
    println!("result {}", x2);
    println!("origin:x:{}",x);
}