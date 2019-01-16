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

import com.bluehoodie.midup.domain.Invitee;
import com.bluehoodie.midup.domain.*; // for static metamodels
import com.bluehoodie.midup.repository.InviteeRepository;
import com.bluehoodie.midup.repository.search.InviteeSearchRepository;
import com.bluehoodie.midup.service.dto.InviteeCriteria;
import com.bluehoodie.midup.service.dto.InviteeDTO;
import com.bluehoodie.midup.service.mapper.InviteeMapper;

/**
 * Service for executing complex queries for Invitee entities in the database.
 * The main input is a {@link InviteeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InviteeDTO} or a {@link Page} of {@link InviteeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InviteeQueryService extends QueryService<Invitee> {

    private final Logger log = LoggerFactory.getLogger(InviteeQueryService.class);

    private InviteeRepository inviteeRepository;

    private InviteeMapper inviteeMapper;

    private InviteeSearchRepository inviteeSearchRepository;

    public InviteeQueryService(InviteeRepository inviteeRepository, InviteeMapper inviteeMapper, InviteeSearchRepository inviteeSearchRepository) {
        this.inviteeRepository = inviteeRepository;
        this.inviteeMapper = inviteeMapper;
        this.inviteeSearchRepository = inviteeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link InviteeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InviteeDTO> findByCriteria(InviteeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Invitee> specification = createSpecification(criteria);
        return inviteeMapper.toDto(inviteeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link InviteeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InviteeDTO> findByCriteria(InviteeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Invitee> specification = createSpecification(criteria);
        return inviteeRepository.findAll(specification, page)
            .map(inviteeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InviteeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Invitee> specification = createSpecification(criteria);
        return inviteeRepository.count(specification);
    }

    /**
     * Function to convert InviteeCriteria to a {@link Specification}
     */
    private Specification<Invitee> createSpecification(InviteeCriteria criteria) {
        Specification<Invitee> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Invitee_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStatus(), Invitee_.status));
            }
            if (criteria.getUserProfileId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserProfileId(),
                    root -> root.join(Invitee_.userProfile, JoinType.LEFT).get(UserProfile_.id)));
            }
            if (criteria.getEventId() != null) {
                specification = specification.and(buildSpecification(criteria.getEventId(),
                    root -> root.join(Invitee_.event, JoinType.LEFT).get(Event_.id)));
            }
        }
        return specification;
    }
}
