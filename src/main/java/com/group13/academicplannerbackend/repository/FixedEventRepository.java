package com.group13.academicplannerbackend.repository;

import com.group13.academicplannerbackend.model.FixedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FixedEventRepository extends JpaRepository<FixedEvent, Long> {
    @Query("SELECT e FROM FixedEvent e WHERE ((e.startDate BETWEEN :firstDate AND :secondDate) OR (e.endDate BETWEEN :firstDate AND :secondDate)) AND e.isRepeat=false AND e.user.email = :email")
    List<FixedEvent> findAllNonRepeatingByStartDateOrEndDateBetweenDates(@Param("firstDate") LocalDate firstDate, @Param("secondDate") LocalDate secondDate, @Param("email") String email);

    @Query("SELECT e FROM FixedEvent e WHERE e.isRepeat=true AND e.repeatEvent.endDate >= :date AND e.user.email = :email")
    List<FixedEvent> findAllRepeatingByEndDateGreaterThanDate(@Param("date") LocalDate date, @Param("email") String email);

    FixedEvent findById(long id);
    void deleteFixedEventById(long id);
}
