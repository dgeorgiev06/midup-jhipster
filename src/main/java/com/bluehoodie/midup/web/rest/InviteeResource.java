package com.bluehoodie.midup.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.bluehoodie.midup.service.InviteeService;
import com.bluehoodie.midup.web.rest.errors.BadRequestAlertException;
import com.bluehoodie.midup.web.rest.util.HeaderUtil;
import com.bluehoodie.midup.web.rest.util.PaginationUtil;
import com.bluehoodie.midup.service.dto.InviteeDTO;
import com.bluehoodie.midup.service.dto.InviteeCriteria;
import com.bluehoodie.midup.service.InviteeQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Invitee.
 */
@RestController
@RequestMapping("/api")
public class InviteeResource {

    private final Logger log = LoggerFactory.getLogger(InviteeResource.class);

    private static final String ENTITY_NAME = "invitee";

    private InviteeService inviteeService;

    private InviteeQueryService inviteeQueryService;

    public InviteeResource(InviteeService inviteeService, InviteeQueryService inviteeQueryService) {
        this.inviteeService = inviteeService;
        this.inviteeQueryService = inviteeQueryService;
    }

    /**
     * POST  /invitees : Create a new invitee.
     *
     * @param inviteeDTO the inviteeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new inviteeDTO, or with status 400 (Bad Request) if the invitee has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/invitees")
    @Timed
    public ResponseEntity<InviteeDTO> createInvitee(@RequestBody InviteeDTO inviteeDTO) throws URISyntaxException {
        log.debug("REST request to save Invitee : {}", inviteeDTO);
        if (inviteeDTO.getId() != null) {
            throw new BadRequestAlertException("A new invitee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InviteeDTO result = inviteeService.save(inviteeDTO);
        return ResponseEntity.created(new URI("/api/invitees/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * POST  /invitees : Create a new invitee.
     *
     * @param invitees the list of invitees to create
     * @return the ResponseEntity with status 201 (Created) and with body the new inviteeDTO list
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/invitees/invite")
    @Timed
    public ResponseEntity<List<InviteeDTO>> createInvitees(@RequestBody List<InviteeDTO> invitees) throws URISyntaxException {
        log.debug("REST request to save Invitees : {}", invitees);

        ArrayList<InviteeDTO> updatedInvitees = new ArrayList<>();

        for(InviteeDTO inviteeDTO : invitees) {
            if (inviteeDTO.getId() != null) {
                throw new BadRequestAlertException("A new invitee cannot already have an ID", ENTITY_NAME, "idexists");
            }
            InviteeDTO result = inviteeService.save(inviteeDTO);
            updatedInvitees.add(result);
        }

        return new ResponseEntity<>(updatedInvitees, HeaderUtil.createEntityCreationAlert(ENTITY_NAME, "invitees"), HttpStatus.OK);
    }

    /**
     * PUT  /invitees : Updates an existing invitee.
     *
     * @param inviteeDTO the inviteeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated inviteeDTO,
     * or with status 400 (Bad Request) if the inviteeDTO is not valid,
     * or with status 500 (Internal Server Error) if the inviteeDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/invitees")
    @Timed
    public ResponseEntity<InviteeDTO> updateInvitee(@RequestBody InviteeDTO inviteeDTO) throws URISyntaxException {
        log.debug("REST request to update Invitee : {}", inviteeDTO);
        if (inviteeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        InviteeDTO result = inviteeService.save(inviteeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, inviteeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /invitees : get all the invitees.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of invitees in body
     */
    @GetMapping("/invitees")
    @Timed
    public ResponseEntity<List<InviteeDTO>> getAllInvitees(InviteeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Invitees by criteria: {}", criteria);
        Page<InviteeDTO> page = inviteeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/invitees");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
    * GET  /invitees/count : count all the invitees.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/invitees/count")
    @Timed
    public ResponseEntity<Long> countInvitees(InviteeCriteria criteria) {
        log.debug("REST request to count Invitees by criteria: {}", criteria);
        return ResponseEntity.ok().body(inviteeQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /invitees/:id : get the "id" invitee.
     *
     * @param id the id of the inviteeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the inviteeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/invitees/{id}")
    @Timed
    public ResponseEntity<InviteeDTO> getInvitee(@PathVariable Long id) {
        log.debug("REST request to get Invitee : {}", id);
        Optional<InviteeDTO> inviteeDTO = inviteeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inviteeDTO);
    }

    /**
     * DELETE  /invitees/:id : delete the "id" invitee.
     *
     * @param id the id of the inviteeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/invitees/{id}")
    @Timed
    public ResponseEntity<Void> deleteInvitee(@PathVariable Long id) {
        log.debug("REST request to delete Invitee : {}", id);
        inviteeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/invitees?query=:query : search for the invitee corresponding
     * to the query.
     *
     * @param query the query of the invitee search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/invitees")
    @Timed
    public ResponseEntity<List<InviteeDTO>> searchInvitees(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Invitees for query {}", query);
        Page<InviteeDTO> page = inviteeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/invitees");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
