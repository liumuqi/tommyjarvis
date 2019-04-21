use std::cell::Cell;
use std::sync::Mutex;

fn run_thread() {
    let m = Mutex::new(Cell::new(0));
    rayon::join(
        || {
            let mutex_guard = m.lock().unwrap();
            mutex_guard.set(mutex_guard.get() + 1);
            println!("Thread 1: {:?}", mutex_guard.get())
        },
        || {
            let mutex_guard = m.lock().unwrap();
            mutex_guard.set(mutex_guard.get() + 1);
            println!("Thread 2: {:?}", mutex_guard.get())
        },
    );
    rayon::join(
        || {
            let mutex_guard = m.lock().unwrap();
            mutex_guard.set(mutex_guard.get() + 1);
            println!("Thread 3: {:?}", mutex_guard.get())
        },
        || {
            let mutex_guard = m.lock().unwrap();
            mutex_guard.set(mutex_guard.get() + 1);
            println!("Thread 4: {:?}", mutex_guard.get())
        },
    );
}

#[test]
fn test_run_thread() {
    run_thread();
}