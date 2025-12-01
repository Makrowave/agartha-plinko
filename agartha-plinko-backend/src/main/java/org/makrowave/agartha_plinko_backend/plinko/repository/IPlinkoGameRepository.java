package org.makrowave.agartha_plinko_backend.plinko.repository;

import org.makrowave.agartha_plinko_backend.shared.domain.model.PlinkoGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPlinkoGameRepository extends JpaRepository<PlinkoGame, Long> {

}