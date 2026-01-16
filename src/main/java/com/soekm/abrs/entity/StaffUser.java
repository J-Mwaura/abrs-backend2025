package com.soekm.abrs.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author James Mwaura
 * 2026
 */


@Entity
@Table(
        name = "staff_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_staff_username",
                        columnNames = "username"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String role; // CHECK_IN, GATE, ADMIN

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;
}

