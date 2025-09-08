package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

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
        if (!(userStorage.containsUser(userId) && userStorage.containsUser(friendId) &&
                !friendshipStorage.containsFriendship(userId, friendId) && userId != null && friendId != null)) {
            throw new NotFoundException("Пользователь с указанным id в базе не найден");
        } else {
            User user = userStorage.getUser(userId);
            Friendship newFriendship = friendshipStorage.createFriendship(new Friendship(userId, friendId));
            user.getFriendsIds().add(newFriendship.getId());
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        if (!(userStorage.containsUser(userId) && userStorage.containsUser(friendId))) {
            throw new NotFoundException("Пользователь с указанным id не найден в базе");
        } else if (!friendshipStorage.containsFriendship(userId, friendId)) {
            //Эти пользователи итак не друзья
            return;
        } else {
            User user = userStorage.getUser(userId);
            Friendship friendship = friendshipStorage.getFriendshipByIds(userId, friendId);
            user.getFriendsIds().remove(friendship.getId());
            friendshipStorage.deleteFriendship(friendship.getId());
        }
    }

    public List<User> getFriendList(Long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return friendshipStorage.getAllFriendship().stream()
                .filter(friendship -> friendship.getUserId() == userId)
                .map(friendship -> userStorage.getUser(friendship.getFriendId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {


        User user1 = userStorage.getUser(user1Id);
        User user2 = userStorage.getUser(user2Id);

        List<Friendship> allFriendships = friendshipStorage.getAllFriendship();


        Set<Long> user1FriendsIds = allFriendships.stream()
                .filter(friendship -> friendship.getUserId() == user1Id)
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());
        Set<Long> user2FriendsIds = allFriendships.stream()
                .filter(friendship -> friendship.getUserId() == user2Id)
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        user1FriendsIds.retainAll(user2FriendsIds);

        return user1FriendsIds.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }


}
