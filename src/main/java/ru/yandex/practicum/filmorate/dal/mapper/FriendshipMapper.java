package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Friendship(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("friend_id"),
                rs.getBoolean("friendship_confirmed")
        );
    }
}
