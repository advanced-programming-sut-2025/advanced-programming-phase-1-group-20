package org.example.models;

// this method is completed
public record Result(boolean success, String message) {
    public static Result success(String message) {
        return new Result(true, message);
    }

    public static Result error(String message) {
        return new Result(false, message);
    }
}
