package com.bluehoodie.midup.web.rest;

import com.bluehoodie.midup.service.CloudinaryService;
import com.bluehoodie.midup.service.InviteeService;
import com.bluehoodie.midup.service.dto.InviteeDTO;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.codahale.metrics.annotation.Timed;
import com.bluehoodie.midup.service.EventService;
import com.bluehoodie.midup.web.rest.errors.BadRequestAlertException;
import com.bluehoodie.midup.web.rest.util.HeaderUtil;
import com.bluehoodie.midup.web.rest.util.PaginationUtil;
import com.bluehoodie.midup.service.dto.EventDTO;
import com.bluehoodie.midup.service.dto.EventCriteria;
import com.bluehoodie.midup.service.EventQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.Cloud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Event.
 */
@RestController
@RequestMapping("/api")
public class EventResource {

    private final Logger log = LoggerFactory.getLogger(EventResource.class);

    private static final String ENTITY_NAME = "event";

    private EventService eventService;

    private EventQueryService eventQueryService;

    private CloudinaryService cloudinaryService;

    public EventResource(EventService eventService, EventQueryService eventQueryService, CloudinaryService cloudinaryService) {
        this.eventService = eventService;
        this.eventQueryService = eventQueryService;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * POST  /events : Create a new event.
     *
     * @param eventDTO the eventDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new eventDTO, or with status 400 (Bad Request) if the event has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/events")
    @Timed
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) throws URISyntaxException {
        log.debug("REST request to save Event : {}", eventDTO);
        if (eventDTO.getId() != null) {
            throw new BadRequestAlertException("A new event cannot already have an ID", ENTITY_NAME, "idexists");
        }

        EventDTO result = eventService.save(eventDTO);

        return ResponseEntity.created(new URI("/api/events/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * POST  /update/event : Updates an existing userProfile .
     *
     * @param image the MultipartFile to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userProfileDTO,
     * or with status 400 (Bad Request) if the userProfileDTO is not valid,
     * or with status 500 (Internal Server Error) if the userProfileDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping(value = "/update/event", consumes = "multipart/form-data")
    @Timed
    public DeferredResult<ResponseEntity<EventDTO>> updateEventMultipart(@RequestParam("image") MultipartFile image,
                                                         @RequestParam("event") EventDTO eventDTO) throws URISyntaxException {

        CompletableFuture<Map> response;

        DeferredResult<ResponseEntity<EventDTO>> dr = new DeferredResult<>(10000l);

        dr.onError((throwable)->ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", throwable.getMessage())).body(null));

        dr.onTimeout(()->dr.setErrorResult(ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "Timeout processing image")).body(null)));

        try {
            response = cloudinaryService.processImage(image.getBytes(), 600, 400);

            response.whenCompleteAsync((result, throwable) -> {
                eventDTO.setImageUrl((String)result.get("secure_url"));

                try {
                    if (eventDTO.getId() == null) {
                        dr.setResult(this.createEvent(eventDTO));
                    } else {
                        EventDTO dto = eventService.save(eventDTO);
                        dr.setResult(ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dto.getId().toString())).body(dto));
                    }
                }
                catch(Exception ex) {
                    dr.setErrorResult(ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", ex.getMessage())).body(null));
                }
                log.debug("REST request to create/update Event: {}", eventDTO);
            });
        }
        catch(Exception ex) {
            log.error(ex.getMessage());
            dr.setErrorResult(ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", ex.getMessage())).body(null));
        }

        return dr;

    }

    /**
     * PUT  /events : Updates an existing event.
     *
     * @param eventDTO the eventDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated eventDTO,
     * or with status 400 (Bad Request) if the eventDTO is not valid,
     * or with status 500 (Internal Server Error) if the eventDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/events")
    @Timed
    public ResponseEntity<EventDTO> updateEvent(@RequestBody EventDTO eventDTO) throws URISyntaxException {
        log.debug("REST request to update Event : {}", eventDTO);
        if (eventDTO.getId() == null) {
            return this.createEvent(eventDTO);
        }
        EventDTO result = eventService.save(eventDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, eventDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /events : get all the events.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of events in body
     */
    @GetMapping("/events")
    @Timed
    public ResponseEntity<List<EventDTO>> getAllEvents(EventCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Events by criteria: {}", criteria);
        Page<EventDTO> page = eventQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/events");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
    * GET  /events/count : count all the events.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/events/count")
    @Timed
    public ResponseEntity<Long> countEvents(EventCriteria criteria) {
        log.debug("REST request to count Events by criteria: {}", criteria);
        return ResponseEntity.ok().body(eventQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /events/:id : get the "id" event.
     *
     * @param id the id of the eventDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the eventDTO, or with status 404 (Not Found)
     */
    @GetMapping("/events/{id}")
    @Timed
    public ResponseEntity<EventDTO> getEvent(@PathVariable Long id) {
        log.debug("REST request to get Event : {}", id);
        Optional<EventDTO> eventDTO = eventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventDTO);
    }

    /**
     * DELETE  /events/:id : delete the "id" event.
     *
     * @param id the id of the eventDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/events/{id}")
    @Timed
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        log.debug("REST request to delete Event : {}", id);
        eventService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/events?query=:query : search for the event corresponding
     * to the query.
     *
     * @param query the query of the event search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/events")
    @Timed
    public ResponseEntity<List<EventDTO>> searchEvents(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Events for query {}", query);
        Page<EventDTO> page = eventService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/events");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
