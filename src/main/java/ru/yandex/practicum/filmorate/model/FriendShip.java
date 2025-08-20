package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendShip {
    private Long userId;
    private Long friendId;
    private boolean friendshipConfirmed;
}
