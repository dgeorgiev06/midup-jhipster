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

import com.bluehoodie.midup.domain.ProfileAddress;
import com.bluehoodie.midup.domain.*; // for static metamodels
import com.bluehoodie.midup.repository.ProfileAddressRepository;
import com.bluehoodie.midup.repository.search.ProfileAddressSearchRepository;
import com.bluehoodie.midup.service.dto.ProfileAddressCriteria;
import com.bluehoodie.midup.service.dto.ProfileAddressDTO;
import com.bluehoodie.midup.service.mapper.ProfileAddressMapper;

/**
 * Service for executing complex queries for ProfileAddress entities in the database.
 * The main input is a {@link ProfileAddressCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProfileAddressDTO} or a {@link Page} of {@link ProfileAddressDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProfileAddressQueryService extends QueryService<ProfileAddress> {

    private final Logger log = LoggerFactory.getLogger(ProfileAddressQueryService.class);

    private ProfileAddressRepository profileAddressRepository;

    private ProfileAddressMapper profileAddressMapper;

    private ProfileAddressSearchRepository profileAddressSearchRepository;

    public ProfileAddressQueryService(ProfileAddressRepository profileAddressRepository, ProfileAddressMapper profileAddressMapper, ProfileAddressSearchRepository profileAddressSearchRepository) {
        this.profileAddressRepository = profileAddressRepository;
        this.profileAddressMapper = profileAddressMapper;
        this.profileAddressSearchRepository = profileAddressSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ProfileAddressDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProfileAddressDTO> findByCriteria(ProfileAddressCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProfileAddress> specification = createSpecification(criteria);
        return profileAddressMapper.toDto(profileAddressRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProfileAddressDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProfileAddressDTO> findByCriteria(ProfileAddressCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProfileAddress> specification = createSpecification(criteria);
        return profileAddressRepository.findAll(specification, page)
            .map(profileAddressMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProfileAddressCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProfileAddress> specification = createSpecification(criteria);
        return profileAddressRepository.count(specification);
    }

    /**
     * Function to convert ProfileAddressCriteria to a {@link Specification}
     */
    private Specification<ProfileAddress> createSpecification(ProfileAddressCriteria criteria) {
        Specification<ProfileAddress> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ProfileAddress_.id));
            }
            if (criteria.getAddressType() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAddressType(), ProfileAddress_.addressType));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), ProfileAddress_.address));
            }
            if (criteria.getIsDefault() != null) {
                specification = specification.and(buildSpecification(criteria.getIsDefault(), ProfileAddress_.isDefault));
            }
            if (criteria.getLongitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLongitude(), ProfileAddress_.longitude));
            }
            if (criteria.getLatitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLatitude(), ProfileAddress_.latitude));
            }
            if (criteria.getUserProfileId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserProfileId(),
                    root -> root.join(ProfileAddress_.userProfile, JoinType.LEFT).get(UserProfile_.id)));
            }
        }
        return specification;
    }
}
