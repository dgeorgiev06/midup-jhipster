package com.bluehoodie.midup.web.rest;

import com.bluehoodie.midup.MidupApp;

import com.bluehoodie.midup.domain.ProfileAddress;
import com.bluehoodie.midup.domain.UserProfile;
import com.bluehoodie.midup.repository.ProfileAddressRepository;
import com.bluehoodie.midup.repository.search.ProfileAddressSearchRepository;
import com.bluehoodie.midup.service.ProfileAddressService;
import com.bluehoodie.midup.service.dto.ProfileAddressDTO;
import com.bluehoodie.midup.service.mapper.ProfileAddressMapper;
import com.bluehoodie.midup.web.rest.errors.ExceptionTranslator;
import com.bluehoodie.midup.service.dto.ProfileAddressCriteria;
import com.bluehoodie.midup.service.ProfileAddressQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static com.bluehoodie.midup.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProfileAddressResource REST controller.
 *
 * @see ProfileAddressResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MidupApp.class)
public class ProfileAddressResourceIntTest {

    private static final Integer DEFAULT_ADDRESS_TYPE = 1;
    private static final Integer UPDATED_ADDRESS_TYPE = 2;

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    @Autowired
    private ProfileAddressRepository profileAddressRepository;

    @Autowired
    private ProfileAddressMapper profileAddressMapper;
    
    @Autowired
    private ProfileAddressService profileAddressService;

    /**
     * This repository is mocked in the com.bluehoodie.midup.repository.search test package.
     *
     * @see com.bluehoodie.midup.repository.search.ProfileAddressSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProfileAddressSearchRepository mockProfileAddressSearchRepository;

    @Autowired
    private ProfileAddressQueryService profileAddressQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProfileAddressMockMvc;

    private ProfileAddress profileAddress;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProfileAddressResource profileAddressResource = new ProfileAddressResource(profileAddressService, profileAddressQueryService);
        this.restProfileAddressMockMvc = MockMvcBuilders.standaloneSetup(profileAddressResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileAddress createEntity(EntityManager em) {
        ProfileAddress profileAddress = new ProfileAddress()
            .addressType(DEFAULT_ADDRESS_TYPE)
            .address(DEFAULT_ADDRESS)
            .isDefault(DEFAULT_IS_DEFAULT)
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE);
        return profileAddress;
    }

    @Before
    public void initTest() {
        profileAddress = createEntity(em);
    }

    @Test
    @Transactional
    public void createProfileAddress() throws Exception {
        int databaseSizeBeforeCreate = profileAddressRepository.findAll().size();

        // Create the ProfileAddress
        ProfileAddressDTO profileAddressDTO = profileAddressMapper.toDto(profileAddress);
        restProfileAddressMockMvc.perform(post("/api/profile-addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(profileAddressDTO)))
            .andExpect(status().isCreated());

        // Validate the ProfileAddress in the database
        List<ProfileAddress> profileAddressList = profileAddressRepository.findAll();
        assertThat(profileAddressList).hasSize(databaseSizeBeforeCreate + 1);
        ProfileAddress testProfileAddress = profileAddressList.get(profileAddressList.size() - 1);
        assertThat(testProfileAddress.getAddressType()).isEqualTo(DEFAULT_ADDRESS_TYPE);
        assertThat(testProfileAddress.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testProfileAddress.isIsDefault()).isEqualTo(DEFAULT_IS_DEFAULT);
        assertThat(testProfileAddress.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testProfileAddress.getLatitude()).isEqualTo(DEFAULT_LATITUDE);

        // Validate the ProfileAddress in Elasticsearch
        verify(mockProfileAddressSearchRepository, times(1)).save(testProfileAddress);
    }

    @Test
    @Transactional
    public void createProfileAddressWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = profileAddressRepository.findAll().size();

        // Create the ProfileAddress with an existing ID
        profileAddress.setId(1L);
        ProfileAddressDTO profileAddressDTO = profileAddressMapper.toDto(profileAddress);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfileAddressMockMvc.perform(post("/api/profile-addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(profileAddressDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProfileAddress in the database
        List<ProfileAddress> profileAddressList = profileAddressRepository.findAll();
        assertThat(profileAddressList).hasSize(databaseSizeBeforeCreate);

        // Validate the ProfileAddress in Elasticsearch
        verify(mockProfileAddressSearchRepository, times(0)).save(profileAddress);
    }

    @Test
    @Transactional
    public void getAllProfileAddresses() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList
        restProfileAddressMockMvc.perform(get("/api/profile-addresses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profileAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].addressType").value(hasItem(DEFAULT_ADDRESS_TYPE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT.booleanValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getProfileAddress() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get the profileAddress
        restProfileAddressMockMvc.perform(get("/api/profile-addresses/{id}", profileAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(profileAddress.getId().intValue()))
            .andExpect(jsonPath("$.addressType").value(DEFAULT_ADDRESS_TYPE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.isDefault").value(DEFAULT_IS_DEFAULT.booleanValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()));
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByAddressTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where addressType equals to DEFAULT_ADDRESS_TYPE
        defaultProfileAddressShouldBeFound("addressType.equals=" + DEFAULT_ADDRESS_TYPE);

        // Get all the profileAddressList where addressType equals to UPDATED_ADDRESS_TYPE
        defaultProfileAddressShouldNotBeFound("addressType.equals=" + UPDATED_ADDRESS_TYPE);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByAddressTypeIsInShouldWork() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where addressType in DEFAULT_ADDRESS_TYPE or UPDATED_ADDRESS_TYPE
        defaultProfileAddressShouldBeFound("addressType.in=" + DEFAULT_ADDRESS_TYPE + "," + UPDATED_ADDRESS_TYPE);

        // Get all the profileAddressList where addressType equals to UPDATED_ADDRESS_TYPE
        defaultProfileAddressShouldNotBeFound("addressType.in=" + UPDATED_ADDRESS_TYPE);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByAddressTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where addressType is not null
        defaultProfileAddressShouldBeFound("addressType.specified=true");

        // Get all the profileAddressList where addressType is null
        defaultProfileAddressShouldNotBeFound("addressType.specified=false");
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByAddressTypeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where addressType greater than or equals to DEFAULT_ADDRESS_TYPE
        defaultProfileAddressShouldBeFound("addressType.greaterOrEqualThan=" + DEFAULT_ADDRESS_TYPE);

        // Get all the profileAddressList where addressType greater than or equals to UPDATED_ADDRESS_TYPE
        defaultProfileAddressShouldNotBeFound("addressType.greaterOrEqualThan=" + UPDATED_ADDRESS_TYPE);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByAddressTypeIsLessThanSomething() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where addressType less than or equals to DEFAULT_ADDRESS_TYPE
        defaultProfileAddressShouldNotBeFound("addressType.lessThan=" + DEFAULT_ADDRESS_TYPE);

        // Get all the profileAddressList where addressType less than or equals to UPDATED_ADDRESS_TYPE
        defaultProfileAddressShouldBeFound("addressType.lessThan=" + UPDATED_ADDRESS_TYPE);
    }


    @Test
    @Transactional
    public void getAllProfileAddressesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where address equals to DEFAULT_ADDRESS
        defaultProfileAddressShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the profileAddressList where address equals to UPDATED_ADDRESS
        defaultProfileAddressShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultProfileAddressShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the profileAddressList where address equals to UPDATED_ADDRESS
        defaultProfileAddressShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where address is not null
        defaultProfileAddressShouldBeFound("address.specified=true");

        // Get all the profileAddressList where address is null
        defaultProfileAddressShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByIsDefaultIsEqualToSomething() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where isDefault equals to DEFAULT_IS_DEFAULT
        defaultProfileAddressShouldBeFound("isDefault.equals=" + DEFAULT_IS_DEFAULT);

        // Get all the profileAddressList where isDefault equals to UPDATED_IS_DEFAULT
        defaultProfileAddressShouldNotBeFound("isDefault.equals=" + UPDATED_IS_DEFAULT);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByIsDefaultIsInShouldWork() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where isDefault in DEFAULT_IS_DEFAULT or UPDATED_IS_DEFAULT
        defaultProfileAddressShouldBeFound("isDefault.in=" + DEFAULT_IS_DEFAULT + "," + UPDATED_IS_DEFAULT);

        // Get all the profileAddressList where isDefault equals to UPDATED_IS_DEFAULT
        defaultProfileAddressShouldNotBeFound("isDefault.in=" + UPDATED_IS_DEFAULT);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByIsDefaultIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where isDefault is not null
        defaultProfileAddressShouldBeFound("isDefault.specified=true");

        // Get all the profileAddressList where isDefault is null
        defaultProfileAddressShouldNotBeFound("isDefault.specified=false");
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where longitude equals to DEFAULT_LONGITUDE
        defaultProfileAddressShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the profileAddressList where longitude equals to UPDATED_LONGITUDE
        defaultProfileAddressShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultProfileAddressShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the profileAddressList where longitude equals to UPDATED_LONGITUDE
        defaultProfileAddressShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where longitude is not null
        defaultProfileAddressShouldBeFound("longitude.specified=true");

        // Get all the profileAddressList where longitude is null
        defaultProfileAddressShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where latitude equals to DEFAULT_LATITUDE
        defaultProfileAddressShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the profileAddressList where latitude equals to UPDATED_LATITUDE
        defaultProfileAddressShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultProfileAddressShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the profileAddressList where latitude equals to UPDATED_LATITUDE
        defaultProfileAddressShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        // Get all the profileAddressList where latitude is not null
        defaultProfileAddressShouldBeFound("latitude.specified=true");

        // Get all the profileAddressList where latitude is null
        defaultProfileAddressShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    public void getAllProfileAddressesByUserProfileIsEqualToSomething() throws Exception {
        // Initialize the database
        UserProfile userProfile = UserProfileResourceIntTest.createEntity(em);
        em.persist(userProfile);
        em.flush();
        profileAddress.setUserProfile(userProfile);
        profileAddressRepository.saveAndFlush(profileAddress);
        Long userProfileId = userProfile.getId();

        // Get all the profileAddressList where userProfile equals to userProfileId
        defaultProfileAddressShouldBeFound("userProfileId.equals=" + userProfileId);

        // Get all the profileAddressList where userProfile equals to userProfileId + 1
        defaultProfileAddressShouldNotBeFound("userProfileId.equals=" + (userProfileId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultProfileAddressShouldBeFound(String filter) throws Exception {
        restProfileAddressMockMvc.perform(get("/api/profile-addresses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profileAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].addressType").value(hasItem(DEFAULT_ADDRESS_TYPE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT.booleanValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())));

        // Check, that the count call also returns 1
        restProfileAddressMockMvc.perform(get("/api/profile-addresses/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultProfileAddressShouldNotBeFound(String filter) throws Exception {
        restProfileAddressMockMvc.perform(get("/api/profile-addresses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProfileAddressMockMvc.perform(get("/api/profile-addresses/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingProfileAddress() throws Exception {
        // Get the profileAddress
        restProfileAddressMockMvc.perform(get("/api/profile-addresses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProfileAddress() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        int databaseSizeBeforeUpdate = profileAddressRepository.findAll().size();

        // Update the profileAddress
        ProfileAddress updatedProfileAddress = profileAddressRepository.findById(profileAddress.getId()).get();
        // Disconnect from session so that the updates on updatedProfileAddress are not directly saved in db
        em.detach(updatedProfileAddress);
        updatedProfileAddress
            .addressType(UPDATED_ADDRESS_TYPE)
            .address(UPDATED_ADDRESS)
            .isDefault(UPDATED_IS_DEFAULT)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE);
        ProfileAddressDTO profileAddressDTO = profileAddressMapper.toDto(updatedProfileAddress);

        restProfileAddressMockMvc.perform(put("/api/profile-addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(profileAddressDTO)))
            .andExpect(status().isOk());

        // Validate the ProfileAddress in the database
        List<ProfileAddress> profileAddressList = profileAddressRepository.findAll();
        assertThat(profileAddressList).hasSize(databaseSizeBeforeUpdate);
        ProfileAddress testProfileAddress = profileAddressList.get(profileAddressList.size() - 1);
        assertThat(testProfileAddress.getAddressType()).isEqualTo(UPDATED_ADDRESS_TYPE);
        assertThat(testProfileAddress.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testProfileAddress.isIsDefault()).isEqualTo(UPDATED_IS_DEFAULT);
        assertThat(testProfileAddress.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testProfileAddress.getLatitude()).isEqualTo(UPDATED_LATITUDE);

        // Validate the ProfileAddress in Elasticsearch
        verify(mockProfileAddressSearchRepository, times(1)).save(testProfileAddress);
    }

    @Test
    @Transactional
    public void updateNonExistingProfileAddress() throws Exception {
        int databaseSizeBeforeUpdate = profileAddressRepository.findAll().size();

        // Create the ProfileAddress
        ProfileAddressDTO profileAddressDTO = profileAddressMapper.toDto(profileAddress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileAddressMockMvc.perform(put("/api/profile-addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(profileAddressDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProfileAddress in the database
        List<ProfileAddress> profileAddressList = profileAddressRepository.findAll();
        assertThat(profileAddressList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProfileAddress in Elasticsearch
        verify(mockProfileAddressSearchRepository, times(0)).save(profileAddress);
    }

    @Test
    @Transactional
    public void deleteProfileAddress() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);

        int databaseSizeBeforeDelete = profileAddressRepository.findAll().size();

        // Get the profileAddress
        restProfileAddressMockMvc.perform(delete("/api/profile-addresses/{id}", profileAddress.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ProfileAddress> profileAddressList = profileAddressRepository.findAll();
        assertThat(profileAddressList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ProfileAddress in Elasticsearch
        verify(mockProfileAddressSearchRepository, times(1)).deleteById(profileAddress.getId());
    }

    @Test
    @Transactional
    public void searchProfileAddress() throws Exception {
        // Initialize the database
        profileAddressRepository.saveAndFlush(profileAddress);
        when(mockProfileAddressSearchRepository.search(queryStringQuery("id:" + profileAddress.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(profileAddress), PageRequest.of(0, 1), 1));
        // Search the profileAddress
        restProfileAddressMockMvc.perform(get("/api/_search/profile-addresses?query=id:" + profileAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profileAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].addressType").value(hasItem(DEFAULT_ADDRESS_TYPE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT.booleanValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileAddress.class);
        ProfileAddress profileAddress1 = new ProfileAddress();
        profileAddress1.setId(1L);
        ProfileAddress profileAddress2 = new ProfileAddress();
        profileAddress2.setId(profileAddress1.getId());
        assertThat(profileAddress1).isEqualTo(profileAddress2);
        profileAddress2.setId(2L);
        assertThat(profileAddress1).isNotEqualTo(profileAddress2);
        profileAddress1.setId(null);
        assertThat(profileAddress1).isNotEqualTo(profileAddress2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileAddressDTO.class);
        ProfileAddressDTO profileAddressDTO1 = new ProfileAddressDTO();
        profileAddressDTO1.setId(1L);
        ProfileAddressDTO profileAddressDTO2 = new ProfileAddressDTO();
        assertThat(profileAddressDTO1).isNotEqualTo(profileAddressDTO2);
        profileAddressDTO2.setId(profileAddressDTO1.getId());
        assertThat(profileAddressDTO1).isEqualTo(profileAddressDTO2);
        profileAddressDTO2.setId(2L);
        assertThat(profileAddressDTO1).isNotEqualTo(profileAddressDTO2);
        profileAddressDTO1.setId(null);
        assertThat(profileAddressDTO1).isNotEqualTo(profileAddressDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(profileAddressMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(profileAddressMapper.fromId(null)).isNull();
    }
}
