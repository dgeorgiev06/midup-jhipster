package com.bluehoodie.midup.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.bluehoodie.midup.domain.Event;
import com.bluehoodie.midup.domain.*; // for static metamodels
import com.bluehoodie.midup.repository.EventRepository;
import com.bluehoodie.midup.repository.search.EventSearchRepository;
import com.bluehoodie.midup.service.dto.EventCriteria;
import com.bluehoodie.midup.service.dto.EventDTO;
import com.bluehoodie.midup.service.mapper.EventMapper;

/**
 * Service for executing complex queries for Event entities in the database.
 * The main input is a {@link EventCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EventDTO} or a {@link Page} of {@link EventDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventQueryService extends QueryService<Event> {

    private final Logger log = LoggerFactory.getLogger(EventQueryService.class);

    private EventRepository eventRepository;

    private EventMapper eventMapper;

    private EventSearchRepository eventSearchRepository;

    public EventQueryService(EventRepository eventRepository, EventMapper eventMapper, EventSearchRepository eventSearchRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventSearchRepository = eventSearchRepository;
    }

    /**
     * Return a {@link List} of {@link EventDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EventDTO> findByCriteria(EventCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Event> specification = createSpecification(criteria);
        return eventMapper.toDto(eventRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EventDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EventDTO> findByCriteria(EventCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Event> specification = createSpecification(criteria);
        return eventRepository.findAll(specification, page)
            .map(eventMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EventCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Event> specification = createSpecification(criteria);
        return eventRepository.count(specification);
    }

    /**
     * Function to convert EventCriteria to a {@link Specification}
     */
    private Specification<Event> createSpecification(EventCriteria criteria) {
        Specification<Event> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Event_.id));
            }
            if (criteria.getEventName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEventName(), Event_.eventName));
            }
            if (criteria.getEventDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEventDate(), Event_.eventDate));
            }
            if (criteria.getVenueId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVenueId(), Event_.venueId));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Event_.address));
            }
            if (criteria.getVenueName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVenueName(), Event_.venueName));
            }
            if (criteria.getEventOrganizerId() != null) {
                specification = specification.and(buildSpecification(criteria.getEventOrganizerId(),
                    root -> root.join(Event_.eventOrganizer, JoinType.LEFT).get(UserProfile_.id)));
            }
            if (criteria.getInviteeId() != null) {
                specification = specification.and(buildSpecification(criteria.getInviteeId(),
                    root -> root.join(Event_.invitees, JoinType.LEFT).get(Invitee_.id)));
            }
        }
        return specification;
    }
}
