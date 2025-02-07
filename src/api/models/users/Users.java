package api.models.users;

import java.util.Set;

public class Users {

    private final Set<User> users;

    public Users(Set<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }

    public User findUser(String email) {
        return this.users.parallelStream()
            .filter(user -> user.getEmail().equals(email))
            .findFirst()
            .orElse(null);
    }
}
