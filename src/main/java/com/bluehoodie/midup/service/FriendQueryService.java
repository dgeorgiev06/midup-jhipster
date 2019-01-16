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

import com.bluehoodie.midup.domain.Friend;
import com.bluehoodie.midup.domain.*; // for static metamodels
import com.bluehoodie.midup.repository.FriendRepository;
import com.bluehoodie.midup.repository.search.FriendSearchRepository;
import com.bluehoodie.midup.service.dto.FriendCriteria;
import com.bluehoodie.midup.service.dto.FriendDTO;
import com.bluehoodie.midup.service.mapper.FriendMapper;

/**
 * Service for executing complex queries for Friend entities in the database.
 * The main input is a {@link FriendCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FriendDTO} or a {@link Page} of {@link FriendDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FriendQueryService extends QueryService<Friend> {

    private final Logger log = LoggerFactory.getLogger(FriendQueryService.class);

    private FriendRepository friendRepository;

    private FriendMapper friendMapper;

    private FriendSearchRepository friendSearchRepository;

    public FriendQueryService(FriendRepository friendRepository, FriendMapper friendMapper, FriendSearchRepository friendSearchRepository) {
        this.friendRepository = friendRepository;
        this.friendMapper = friendMapper;
        this.friendSearchRepository = friendSearchRepository;
    }

    /**
     * Return a {@link List} of {@link FriendDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FriendDTO> findByCriteria(FriendCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Friend> specification = createSpecification(criteria);
        return friendMapper.toDto(friendRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FriendDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FriendDTO> findByCriteria(FriendCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Friend> specification = createSpecification(criteria);
        return friendRepository.findAll(specification, page)
            .map(friendMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FriendCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Friend> specification = createSpecification(criteria);
        return friendRepository.count(specification);
    }

    /**
     * Function to convert FriendCriteria to a {@link Specification}
     */
    private Specification<Friend> createSpecification(FriendCriteria criteria) {
        Specification<Friend> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Friend_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStatus(), Friend_.status));
            }
            if (criteria.getFriendshipRequestDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFriendshipRequestDate(), Friend_.friendshipRequestDate));
            }
            if (criteria.getFriendshipStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFriendshipStartDate(), Friend_.friendshipStartDate));
            }
            if (criteria.getFriendRequestingId() != null || criteria.getFriendAcceptingId() != null) {

                Specification<Friend> friendRequestingSpecification = null;
                Specification<Friend> friendAcceptingSpecification = null;

                if(criteria.getFriendRequestingId() != null) {
                    friendRequestingSpecification = buildSpecification(criteria.getFriendRequestingId(),
                        root -> root.join(Friend_.friendRequesting, JoinType.INNER).get(UserProfile_.id));
                }

                if(criteria.getFriendAcceptingId() != null) {
                    friendAcceptingSpecification = buildSpecification(criteria.getFriendAcceptingId(),
                        root -> root.join(Friend_.friendAccepting, JoinType.INNER).get(UserProfile_.id));
                }

                if(friendRequestingSpecification != null && friendAcceptingSpecification != null) {
                    specification = specification.and(friendRequestingSpecification.or(friendAcceptingSpecification));
                }
                else if(friendRequestingSpecification != null) {
                    specification = specification.and(friendRequestingSpecification);
                }
                else if(friendAcceptingSpecification != null) {
                    specification = specification.and(friendAcceptingSpecification);
                }
            }
        }
        return specification;
    }
}
