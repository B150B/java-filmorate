package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }


    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }


    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriendList(Long userId) {
        return userStorage.getUser(userId).getFriends().stream()
                .map(userFriendId -> userStorage.getUser(userFriendId))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        User user1 = userStorage.getUser(user1Id);
        User user2 = userStorage.getUser(user2Id);

        Set<Long> user1FriendsIds = user1.getFriends();
        Set<Long> user2FriendsIds = user2.getFriends();

        return user1FriendsIds.stream()
                .filter(friendId -> user2FriendsIds.contains(friendId))
                .map(friendId -> userStorage.getUser(friendId))
                .collect(Collectors.toUnmodifiableList());
    }


}
