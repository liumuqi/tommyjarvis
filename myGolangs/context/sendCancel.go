package main

import (
	"time"
	"errors"
	"fmt"
	"context"
)

func operation1(ctx context.Context) error {
	// Let's assume that this operation failed for some reason
	// We use time.Sleep to simulate a resource intensive operation
	time.Sleep(100 * time.Millisecond)
	return errors.New("failed")
}
func operation2(ctx context.Context) {
	// We use a similar pattern to the HTTP server
	// that we saw in the earlier example
	select {
	case <-time.After(500 * time.Millisecond):
		fmt.Println("done")
	case <-ctx.Done():
		fmt.Println("halted operation2")
	}
}
func operation3(ctx context.Context) {
	// The context will be cancelled after 3 seconds
	// If it needs to be cancelled earlier, the `cancel` function can
	// be used, like before
	ctx, cancel := context.WithTimeout(ctx, 3*time.Second)
	// The context will be cancelled on 2009-11-10 23:00:00
	//ctx, cancel := context.WithDeadline(ctx, time.Date(2009, time.November, 10, 23, 0, 0, 0, time.UTC))
	select {
	case <-time.After(500 * time.Millisecond):
		fmt.Println("done")
		cancel()
	case <-ctx.Done():
		fmt.Println("halted operation2")
	}
}
func main() {
	// Create a new context
	ctx := context.Background()
	// Create a new context, with its cancellation function
	// from the original context
	ctx, cancel := context.WithCancel(ctx)
	// Run two operations: one in a different go routine
	go func() {
		err := operation1(ctx)
		// If this operation returns an error
		// cancel all operations using this context
		if err != nil {
			cancel()
		}
	}()
	// Run operation2 with the same context we use for operation1
	operation2(ctx)
}
