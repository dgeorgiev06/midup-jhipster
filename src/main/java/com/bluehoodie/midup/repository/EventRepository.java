package com.bluehoodie.midup.repository;

import com.bluehoodie.midup.domain.Event;
import com.bluehoodie.midup.domain.UserProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Event entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findAllByEventOrganizer(UserProfile eventOrganizer);
}
