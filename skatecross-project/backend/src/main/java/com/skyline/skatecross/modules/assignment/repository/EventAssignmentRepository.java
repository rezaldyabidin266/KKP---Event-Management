package com.skyline.skatecross.modules.assignment.repository;

import com.skyline.skatecross.modules.assignment.entity.EventAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAssignmentRepository extends JpaRepository<EventAssignment, Long> {
}