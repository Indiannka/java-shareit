package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

@Table(name = "users")
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 512)
    private String name;

    @Column(nullable = false, length = 512)
    private String email;
}
