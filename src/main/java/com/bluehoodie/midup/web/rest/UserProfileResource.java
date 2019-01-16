package com.bluehoodie.midup.web.rest;

import com.bluehoodie.midup.domain.User;
import com.bluehoodie.midup.security.SecurityUtils;
import com.bluehoodie.midup.service.*;
import com.bluehoodie.midup.service.dto.FriendCriteria;
import com.bluehoodie.midup.service.dto.FriendDTO;
import com.cloudinary.Cloudinary;
import com.codahale.metrics.annotation.Timed;
import com.bluehoodie.midup.web.rest.errors.BadRequestAlertException;
import com.bluehoodie.midup.web.rest.util.HeaderUtil;
import com.bluehoodie.midup.web.rest.util.PaginationUtil;
import com.bluehoodie.midup.service.dto.UserProfileDTO;
import com.bluehoodie.midup.service.dto.UserProfileCriteria;
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
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.context.request.async.DeferredResult;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 * REST controller for managing UserProfile.
 */
@RestController
@RequestMapping("/api")
public class UserProfileResource {

    private final Logger log = LoggerFactory.getLogger(UserProfileResource.class);

    private static final String ENTITY_NAME = "userProfile";

    private UserProfileService userProfileService;

    private UserProfileQueryService userProfileQueryService;

    private UserService userService;

    private FriendQueryService friendQueryService;

    private CloudinaryService cloudinaryService;

    public UserProfileResource(UserProfileService userProfileService, UserProfileQueryService userProfileQueryService, UserService userService, FriendQueryService friendQueryService, CloudinaryService cloudinaryService) {
        this.userProfileService = userProfileService;
        this.userProfileQueryService = userProfileQueryService;
        this.userService = userService;
        this.friendQueryService = friendQueryService;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * POST  /user-profiles : Create a new userProfile.
     *
     * @param userProfileDTO the userProfileDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userProfileDTO, or with status 400 (Bad Request) if the userProfile has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user-profiles")
    @Timed
    public ResponseEntity<UserProfileDTO> createUserProfile(@RequestBody UserProfileDTO userProfileDTO) throws URISyntaxException {
        log.debug("REST request to save UserProfile : {}", userProfileDTO);
        if (userProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new userProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserProfileDTO result = userProfileService.save(userProfileDTO);
        return ResponseEntity.created(new URI("/api/user-profiles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-profiles : Updates an existing UserProfile.
     *
     * @param userProfileDTO the userProfileDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userProfileDTO,
     * or with status 400 (Bad Request) if the userProfileDTO is not valid,
     * or with status 500 (Internal Server Error) if the userProfileDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/user-profiles")
    @Timed
    public ResponseEntity<UserProfileDTO> updateUserProfile(@RequestBody UserProfileDTO userProfileDTO) throws URISyntaxException {
        log.debug("REST request to update UserProfile : {}", userProfileDTO);
        if (userProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserProfileDTO result = userProfileService.save(userProfileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, userProfileDTO.getId().toString()))
            .body(result);
    }


    /**
     * POST  /update/user-profiles : Updates an existing userProfile .
     *
     * @param image the MultipartFile to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userProfileDTO,
     * or with status 400 (Bad Request) if the userProfileDTO is not valid,
     * or with status 500 (Internal Server Error) if the userProfileDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping(value = "/update/user-profiles", consumes = "multipart/form-data")
    @Timed
    public DeferredResult<ResponseEntity<UserProfileDTO>> updateUserProfile(@RequestParam("image") MultipartFile image,
                                                            @RequestParam("profile") UserProfileDTO userProfileDTO) throws URISyntaxException {

        CompletableFuture<Map> response;

        DeferredResult<ResponseEntity<UserProfileDTO>> dr = new DeferredResult<>(10000l);

        dr.onError((throwable)->ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", throwable.getMessage())).body(null));

        dr.onTimeout(()->dr.setErrorResult(ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "Timeout processing image")).body(null)));

        try {
            response = cloudinaryService.processImage(image.getBytes(), 150, 150);

            response.whenCompleteAsync((result, throwable) -> {
                userProfileDTO.setImageUrl((String)result.get("secure_url"));

                try {
                    if (userProfileDTO.getId() == null) {
                        dr.setResult(this.createUserProfile(userProfileDTO));
                    } else {
                        UserProfileDTO dto = userProfileService.save(userProfileDTO);
                        dr.setResult(ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dto.getId().toString())).body(dto));
                    }
                }
                catch(Exception ex) {
                    dr.setErrorResult(ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", ex.getMessage())).body(null));
                }
                log.debug("REST request to create/update UserProfile: {}", userProfileDTO);
            });
        }
        catch(Exception ex) {
            log.error(ex.getMessage());
            dr.setErrorResult(ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", ex.getMessage())).body(null));
        }

        return dr;
    }

    /**
     * GET  /user-profiles : get all the userProfiles.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of userProfiles in body
     */
    @GetMapping("/user-profiles")
    @Timed
    public ResponseEntity<List<UserProfileDTO>> getAllUserProfiles(UserProfileCriteria criteria, Pageable pageable) {
        log.debug("REST request to get UserProfiles by criteria: {}", criteria);
        Page<UserProfileDTO> page = userProfileQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-profiles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /user-profiles/sans-friends : get all the userProfiles sans friends.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of userProfiles in body
     */
    @GetMapping("/user-profiles/sans-friends")
    @Timed
    public ResponseEntity<Set<UserProfileDTO>> getAllUserProfilesSansFriends(Pageable pageable) {
        Optional<String> maybeLogin = SecurityUtils.getCurrentUserLogin();
        String login = maybeLogin.isPresent() ? maybeLogin.get() : null;

        if(login == null) {
            return ResponseUtil.wrapOrNotFound(null);
        }

        log.debug("REST request to get user profiles of everyone in the system who is not a friend of current UserProfile : {}", login);

        Optional<User> maybeUser = userService.findOneByLogin(login);

        if(!maybeUser.isPresent()) {
            return ResponseUtil.wrapOrNotFound(null);
        }

        Optional<UserProfileDTO> maybeUserProfileDTO = userProfileService.findOneByUserId(maybeUser.get().getId());

        if(!maybeUserProfileDTO.isPresent()) {
            return ResponseUtil.wrapOrNotFound(null);
        }

        FriendCriteria friendCriteria = new FriendCriteria();
        LongFilter longFilter = new LongFilter();
        longFilter.setEquals(maybeUserProfileDTO.get().getId());

        friendCriteria.setFriendRequestingId(longFilter);
        friendCriteria.setFriendAcceptingId(longFilter);

        List<FriendDTO> friends = friendQueryService.findByCriteria(friendCriteria);

        Set<Long> friendIds = new HashSet<>();

        for(FriendDTO friend : friends) {
            friendIds.add(friend.getFriendAcceptingId());
            friendIds.add(friend.getFriendRequestingId());
        }

        Page<UserProfileDTO> page = userProfileQueryService.findByCriteria(null, pageable);

        List<UserProfileDTO> userProfiles = page.getContent();

        Set<UserProfileDTO> results = new HashSet<>();

        for(UserProfileDTO userProfileDTO : userProfiles) {
            if(!maybeUserProfileDTO.get().getId().equals(userProfileDTO.getId()) && !friendIds.contains(userProfileDTO.getId())) {
                results.add(userProfileDTO);
            }
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-profiles");
        return new ResponseEntity<>(results, headers, HttpStatus.OK);
    }

    /**
    * GET  /user-profiles/count : count all the userProfiles.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/user-profiles/count")
    @Timed
    public ResponseEntity<Long> countUserProfiles(UserProfileCriteria criteria) {
        log.debug("REST request to count UserProfiles by criteria: {}", criteria);
        return ResponseEntity.ok().body(userProfileQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /user-profiles/:id : get the "id" userProfile.
     *
     * @param id the id of the userProfileDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userProfileDTO, or with status 404 (Not Found)
     */
    @GetMapping("/user-profiles/{id}")
    @Timed
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long id) {
        log.debug("REST request to get UserProfile : {}", id);
        Optional<UserProfileDTO> userProfileDTO = userProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userProfileDTO);
    }

    /**
     * GET  /user-profiles/current-user : get the current user's userProfile.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the userProfileDTO, or with status 404 (Not Found)
     */
    @GetMapping("/user-profiles/current-user")
    @Timed
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {

        log.debug("REST request to get current UserProfile");
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

        Optional<UserProfileDTO> userProfileDTO = userProfileService.findOneByUserId(maybeUser.get().getId());

        return ResponseUtil.wrapOrNotFound(userProfileDTO);
    }

    /**
     * DELETE  /user-profiles/:id : delete the "id" userProfile.
     *
     * @param id the id of the userProfileDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-profiles/{id}")
    @Timed
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long id) {
        log.debug("REST request to delete UserProfile : {}", id);
        userProfileService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/user-profiles?query=:query : search for the userProfile corresponding
     * to the query.
     *
     * @param query the query of the userProfile search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/user-profiles")
    @Timed
    public ResponseEntity<List<UserProfileDTO>> searchUserProfiles(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of UserProfiles for query {}", query);
        Page<UserProfileDTO> page = userProfileService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/user-profiles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
