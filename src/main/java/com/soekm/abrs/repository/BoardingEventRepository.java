package com.soekm.abrs.repository;

import com.soekm.abrs.entity.BoardingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author James Mwaura
 * 2026
 */

public interface BoardingEventRepository extends JpaRepository<BoardingEvent, Long> {
}
