package org.makrowave.agartha_plinko_backend.domain.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table
public class PlayedGame {
    @Id
    public Long id;



    public User user;
}
