package com.bluehoodie.midup.web.rest;

import com.bluehoodie.midup.domain.User;
import com.bluehoodie.midup.security.SecurityUtils;
import com.bluehoodie.midup.service.UserProfileService;
import com.bluehoodie.midup.service.UserService;
import com.bluehoodie.midup.service.dto.UserProfileDTO;
import com.codahale.metrics.annotation.Timed;
import com.bluehoodie.midup.service.FriendService;
import com.bluehoodie.midup.web.rest.errors.BadRequestAlertException;
import com.bluehoodie.midup.web.rest.util.HeaderUtil;
import com.bluehoodie.midup.web.rest.util.PaginationUtil;
import com.bluehoodie.midup.service.dto.FriendDTO;
import com.bluehoodie.midup.service.dto.FriendCriteria;
import com.bluehoodie.midup.service.FriendQueryService;
import io.github.jhipster.service.filter.LongFilter;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Friend.
 */
@RestController
@RequestMapping("/api")
public class FriendResource {

    private final Logger log = LoggerFactory.getLogger(FriendResource.class);

    private static final String ENTITY_NAME = "friend";

    private FriendService friendService;

    private FriendQueryService friendQueryService;

    private UserService userService;

    private UserProfileService userProfileService;

    public FriendResource(FriendService friendService, FriendQueryService friendQueryService, UserService userService, UserProfileService userProfileService) {
        this.friendService = friendService;
        this.friendQueryService = friendQueryService;
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    /**
     * POST  /friends : Create a new friend.
     *
     * @param friendDTO the friendDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new friendDTO, or with status 400 (Bad Request) if the friend has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/friends")
    @Timed
    public ResponseEntity<FriendDTO> createFriend(@RequestBody FriendDTO friendDTO) throws URISyntaxException {
        log.debug("REST request to save Friend : {}", friendDTO);
        if (friendDTO.getId() != null) {
            throw new BadRequestAlertException("A new friend cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if(friendDTO.getFriendshipRequestDate() == null)
            friendDTO.setFriendshipRequestDate(Instant.now());

        friendDTO.setStatus("pending");
        FriendDTO result = friendService.save(friendDTO);
        return ResponseEntity.created(new URI("/api/friends/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * POST  /friends/request-friends : Request new friends.
     *
     * @param friends list of friendDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new friendDTO, or with status 400 (Bad Request) if the friend has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/friends/request-friends")
    @Timed
    public ResponseEntity<List<FriendDTO>> createFriends(@RequestBody List<FriendDTO> friends) throws URISyntaxException {
        log.debug("REST request to create friends : {}", friends);

        ArrayList<FriendDTO> friendsCreated = new ArrayList<>();

        for(FriendDTO friendDTO : friends) {
            if (friendDTO.getId() != null) {
                throw new BadRequestAlertException("A new friend cannot already have an ID", ENTITY_NAME, "idexists");
            }
            friendDTO.setStatus("pending");
            FriendDTO result = friendService.save(friendDTO);
            friendsCreated.add(result);
        }

        return new ResponseEntity<>(friendsCreated, HeaderUtil.createEntityCreationAlert(ENTITY_NAME, "friends"), HttpStatus.OK);
    }

    /**
     * PUT  /friends : Updates an existing friend.
     *
     * @param friendDTO the friendDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated friendDTO,
     * or with status 400 (Bad Request) if the friendDTO is not valid,
     * or with status 500 (Internal Server Error) if the friendDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/friends")
    @Timed
    public ResponseEntity<FriendDTO> updateFriend(@RequestBody FriendDTO friendDTO) throws URISyntaxException {
        log.debug("REST request to update Friend : {}", friendDTO);
        if (friendDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        FriendDTO result = friendService.save(friendDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, friendDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /friends : get all the friends.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of friends in body
     */
    @GetMapping("/friends")
    @Timed
    public ResponseEntity<List<FriendDTO>> getAllFriends(FriendCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Friends by criteria: {}", criteria);
        Page<FriendDTO> page = friendQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/friends");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /friends/my-friends : get all the friends.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of friends in body
     */
    @GetMapping("/friends/my-friends")
    @Timed
    public ResponseEntity<List<FriendDTO>> getMyFriends(Pageable pageable) {
        log.debug("REST request to get caller's Friends.");
        Optional<String> maybeLogin = SecurityUtils.getCurrentUserLogin();
        String login = maybeLogin.isPresent() ? maybeLogin.get() : null;

        if(login == null) {
            return ResponseUtil.wrapOrNotFound(null);
        }

        log.debug("REST request to get current UserProfile : {}", login);

        Optional<User> maybeUser = userService.findOneByLogin(login);

        if(!maybeUser.isPresent()) {
            return ResponseUtil.wrapOrNotFound(null);
        }

        Optional<UserProfileDTO> maybeUserProfileDTO = userProfileService.findOneByUserId(maybeUser.get().getId());

        if(!maybeUserProfileDTO.isPresent()) {
            return ResponseUtil.wrapOrNotFound(null);
        }

        LongFilter longFilter = new LongFilter();
        longFilter.setEquals(maybeUserProfileDTO.get().getId());

        FriendCriteria criteria = new FriendCriteria();
        criteria.setFriendAcceptingId(longFilter);
        criteria.setFriendRequestingId(longFilter);

        Page<FriendDTO> page = friendQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/friends");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /friends/for-user-profile : get all the friends.
     *
     * @param id user profile id
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of friends in body
     */
    @GetMapping("/friends/for-user-profile/{id}")
    @Timed
    public ResponseEntity<List<FriendDTO>> getFriendsForUserProfile(@PathVariable Long id, Pageable pageable) {
        log.debug("REST request to get friends for user {}.", id);

        LongFilter longFilter = new LongFilter();
        longFilter.setEquals(id);

        FriendCriteria criteria = new FriendCriteria();
        criteria.setFriendAcceptingId(longFilter);
        criteria.setFriendRequestingId(longFilter);

        Page<FriendDTO> page = friendQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/friends");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
    * GET  /friends/count : count all the friends.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/friends/count")
    @Timed
    public ResponseEntity<Long> countFriends(FriendCriteria criteria) {
        log.debug("REST request to count Friends by criteria: {}", criteria);
        return ResponseEntity.ok().body(friendQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /friends/:id : get the "id" friend.
     *
     * @param id the id of the friendDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the friendDTO, or with status 404 (Not Found)
     */
    @GetMapping("/friends/{id}")
    @Timed
    public ResponseEntity<FriendDTO> getFriend(@PathVariable Long id) {
        log.debug("REST request to get Friend : {}", id);
        Optional<FriendDTO> friendDTO = friendService.findOne(id);
        return ResponseUtil.wrapOrNotFound(friendDTO);
    }

    /**
     * DELETE  /friends/:id : delete the "id" friend.
     *
     * @param id the id of the friendDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/friends/{id}")
    @Timed
    public ResponseEntity<Void> deleteFriend(@PathVariable Long id) {
        log.debug("REST request to delete Friend : {}", id);
        friendService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/friends?query=:query : search for the friend corresponding
     * to the query.
     *
     * @param query the query of the friend search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/friends")
    @Timed
    public ResponseEntity<List<FriendDTO>> searchFriends(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Friends for query {}", query);
        Page<FriendDTO> page = friendService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/friends");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
