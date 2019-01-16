package com.bluehoodie.midup.service;

import com.bluehoodie.midup.domain.ProfileAddress;
import com.bluehoodie.midup.repository.ProfileAddressRepository;
import com.bluehoodie.midup.repository.search.ProfileAddressSearchRepository;
import com.bluehoodie.midup.service.dto.ProfileAddressDTO;
import com.bluehoodie.midup.service.mapper.ProfileAddressMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ProfileAddress.
 */
@Service
@Transactional
public class ProfileAddressService {

    private final Logger log = LoggerFactory.getLogger(ProfileAddressService.class);

    private ProfileAddressRepository profileAddressRepository;

    private ProfileAddressMapper profileAddressMapper;

    private ProfileAddressSearchRepository profileAddressSearchRepository;

    public ProfileAddressService(ProfileAddressRepository profileAddressRepository, ProfileAddressMapper profileAddressMapper, ProfileAddressSearchRepository profileAddressSearchRepository) {
        this.profileAddressRepository = profileAddressRepository;
        this.profileAddressMapper = profileAddressMapper;
        this.profileAddressSearchRepository = profileAddressSearchRepository;
    }

    /**
     * Save a profileAddress.
     *
     * @param profileAddressDTO the entity to save
     * @return the persisted entity
     */
    public ProfileAddressDTO save(ProfileAddressDTO profileAddressDTO) {
        log.debug("Request to save ProfileAddress : {}", profileAddressDTO);

        ProfileAddress profileAddress = profileAddressMapper.toEntity(profileAddressDTO);
        profileAddress = profileAddressRepository.save(profileAddress);
        ProfileAddressDTO result = profileAddressMapper.toDto(profileAddress);
        profileAddressSearchRepository.save(profileAddress);
        return result;
    }

    /**
     * Get all the profileAddresses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProfileAddressDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProfileAddresses");
        return profileAddressRepository.findAll(pageable)
            .map(profileAddressMapper::toDto);
    }


    /**
     * Get one profileAddress by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<ProfileAddressDTO> findOne(Long id) {
        log.debug("Request to get ProfileAddress : {}", id);
        return profileAddressRepository.findById(id)
            .map(profileAddressMapper::toDto);
    }

    /**
     * Delete the profileAddress by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProfileAddress : {}", id);
        profileAddressRepository.deleteById(id);
        profileAddressSearchRepository.deleteById(id);
    }

    /**
     * Search for the profileAddress corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProfileAddressDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProfileAddresses for query {}", query);
        return profileAddressSearchRepository.search(queryStringQuery(query), pageable)
            .map(profileAddressMapper::toDto);
    }
}
