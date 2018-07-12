package main

import "google.golang.org/grpc"

func WithUnaryInterceptor(f grpc.UnaryClientInterceptor) {
}

func WithStreamInterceptor(f grpc.StreamClientInterceptor) {
}
