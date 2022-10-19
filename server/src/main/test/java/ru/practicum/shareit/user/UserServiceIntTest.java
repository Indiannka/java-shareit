package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntTest {

    private final EntityManager em;
    private final UserService userService;

    private final User user = User.builder().name("u").email("user@email.ru").build();
    private final User emptyUser = User.builder().build();

    @BeforeEach
    void setUp() {
        em.persist(user);
    }

    @AfterEach
    void resetSetUp() {
        em.clear();
    }

    @Test
    void updateUserTest() {
        UserDto userDto = UserDto.builder().id(1L).name("newName").email("userDto@email.ru").build();
        userService.update(user.getId(), userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userRes = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(userRes.getName()).isEqualTo("newName");
        assertThat(userRes.getEmail()).isEqualTo("userDto@email.ru");
        assertThat(userRes).isNotEqualTo(emptyUser);

        int hashCode = userRes.hashCode();
        Assertions.assertThat(userRes.getClass().hashCode()).isEqualTo(hashCode);
    }
}