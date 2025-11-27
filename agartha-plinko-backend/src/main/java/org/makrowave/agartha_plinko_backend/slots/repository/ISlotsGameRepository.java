package org.makrowave.agartha_plinko_backend.slots.repository;

import org.makrowave.agartha_plinko_backend.shared.domain.model.SlotsGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISlotsGameRepository extends JpaRepository<SlotsGame, Long> {
}