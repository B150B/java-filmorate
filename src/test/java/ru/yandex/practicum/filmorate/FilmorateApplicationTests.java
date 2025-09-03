package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.Friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class FilmorateApplicationTests {

	private final UserDbStorage userDbStorage;
	private final JdbcTemplate jdbcTemplate;

	@MockBean
	@Qualifier("friendshipDbStorage")
	private FriendshipStorage friendshipStorage;

	@BeforeEach
	void setUp() {
		jdbcTemplate.update("INSERT INTO users (id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
				1, "user1@mail.ru", "user1", "User Userov", LocalDate.of(1992,7,5));
	}

	@Test
	public void testFindUserById() {
		Optional<User> userOptional = Optional.ofNullable(userDbStorage.getUser(Long.valueOf(1)));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id",1L)
				);
	}



}
