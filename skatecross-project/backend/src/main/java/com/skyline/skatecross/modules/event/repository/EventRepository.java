package com.skyline.skatecross.modules.event.repository;

import com.skyline.skatecross.modules.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByEventCode(String eventCode);
}