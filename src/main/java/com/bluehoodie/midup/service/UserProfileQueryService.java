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

import com.bluehoodie.midup.domain.UserProfile;
import com.bluehoodie.midup.domain.*; // for static metamodels
import com.bluehoodie.midup.repository.UserProfileRepository;
import com.bluehoodie.midup.repository.search.UserProfileSearchRepository;
import com.bluehoodie.midup.service.dto.UserProfileCriteria;
import com.bluehoodie.midup.service.dto.UserProfileDTO;
import com.bluehoodie.midup.service.mapper.UserProfileMapper;

/**
 * Service for executing complex queries for UserProfile entities in the database.
 * The main input is a {@link UserProfileCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserProfileDTO} or a {@link Page} of {@link UserProfileDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserProfileQueryService extends QueryService<UserProfile> {

    private final Logger log = LoggerFactory.getLogger(UserProfileQueryService.class);

    private UserProfileRepository userProfileRepository;

    private UserProfileMapper userProfileMapper;

    private UserProfileSearchRepository userProfileSearchRepository;

    public UserProfileQueryService(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper, UserProfileSearchRepository userProfileSearchRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.userProfileSearchRepository = userProfileSearchRepository;
    }

    /**
     * Return a {@link List} of {@link UserProfileDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserProfileDTO> findByCriteria(UserProfileCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserProfile> specification = createSpecification(criteria);
        return userProfileMapper.toDto(userProfileRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UserProfileDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> findByCriteria(UserProfileCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserProfile> specification = createSpecification(criteria);
        return userProfileRepository.findAll(specification, page)
            .map(userProfileMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserProfileCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserProfile> specification = createSpecification(criteria);
        return userProfileRepository.count(specification);
    }

    /**
     * Function to convert UserProfileCriteria to a {@link Specification}
     */
    private Specification<UserProfile> createSpecification(UserProfileCriteria criteria) {
        Specification<UserProfile> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), UserProfile_.id));
            }
            if (criteria.getImageUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImageUrl(), UserProfile_.imageUrl));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), UserProfile_.address));
            }
            if (criteria.getAddressLongitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAddressLongitude(), UserProfile_.addressLongitude));
            }
            if (criteria.getAddressLatitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAddressLatitude(), UserProfile_.addressLatitude));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(UserProfile_.user, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getRequestingFriendId() != null) {
                specification = specification.and(buildSpecification(criteria.getRequestingFriendId(),
                    root -> root.join(UserProfile_.requestingFriends, JoinType.LEFT).get(Friend_.id)));
            }
            if (criteria.getAcceptingFriendId() != null) {
                specification = specification.and(buildSpecification(criteria.getAcceptingFriendId(),
                    root -> root.join(UserProfile_.acceptingFriends, JoinType.LEFT).get(Friend_.id)));
            }
            if (criteria.getEventId() != null) {
                specification = specification.and(buildSpecification(criteria.getEventId(),
                    root -> root.join(UserProfile_.events, JoinType.LEFT).get(Event_.id)));
            }
            if (criteria.getInviteeId() != null) {
                specification = specification.and(buildSpecification(criteria.getInviteeId(),
                    root -> root.join(UserProfile_.invitees, JoinType.LEFT).get(Invitee_.id)));
            }
        }
        return specification;
    }
}
