package unl.edu.ec.ama.engine.domain.user;

import unl.edu.ec.ama.data.entity.User;
import unl.edu.ec.ama.engine.domain.entity.Player;

import java.util.List;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class UserProgressManager {

    private final UserRepository userRepository;
    private final List<User>     users;
    private final IAvatarConfig  avatar;   // ← interfaz, no clase concreta
    private final Player player;

    public UserProgressManager(UserRepository userRepository,
                                List<User> users,
                                IAvatarConfig avatar,   // ← inyección por interfaz
                                Player player) {
        this.userRepository = userRepository;
        this.users          = users;
        this.avatar         = avatar;
        this.player         = player;
    }

    public void saveProgress(User user) {
        if (user == null) return;
        saveAvatarProgress(user);
        userRepository.saveUsers(users);
    }

    public void applyProgress(User user) {
        if (user == null) return;
        applyAvatarProgress(user);
    }

    private void saveAvatarProgress(User user) {
        user.setSkinIndex(avatar.getActualSkin());
        user.setHairIndex(avatar.getActualHair());
        user.setShirtIndex(avatar.getActualShirt());
        user.setEyeIndex(avatar.getActualEye());
        user.setFemale(avatar.isGender());
    }

    private void applyAvatarProgress(User user) {
        avatar.setActualSkin(user.getSkinIndex());
        avatar.setActualHair(user.getHairIndex());
        avatar.setActualShirt(user.getShirtIndex());
        avatar.setActualEye(user.getEyeIndex());
        avatar.setGender(user.isFemale());
    }

}
