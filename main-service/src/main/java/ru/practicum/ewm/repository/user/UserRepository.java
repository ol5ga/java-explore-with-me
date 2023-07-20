package ru.practicum.ewm.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
