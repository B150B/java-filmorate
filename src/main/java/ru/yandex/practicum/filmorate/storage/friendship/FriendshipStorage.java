package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

public interface FriendshipStorage {
    List<Friendship> getAllFriendship();

    Friendship getFriendship(Long id);

    Friendship deleteFriendship(Long id);

    List<Friendship> getFriendshipById(Long userId);

    Friendship getFriendshipByIds(Long userId, Long friendId);

    Friendship createFriendship(Friendship friendship);

    Friendship updateFriendship(Friendship friendship);

    boolean containsFriendship(Long userId, Long friendId);


}
