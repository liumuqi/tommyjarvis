use std::fmt;

// Custom error type; can be any type which defined in the current crate
// 💡 In here, we use a simple "unit struct" to simplify the example
struct AppError;

// Implement std::fmt::Display for AppError
impl fmt::Display for AppError {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "An Error Occurred, Please Try Again!") // user-facing output
    }
}

// Implement std::fmt::Debug for AppError
impl fmt::Debug for AppError {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ file: {}, line: {} }}", file!(), line!()) // programmer-facing output
    }
}

// A sample function to produce an AppError Err
fn produce_error() -> Result<(), AppError> {
    Err(AppError)
}

pub fn main() {
    match produce_error() {
        Err(e) => eprintln!("{}", e), // An Error Occurred, Please Try Again!
        _ => println!("No error"),
    }

    eprintln!("{:?}", produce_error()); // Err({ file: src/main.rs, line: 17 })
}
