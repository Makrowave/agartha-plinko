package org.makrowave.agartha_plinko_backend.roulette.repository;

import org.makrowave.agartha_plinko_backend.shared.domain.model.RouletteGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRouletteGameRepository extends JpaRepository<RouletteGame, Long> {

}
