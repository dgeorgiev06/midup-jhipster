package com.bluehoodie.midup.service;

import com.bluehoodie.midup.domain.Event;
import com.bluehoodie.midup.domain.Invitee;
import com.bluehoodie.midup.domain.UserProfile;
import com.bluehoodie.midup.repository.EventRepository;
import com.bluehoodie.midup.repository.InviteeRepository;
import com.bluehoodie.midup.repository.search.EventSearchRepository;
import com.bluehoodie.midup.repository.search.InviteeSearchRepository;
import com.bluehoodie.midup.service.dto.EventDTO;
import com.bluehoodie.midup.service.dto.InviteeDTO;
import com.bluehoodie.midup.service.mapper.EventMapper;
import com.bluehoodie.midup.service.mapper.InviteeMapper;
import org.apache.lucene.search.grouping.CollectedSearchGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Event.
 */
@Service
@Transactional
public class EventService {

    private final Logger log = LoggerFactory.getLogger(EventService.class);

    private EventRepository eventRepository;

    private EventMapper eventMapper;

    private EventSearchRepository eventSearchRepository;

    private InviteeRepository inviteeRepository;

    private InviteeMapper inviteeMapper;

    private InviteeSearchRepository inviteeSearchRepository;

    public EventService(EventRepository eventRepository,
                        EventMapper eventMapper,
                        EventSearchRepository eventSearchRepository,
                        InviteeRepository inviteeRepository,
                        InviteeMapper inviteeMapper,
                        InviteeSearchRepository inviteeSearchRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventSearchRepository = eventSearchRepository;
        this.inviteeRepository = inviteeRepository;
        this.inviteeMapper = inviteeMapper;
        this.inviteeSearchRepository = inviteeSearchRepository;
    }

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save
     * @return the persisted entity
     */
    public EventDTO save(EventDTO eventDTO) {
        log.debug("Request to save Event : {}", eventDTO);

        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);
        Set<InviteeDTO> inviteeDTOs = eventDTO.getInvitees();

        if(inviteeDTOs != null && inviteeDTOs.size() > 0) {
            Long eventId = event.getId();
            List<InviteeDTO> inviteeDTOList = inviteeDTOs.stream().map(inviteeDTO -> { inviteeDTO.setEventId(eventId); return inviteeDTO; }).collect(Collectors.toList());
            List<Invitee> invitees = inviteeMapper.toEntity(inviteeDTOList);
            List<Invitee> savedInvitees = inviteeRepository.saveAll(invitees);
            event.setInvitees(new HashSet<Invitee>(savedInvitees));
            inviteeSearchRepository.saveAll(savedInvitees);
        }

        EventDTO result = eventMapper.toDto(event);
        eventSearchRepository.save(event);
        return result;
    }

    /**
     * Get all the events.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<EventDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Events");
        return eventRepository.findAll(pageable)
            .map(eventMapper::toDto);
    }

    /**
     * Get all the events.
     *
     * @param eventOrganizer
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<EventDTO> findAllByEventOrganizer(UserProfile eventOrganizer) {
        log.debug("Request to get all Events by event organizer");
        List<Event> events = eventRepository.findAllByEventOrganizer(eventOrganizer);
        return eventMapper.toDto(events);
    }


    /**
     * Get one event by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<EventDTO> findOne(Long id) {
        log.debug("Request to get Event : {}", id);
        return eventRepository.findById(id)
            .map(eventMapper::toDto);
    }

    /**
     * Delete the event by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Event : {}", id);
        eventRepository.deleteById(id);
        eventSearchRepository.deleteById(id);
    }

    /**
     * Search for the event corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<EventDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Events for query {}", query);
        return eventSearchRepository.search(queryStringQuery(query), pageable)
            .map(eventMapper::toDto);
    }
}
