package com.bluehoodie.midup.web.rest;

import com.bluehoodie.midup.MidupApp;

import com.bluehoodie.midup.domain.UserProfile;
import com.bluehoodie.midup.domain.User;
import com.bluehoodie.midup.domain.Friend;
import com.bluehoodie.midup.domain.Friend;
import com.bluehoodie.midup.domain.Event;
import com.bluehoodie.midup.domain.Invitee;
import com.bluehoodie.midup.repository.UserProfileRepository;
import com.bluehoodie.midup.repository.search.UserProfileSearchRepository;
import com.bluehoodie.midup.service.*;
import com.bluehoodie.midup.service.dto.UserProfileDTO;
import com.bluehoodie.midup.service.mapper.UserProfileMapper;
import com.bluehoodie.midup.web.rest.errors.ExceptionTranslator;
import com.bluehoodie.midup.service.dto.UserProfileCriteria;

import com.cloudinary.Cloudinary;
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
 * Test class for the UserProfileResource REST controller.
 *
 * @see UserProfileResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MidupApp.class)
public class UserProfileResourceIntTest {

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Double DEFAULT_ADDRESS_LONGITUDE = 1D;
    private static final Double UPDATED_ADDRESS_LONGITUDE = 2D;

    private static final Double DEFAULT_ADDRESS_LATITUDE = 1D;
    private static final Double UPDATED_ADDRESS_LATITUDE = 2D;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserProfileService userProfileService;

    /**
     * This repository is mocked in the com.bluehoodie.midup.repository.search test package.
     *
     * @see com.bluehoodie.midup.repository.search.UserProfileSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserProfileSearchRepository mockUserProfileSearchRepository;

    @Autowired
    private UserProfileQueryService userProfileQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restUserProfileMockMvc;

    private UserProfile userProfile;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendQueryService friendQueryService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserProfileResource userProfileResource = new UserProfileResource(userProfileService, userProfileQueryService, userService, friendQueryService, cloudinaryService);
        this.restUserProfileMockMvc = MockMvcBuilders.standaloneSetup(userProfileResource)
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
    public static UserProfile createEntity(EntityManager em) {
        UserProfile userProfile = new UserProfile()
            .imageUrl(DEFAULT_IMAGE_URL)
            .address(DEFAULT_ADDRESS)
            .addressLongitude(DEFAULT_ADDRESS_LONGITUDE)
            .addressLatitude(DEFAULT_ADDRESS_LATITUDE);
        return userProfile;
    }

    @Before
    public void initTest() {
        userProfile = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserProfile() throws Exception {
        int databaseSizeBeforeCreate = userProfileRepository.findAll().size();

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);
        restUserProfileMockMvc.perform(post("/api/user-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userProfileDTO)))
            .andExpect(status().isCreated());

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll();
        assertThat(userProfileList).hasSize(databaseSizeBeforeCreate + 1);
        UserProfile testUserProfile = userProfileList.get(userProfileList.size() - 1);
        assertThat(testUserProfile.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testUserProfile.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testUserProfile.getAddressLongitude()).isEqualTo(DEFAULT_ADDRESS_LONGITUDE);
        assertThat(testUserProfile.getAddressLatitude()).isEqualTo(DEFAULT_ADDRESS_LATITUDE);

        // Validate the UserProfile in Elasticsearch
        verify(mockUserProfileSearchRepository, times(1)).save(testUserProfile);
    }

    @Test
    @Transactional
    public void createUserProfileWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userProfileRepository.findAll().size();

        // Create the UserProfile with an existing ID
        userProfile.setId(1L);
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserProfileMockMvc.perform(post("/api/user-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userProfileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll();
        assertThat(userProfileList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserProfile in Elasticsearch
        verify(mockUserProfileSearchRepository, times(0)).save(userProfile);
    }

    @Test
    @Transactional
    public void getAllUserProfiles() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList
        restUserProfileMockMvc.perform(get("/api/user-profiles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].addressLongitude").value(hasItem(DEFAULT_ADDRESS_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].addressLatitude").value(hasItem(DEFAULT_ADDRESS_LATITUDE.doubleValue())));
    }

    @Test
    @Transactional
    public void getUserProfile() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get the userProfile
        restUserProfileMockMvc.perform(get("/api/user-profiles/{id}", userProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userProfile.getId().intValue()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.addressLongitude").value(DEFAULT_ADDRESS_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.addressLatitude").value(DEFAULT_ADDRESS_LATITUDE.doubleValue()));
    }

    @Test
    @Transactional
    public void getAllUserProfilesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where imageUrl equals to DEFAULT_IMAGE_URL
        defaultUserProfileShouldBeFound("imageUrl.equals=" + DEFAULT_IMAGE_URL);

        // Get all the userProfileList where imageUrl equals to UPDATED_IMAGE_URL
        defaultUserProfileShouldNotBeFound("imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    public void getAllUserProfilesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where imageUrl in DEFAULT_IMAGE_URL or UPDATED_IMAGE_URL
        defaultUserProfileShouldBeFound("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL);

        // Get all the userProfileList where imageUrl equals to UPDATED_IMAGE_URL
        defaultUserProfileShouldNotBeFound("imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    public void getAllUserProfilesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where imageUrl is not null
        defaultUserProfileShouldBeFound("imageUrl.specified=true");

        // Get all the userProfileList where imageUrl is null
        defaultUserProfileShouldNotBeFound("imageUrl.specified=false");
    }

    @Test
    @Transactional
    public void getAllUserProfilesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where address equals to DEFAULT_ADDRESS
        defaultUserProfileShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the userProfileList where address equals to UPDATED_ADDRESS
        defaultUserProfileShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllUserProfilesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultUserProfileShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the userProfileList where address equals to UPDATED_ADDRESS
        defaultUserProfileShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllUserProfilesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where address is not null
        defaultUserProfileShouldBeFound("address.specified=true");

        // Get all the userProfileList where address is null
        defaultUserProfileShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    public void getAllUserProfilesByAddressLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where addressLongitude equals to DEFAULT_ADDRESS_LONGITUDE
        defaultUserProfileShouldBeFound("addressLongitude.equals=" + DEFAULT_ADDRESS_LONGITUDE);

        // Get all the userProfileList where addressLongitude equals to UPDATED_ADDRESS_LONGITUDE
        defaultUserProfileShouldNotBeFound("addressLongitude.equals=" + UPDATED_ADDRESS_LONGITUDE);
    }

    @Test
    @Transactional
    public void getAllUserProfilesByAddressLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where addressLongitude in DEFAULT_ADDRESS_LONGITUDE or UPDATED_ADDRESS_LONGITUDE
        defaultUserProfileShouldBeFound("addressLongitude.in=" + DEFAULT_ADDRESS_LONGITUDE + "," + UPDATED_ADDRESS_LONGITUDE);

        // Get all the userProfileList where addressLongitude equals to UPDATED_ADDRESS_LONGITUDE
        defaultUserProfileShouldNotBeFound("addressLongitude.in=" + UPDATED_ADDRESS_LONGITUDE);
    }

    @Test
    @Transactional
    public void getAllUserProfilesByAddressLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where addressLongitude is not null
        defaultUserProfileShouldBeFound("addressLongitude.specified=true");

        // Get all the userProfileList where addressLongitude is null
        defaultUserProfileShouldNotBeFound("addressLongitude.specified=false");
    }

    @Test
    @Transactional
    public void getAllUserProfilesByAddressLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where addressLatitude equals to DEFAULT_ADDRESS_LATITUDE
        defaultUserProfileShouldBeFound("addressLatitude.equals=" + DEFAULT_ADDRESS_LATITUDE);

        // Get all the userProfileList where addressLatitude equals to UPDATED_ADDRESS_LATITUDE
        defaultUserProfileShouldNotBeFound("addressLatitude.equals=" + UPDATED_ADDRESS_LATITUDE);
    }

    @Test
    @Transactional
    public void getAllUserProfilesByAddressLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where addressLatitude in DEFAULT_ADDRESS_LATITUDE or UPDATED_ADDRESS_LATITUDE
        defaultUserProfileShouldBeFound("addressLatitude.in=" + DEFAULT_ADDRESS_LATITUDE + "," + UPDATED_ADDRESS_LATITUDE);

        // Get all the userProfileList where addressLatitude equals to UPDATED_ADDRESS_LATITUDE
        defaultUserProfileShouldNotBeFound("addressLatitude.in=" + UPDATED_ADDRESS_LATITUDE);
    }

    @Test
    @Transactional
    public void getAllUserProfilesByAddressLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        // Get all the userProfileList where addressLatitude is not null
        defaultUserProfileShouldBeFound("addressLatitude.specified=true");

        // Get all the userProfileList where addressLatitude is null
        defaultUserProfileShouldNotBeFound("addressLatitude.specified=false");
    }

    @Test
    @Transactional
    public void getAllUserProfilesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        userProfile.setUser(user);
        userProfileRepository.saveAndFlush(userProfile);
        Long userId = user.getId();

        // Get all the userProfileList where user equals to userId
        defaultUserProfileShouldBeFound("userId.equals=" + userId);

        // Get all the userProfileList where user equals to userId + 1
        defaultUserProfileShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllUserProfilesByRequestingFriendIsEqualToSomething() throws Exception {
        // Initialize the database
        Friend requestingFriend = FriendResourceIntTest.createEntity(em);
        em.persist(requestingFriend);
        em.flush();
        userProfile.addRequestingFriend(requestingFriend);
        userProfileRepository.saveAndFlush(userProfile);
        Long requestingFriendId = requestingFriend.getId();

        // Get all the userProfileList where requestingFriend equals to requestingFriendId
        defaultUserProfileShouldBeFound("requestingFriendId.equals=" + requestingFriendId);

        // Get all the userProfileList where requestingFriend equals to requestingFriendId + 1
        defaultUserProfileShouldNotBeFound("requestingFriendId.equals=" + (requestingFriendId + 1));
    }


    @Test
    @Transactional
    public void getAllUserProfilesByAcceptingFriendIsEqualToSomething() throws Exception {
        // Initialize the database
        Friend acceptingFriend = FriendResourceIntTest.createEntity(em);
        em.persist(acceptingFriend);
        em.flush();
        userProfile.addAcceptingFriend(acceptingFriend);
        userProfileRepository.saveAndFlush(userProfile);
        Long acceptingFriendId = acceptingFriend.getId();

        // Get all the userProfileList where acceptingFriend equals to acceptingFriendId
        defaultUserProfileShouldBeFound("acceptingFriendId.equals=" + acceptingFriendId);

        // Get all the userProfileList where acceptingFriend equals to acceptingFriendId + 1
        defaultUserProfileShouldNotBeFound("acceptingFriendId.equals=" + (acceptingFriendId + 1));
    }


    @Test
    @Transactional
    public void getAllUserProfilesByEventIsEqualToSomething() throws Exception {
        // Initialize the database
        Event event = EventResourceIntTest.createEntity(em);
        em.persist(event);
        em.flush();
        userProfile.addEvent(event);
        userProfileRepository.saveAndFlush(userProfile);
        Long eventId = event.getId();

        // Get all the userProfileList where event equals to eventId
        defaultUserProfileShouldBeFound("eventId.equals=" + eventId);

        // Get all the userProfileList where event equals to eventId + 1
        defaultUserProfileShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }


    @Test
    @Transactional
    public void getAllUserProfilesByInviteeIsEqualToSomething() throws Exception {
        // Initialize the database
        Invitee invitee = InviteeResourceIntTest.createEntity(em);
        em.persist(invitee);
        em.flush();
        userProfile.addInvitee(invitee);
        userProfileRepository.saveAndFlush(userProfile);
        Long inviteeId = invitee.getId();

        // Get all the userProfileList where invitee equals to inviteeId
        defaultUserProfileShouldBeFound("inviteeId.equals=" + inviteeId);

        // Get all the userProfileList where invitee equals to inviteeId + 1
        defaultUserProfileShouldNotBeFound("inviteeId.equals=" + (inviteeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultUserProfileShouldBeFound(String filter) throws Exception {
        restUserProfileMockMvc.perform(get("/api/user-profiles?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].addressLongitude").value(hasItem(DEFAULT_ADDRESS_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].addressLatitude").value(hasItem(DEFAULT_ADDRESS_LATITUDE.doubleValue())));

        // Check, that the count call also returns 1
        restUserProfileMockMvc.perform(get("/api/user-profiles/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultUserProfileShouldNotBeFound(String filter) throws Exception {
        restUserProfileMockMvc.perform(get("/api/user-profiles?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserProfileMockMvc.perform(get("/api/user-profiles/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingUserProfile() throws Exception {
        // Get the userProfile
        restUserProfileMockMvc.perform(get("/api/user-profiles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserProfile() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        int databaseSizeBeforeUpdate = userProfileRepository.findAll().size();

        // Update the userProfile
        UserProfile updatedUserProfile = userProfileRepository.findById(userProfile.getId()).get();
        // Disconnect from session so that the updates on updatedUserProfile are not directly saved in db
        em.detach(updatedUserProfile);
        updatedUserProfile
            .imageUrl(UPDATED_IMAGE_URL)
            .address(UPDATED_ADDRESS)
            .addressLongitude(UPDATED_ADDRESS_LONGITUDE)
            .addressLatitude(UPDATED_ADDRESS_LATITUDE);
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(updatedUserProfile);

        restUserProfileMockMvc.perform(put("/api/user-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userProfileDTO)))
            .andExpect(status().isOk());

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);
        UserProfile testUserProfile = userProfileList.get(userProfileList.size() - 1);
        assertThat(testUserProfile.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testUserProfile.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testUserProfile.getAddressLongitude()).isEqualTo(UPDATED_ADDRESS_LONGITUDE);
        assertThat(testUserProfile.getAddressLatitude()).isEqualTo(UPDATED_ADDRESS_LATITUDE);

        // Validate the UserProfile in Elasticsearch
        verify(mockUserProfileSearchRepository, times(1)).save(testUserProfile);
    }

    @Test
    @Transactional
    public void updateNonExistingUserProfile() throws Exception {
        int databaseSizeBeforeUpdate = userProfileRepository.findAll().size();

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserProfileMockMvc.perform(put("/api/user-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userProfileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserProfile in the database
        List<UserProfile> userProfileList = userProfileRepository.findAll();
        assertThat(userProfileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserProfile in Elasticsearch
        verify(mockUserProfileSearchRepository, times(0)).save(userProfile);
    }

    @Test
    @Transactional
    public void deleteUserProfile() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);

        int databaseSizeBeforeDelete = userProfileRepository.findAll().size();

        // Get the userProfile
        restUserProfileMockMvc.perform(delete("/api/user-profiles/{id}", userProfile.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UserProfile> userProfileList = userProfileRepository.findAll();
        assertThat(userProfileList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserProfile in Elasticsearch
        verify(mockUserProfileSearchRepository, times(1)).deleteById(userProfile.getId());
    }

    @Test
    @Transactional
    public void searchUserProfile() throws Exception {
        // Initialize the database
        userProfileRepository.saveAndFlush(userProfile);
        when(mockUserProfileSearchRepository.search(queryStringQuery("id:" + userProfile.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(userProfile), PageRequest.of(0, 1), 1));
        // Search the userProfile
        restUserProfileMockMvc.perform(get("/api/_search/user-profiles?query=id:" + userProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].addressLongitude").value(hasItem(DEFAULT_ADDRESS_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].addressLatitude").value(hasItem(DEFAULT_ADDRESS_LATITUDE.doubleValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfile.class);
        UserProfile userProfile1 = new UserProfile();
        userProfile1.setId(1L);
        UserProfile userProfile2 = new UserProfile();
        userProfile2.setId(userProfile1.getId());
        assertThat(userProfile1).isEqualTo(userProfile2);
        userProfile2.setId(2L);
        assertThat(userProfile1).isNotEqualTo(userProfile2);
        userProfile1.setId(null);
        assertThat(userProfile1).isNotEqualTo(userProfile2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfileDTO.class);
        UserProfileDTO userProfileDTO1 = new UserProfileDTO();
        userProfileDTO1.setId(1L);
        UserProfileDTO userProfileDTO2 = new UserProfileDTO();
        assertThat(userProfileDTO1).isNotEqualTo(userProfileDTO2);
        userProfileDTO2.setId(userProfileDTO1.getId());
        assertThat(userProfileDTO1).isEqualTo(userProfileDTO2);
        userProfileDTO2.setId(2L);
        assertThat(userProfileDTO1).isNotEqualTo(userProfileDTO2);
        userProfileDTO1.setId(null);
        assertThat(userProfileDTO1).isNotEqualTo(userProfileDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userProfileMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userProfileMapper.fromId(null)).isNull();
    }
}
