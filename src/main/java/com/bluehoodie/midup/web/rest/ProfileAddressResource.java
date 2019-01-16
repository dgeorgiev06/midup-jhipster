package com.bluehoodie.midup.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.bluehoodie.midup.service.ProfileAddressService;
import com.bluehoodie.midup.web.rest.errors.BadRequestAlertException;
import com.bluehoodie.midup.web.rest.util.HeaderUtil;
import com.bluehoodie.midup.web.rest.util.PaginationUtil;
import com.bluehoodie.midup.service.dto.ProfileAddressDTO;
import com.bluehoodie.midup.service.dto.ProfileAddressCriteria;
import com.bluehoodie.midup.service.ProfileAddressQueryService;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ProfileAddress.
 */
@RestController
@RequestMapping("/api")
public class ProfileAddressResource {

    private final Logger log = LoggerFactory.getLogger(ProfileAddressResource.class);

    private static final String ENTITY_NAME = "profileAddress";

    private ProfileAddressService profileAddressService;

    private ProfileAddressQueryService profileAddressQueryService;

    public ProfileAddressResource(ProfileAddressService profileAddressService, ProfileAddressQueryService profileAddressQueryService) {
        this.profileAddressService = profileAddressService;
        this.profileAddressQueryService = profileAddressQueryService;
    }

    /**
     * POST  /profile-addresses : Create a new profileAddress.
     *
     * @param profileAddressDTO the profileAddressDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new profileAddressDTO, or with status 400 (Bad Request) if the profileAddress has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/profile-addresses")
    @Timed
    public ResponseEntity<ProfileAddressDTO> createProfileAddress(@RequestBody ProfileAddressDTO profileAddressDTO) throws URISyntaxException {
        log.debug("REST request to save ProfileAddress : {}", profileAddressDTO);
        if (profileAddressDTO.getId() != null) {
            throw new BadRequestAlertException("A new profileAddress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProfileAddressDTO result = profileAddressService.save(profileAddressDTO);
        return ResponseEntity.created(new URI("/api/profile-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /profile-addresses : Updates an existing profileAddress.
     *
     * @param profileAddressDTO the profileAddressDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated profileAddressDTO,
     * or with status 400 (Bad Request) if the profileAddressDTO is not valid,
     * or with status 500 (Internal Server Error) if the profileAddressDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/profile-addresses")
    @Timed
    public ResponseEntity<ProfileAddressDTO> updateProfileAddress(@RequestBody ProfileAddressDTO profileAddressDTO) throws URISyntaxException {
        log.debug("REST request to update ProfileAddress : {}", profileAddressDTO);
        if (profileAddressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ProfileAddressDTO result = profileAddressService.save(profileAddressDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, profileAddressDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /profile-addresses : get all the profileAddresses.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of profileAddresses in body
     */
    @GetMapping("/profile-addresses")
    @Timed
    public ResponseEntity<List<ProfileAddressDTO>> getAllProfileAddresses(ProfileAddressCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProfileAddresses by criteria: {}", criteria);
        Page<ProfileAddressDTO> page = profileAddressQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/profile-addresses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
    * GET  /profile-addresses/count : count all the profileAddresses.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/profile-addresses/count")
    @Timed
    public ResponseEntity<Long> countProfileAddresses(ProfileAddressCriteria criteria) {
        log.debug("REST request to count ProfileAddresses by criteria: {}", criteria);
        return ResponseEntity.ok().body(profileAddressQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /profile-addresses/:id : get the "id" profileAddress.
     *
     * @param id the id of the profileAddressDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the profileAddressDTO, or with status 404 (Not Found)
     */
    @GetMapping("/profile-addresses/{id}")
    @Timed
    public ResponseEntity<ProfileAddressDTO> getProfileAddress(@PathVariable Long id) {
        log.debug("REST request to get ProfileAddress : {}", id);
        Optional<ProfileAddressDTO> profileAddressDTO = profileAddressService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profileAddressDTO);
    }

    /**
     * DELETE  /profile-addresses/:id : delete the "id" profileAddress.
     *
     * @param id the id of the profileAddressDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/profile-addresses/{id}")
    @Timed
    public ResponseEntity<Void> deleteProfileAddress(@PathVariable Long id) {
        log.debug("REST request to delete ProfileAddress : {}", id);
        profileAddressService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/profile-addresses?query=:query : search for the profileAddress corresponding
     * to the query.
     *
     * @param query the query of the profileAddress search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/profile-addresses")
    @Timed
    public ResponseEntity<List<ProfileAddressDTO>> searchProfileAddresses(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ProfileAddresses for query {}", query);
        Page<ProfileAddressDTO> page = profileAddressService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/profile-addresses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
