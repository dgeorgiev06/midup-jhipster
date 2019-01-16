package com.bluehoodie.midup.service;

import com.bluehoodie.midup.domain.Friend;
import com.bluehoodie.midup.repository.FriendRepository;
import com.bluehoodie.midup.repository.search.FriendSearchRepository;
import com.bluehoodie.midup.service.dto.FriendDTO;
import com.bluehoodie.midup.service.mapper.FriendMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Friend.
 */
@Service
@Transactional
public class FriendService {

    private final Logger log = LoggerFactory.getLogger(FriendService.class);

    private FriendRepository friendRepository;

    private FriendMapper friendMapper;

    private FriendSearchRepository friendSearchRepository;

    public FriendService(FriendRepository friendRepository, FriendMapper friendMapper, FriendSearchRepository friendSearchRepository) {
        this.friendRepository = friendRepository;
        this.friendMapper = friendMapper;
        this.friendSearchRepository = friendSearchRepository;
    }

    /**
     * Save a friend.
     *
     * @param friendDTO the entity to save
     * @return the persisted entity
     */
    public FriendDTO save(FriendDTO friendDTO) {
        log.debug("Request to save Friend : {}", friendDTO);

        if(friendDTO.getFriendshipRequestDate() == null) {
            friendDTO.setFriendshipRequestDate(Instant.now());
        }

        Friend friend = friendMapper.toEntity(friendDTO);
        friend = friendRepository.save(friend);
        FriendDTO result = friendMapper.toDto(friend);
        friendSearchRepository.save(friend);
        return result;
    }

    /**
     * Get all the friends.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<FriendDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Friends");
        return friendRepository.findAll(pageable)
            .map(friendMapper::toDto);
    }


    /**
     * Get one friend by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<FriendDTO> findOne(Long id) {
        log.debug("Request to get Friend : {}", id);
        return friendRepository.findById(id)
            .map(friendMapper::toDto);
    }

    /**
     * Delete the friend by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Friend : {}", id);
        friendRepository.deleteById(id);
        friendSearchRepository.deleteById(id);
    }

    /**
     * Search for the friend corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<FriendDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Friends for query {}", query);
        return friendSearchRepository.search(queryStringQuery(query), pageable)
            .map(friendMapper::toDto);
    }
}
