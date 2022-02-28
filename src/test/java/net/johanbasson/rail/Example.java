package net.johanbasson.rail;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Example {

    public void createUser() {
        Result.success(new CreateUserRequest("john", "password"))
                .then(this::validate)
                .then(this::createUser)
                .then(this::persistUser)
                .onSuccess(user -> {
                    System.out.println("New User created: " + user);
                })
                .onFailure(error -> {
                    System.out.println("Error Occurred: " + error);
                });
    }

    private Result<User, Error> persistUser(User user) {
        try {
            // Insert into db
            return Result.success(user);
        } catch (Exception ex) {
            return Result.failure(new DatabaseError(ex));
        }
    }

    private Result<User, Error> createUser(CreateUserRequest req) {
        return Result.success(new User(UUID.randomUUID(), req.username, req.getPassword()));
    }

    private Result<CreateUserRequest, ValidationErrors> validate(CreateUserRequest req) {
        return Result.combine(
                        validateUsername(req.getUsername()),
                        validatePassword(req.getPassword())
                )
                .ap(CreateUserRequest::new)
                .mapError(ValidationErrors::new);
    }

    private Result<String, String> validateUsername(String username) {
        if (username == null || username.trim().length() == 0) {
            return Result.failure("Username cannot be empty");
        } else {
            return Result.failure(username);
        }
    }

    private Result<String, String> validatePassword(String password) {
        if (password == null || password.trim().length() == 0) {
            return Result.failure("Password cannot be empty");
        } else if (password.trim().length() < 10) {
            return Result.failure("Password cannot be less than 10 characters");
        } else {
            return Result.success(password);
        }
    }

    private abstract static class Error {

    }

    private static class DatabaseError extends Error {
        private final Throwable throwable;

        public DatabaseError(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        @Override
        public String toString() {
            return "DatabaseError{" +
                    "throwable=" + throwable +
                    '}';
        }
    }

    private static class ValidationErrors extends Error {
        private final ImmutableSet<String> errors;

        public ValidationErrors(ImmutableSet<String> errors) {
            this.errors = errors;
        }

        public Set<String> getErrors() {
            return errors;
        }

        @Override
        public String toString() {
            return "ValidationErrors{" +
                    "errors=" + errors +
                    '}';
        }
    }

    private static class CreateUserRequest {
        private final String username;
        private final String password;

        public CreateUserRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    private class User {
        private final UUID id;
        private final String username;
        private final String password;

        public User(UUID id, String email, String password) {
            this.id = id;
            this.username = email;
            this.password = password;
        }

        public UUID getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, username, password);
        }
    }
}
