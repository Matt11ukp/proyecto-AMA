package unl.edu.ec.ama.engine.domain.user;

import unl.edu.ec.ama.data.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class UserSelectionMenu {

    private final List<User> users;
    private int selectedIndex;

    public UserSelectionMenu() {
        users = new ArrayList<>();
        selectedIndex = 0;
    }
public void removeSelectedUser() {
    if (isNewUserSelected()) {
        return;
    }

    users.remove(selectedIndex - 1);

    if (selectedIndex > users.size()) {
        selectedIndex = users.size();
    }
}
    public void addUser(User user) {
        users.add(user);
    }

    public void setUsers(List<User> loadedUsers) {
        users.clear();
        users.addAll(loadedUsers);
        selectedIndex = 0;
    }

    public void selectNext() {
        selectedIndex++;

        if (selectedIndex > users.size()) {
            selectedIndex = 0;
        }
    }

    public void selectPrevious() {
        selectedIndex--;

        if (selectedIndex < 0) {
            selectedIndex = users.size();
        }
    }

    public void selectLastUser() {
        selectedIndex = users.size();
    }

    public boolean isNewUserSelected() {
        return selectedIndex == 0;
    }

    public User getSelectedUser() {
        if (isNewUserSelected()) {
            return null;
        }

        return users.get(selectedIndex - 1);
    }

    public List<User> getUsers() {
        return users;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}