package org.makrowave.agartha_plinko_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Table
@Entity
@NoArgsConstructor
public class User {

    @Id
    Long userId;

    String username;

    String hash;

    BigDecimal balance;

    List<PlayedGame> gameHistory;
}
