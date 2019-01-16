package com.bluehoodie.midup.service;

import com.bluehoodie.midup.domain.Invitee;
import com.bluehoodie.midup.repository.InviteeRepository;
import com.bluehoodie.midup.repository.search.InviteeSearchRepository;
import com.bluehoodie.midup.service.dto.InviteeDTO;
import com.bluehoodie.midup.service.mapper.InviteeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Invitee.
 */
@Service
@Transactional
public class InviteeService {

    private final Logger log = LoggerFactory.getLogger(InviteeService.class);

    private InviteeRepository inviteeRepository;

    private InviteeMapper inviteeMapper;

    private InviteeSearchRepository inviteeSearchRepository;

    public InviteeService(InviteeRepository inviteeRepository, InviteeMapper inviteeMapper, InviteeSearchRepository inviteeSearchRepository) {
        this.inviteeRepository = inviteeRepository;
        this.inviteeMapper = inviteeMapper;
        this.inviteeSearchRepository = inviteeSearchRepository;
    }

    /**
     * Save a invitee.
     *
     * @param inviteeDTO the entity to save
     * @return the persisted entity
     */
    public InviteeDTO save(InviteeDTO inviteeDTO) {
        log.debug("Request to save Invitee : {}", inviteeDTO);

        Invitee invitee = inviteeMapper.toEntity(inviteeDTO);
        invitee = inviteeRepository.save(invitee);
        InviteeDTO result = inviteeMapper.toDto(invitee);
        inviteeSearchRepository.save(invitee);
        return result;
    }

    /**
     * Get all the invitees.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<InviteeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Invitees");
        return inviteeRepository.findAll(pageable)
            .map(inviteeMapper::toDto);
    }


    /**
     * Get one invitee by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<InviteeDTO> findOne(Long id) {
        log.debug("Request to get Invitee : {}", id);
        return inviteeRepository.findById(id)
            .map(inviteeMapper::toDto);
    }

    /**
     * Delete the invitee by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Invitee : {}", id);
        inviteeRepository.deleteById(id);
        inviteeSearchRepository.deleteById(id);
    }

    /**
     * Search for the invitee corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<InviteeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Invitees for query {}", query);
        return inviteeSearchRepository.search(queryStringQuery(query), pageable)
            .map(inviteeMapper::toDto);
    }
}
