package com.boardcampapi.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.boardcampapi.api.models.RentalModel;

@Repository
public interface RentalRepository extends JpaRepository<RentalModel, Long> {
    @Query(
        value="SELECT COUNT(rentals.id)\r\n" + //
        "FROM rentals\r\n" + //
        "JOIN games ON rentals.game_id = games.id\r\n" + //
        "WHERE rentals.return_date IS NULL AND games.id = :gameId",
    nativeQuery = true)
    Long countOpenRentalsByGameId(@Param("gameId") Long gameId);
}
