package com.bluehoodie.midup.web.rest;

import com.bluehoodie.midup.MidupApp;

import com.bluehoodie.midup.domain.Friend;
import com.bluehoodie.midup.domain.UserProfile;
import com.bluehoodie.midup.domain.UserProfile;
import com.bluehoodie.midup.repository.FriendRepository;
import com.bluehoodie.midup.repository.search.FriendSearchRepository;
import com.bluehoodie.midup.service.FriendService;
import com.bluehoodie.midup.service.UserProfileService;
import com.bluehoodie.midup.service.UserService;
import com.bluehoodie.midup.service.dto.FriendDTO;
import com.bluehoodie.midup.service.mapper.FriendMapper;
import com.bluehoodie.midup.web.rest.errors.ExceptionTranslator;
import com.bluehoodie.midup.service.dto.FriendCriteria;
import com.bluehoodie.midup.service.FriendQueryService;

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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Test class for the FriendResource REST controller.
 *
 * @see FriendResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MidupApp.class)
public class FriendResourceIntTest {

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_FRIENDSHIP_REQUEST_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FRIENDSHIP_REQUEST_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FRIENDSHIP_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FRIENDSHIP_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    /**
     * This repository is mocked in the com.bluehoodie.midup.repository.search test package.
     *
     * @see com.bluehoodie.midup.repository.search.FriendSearchRepositoryMockConfiguration
     */
    @Autowired
    private FriendSearchRepository mockFriendSearchRepository;

    @Autowired
    private FriendQueryService friendQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restFriendMockMvc;

    private Friend friend;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FriendResource friendResource = new FriendResource(friendService, friendQueryService, userService, userProfileService);
        this.restFriendMockMvc = MockMvcBuilders.standaloneSetup(friendResource)
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
    public static Friend createEntity(EntityManager em) {
        Friend friend = new Friend()
            .status(DEFAULT_STATUS)
            .friendshipRequestDate(DEFAULT_FRIENDSHIP_REQUEST_DATE)
            .friendshipStartDate(DEFAULT_FRIENDSHIP_START_DATE);
        return friend;
    }

    @Before
    public void initTest() {
        friend = createEntity(em);
    }

    @Test
    @Transactional
    public void createFriend() throws Exception {
        int databaseSizeBeforeCreate = friendRepository.findAll().size();

        // Create the Friend
        FriendDTO friendDTO = friendMapper.toDto(friend);
        restFriendMockMvc.perform(post("/api/friends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(friendDTO)))
            .andExpect(status().isCreated());

        // Validate the Friend in the database
        List<Friend> friendList = friendRepository.findAll();
        assertThat(friendList).hasSize(databaseSizeBeforeCreate + 1);
        Friend testFriend = friendList.get(friendList.size() - 1);
        assertThat(testFriend.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testFriend.getFriendshipRequestDate()).isEqualTo(DEFAULT_FRIENDSHIP_REQUEST_DATE);
        assertThat(testFriend.getFriendshipStartDate()).isEqualTo(DEFAULT_FRIENDSHIP_START_DATE);

        // Validate the Friend in Elasticsearch
        verify(mockFriendSearchRepository, times(1)).save(testFriend);
    }

    @Test
    @Transactional
    public void createFriendWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = friendRepository.findAll().size();

        // Create the Friend with an existing ID
        friend.setId(1L);
        FriendDTO friendDTO = friendMapper.toDto(friend);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFriendMockMvc.perform(post("/api/friends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(friendDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Friend in the database
        List<Friend> friendList = friendRepository.findAll();
        assertThat(friendList).hasSize(databaseSizeBeforeCreate);

        // Validate the Friend in Elasticsearch
        verify(mockFriendSearchRepository, times(0)).save(friend);
    }

    @Test
    @Transactional
    public void getAllFriends() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get all the friendList
        restFriendMockMvc.perform(get("/api/friends?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(friend.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].friendshipRequestDate").value(hasItem(DEFAULT_FRIENDSHIP_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].friendshipStartDate").value(hasItem(DEFAULT_FRIENDSHIP_START_DATE.toString())));
    }

    @Test
    @Transactional
    public void getFriend() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get the friend
        restFriendMockMvc.perform(get("/api/friends/{id}", friend.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(friend.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.friendshipRequestDate").value(DEFAULT_FRIENDSHIP_REQUEST_DATE.toString()))
            .andExpect(jsonPath("$.friendshipStartDate").value(DEFAULT_FRIENDSHIP_START_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllFriendsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get all the friendList where status equals to DEFAULT_STATUS
        defaultFriendShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the friendList where status equals to UPDATED_STATUS
        defaultFriendShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllFriendsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get all the friendList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultFriendShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the friendList where status equals to UPDATED_STATUS
        defaultFriendShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllFriendsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get all the friendList where status is not null
        defaultFriendShouldBeFound("status.specified=true");

        // Get all the friendList where status is null
        defaultFriendShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllFriendsByFriendshipRequestDateIsEqualToSomething() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get all the friendList where friendshipRequestDate equals to DEFAULT_FRIENDSHIP_REQUEST_DATE
        defaultFriendShouldBeFound("friendshipRequestDate.equals=" + DEFAULT_FRIENDSHIP_REQUEST_DATE);

        // Get all the friendList where friendshipRequestDate equals to UPDATED_FRIENDSHIP_REQUEST_DATE
        defaultFriendShouldNotBeFound("friendshipRequestDate.equals=" + UPDATED_FRIENDSHIP_REQUEST_DATE);
    }

    @Test
    @Transactional
    public void getAllFriendsByFriendshipRequestDateIsInShouldWork() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get all the friendList where friendshipRequestDate in DEFAULT_FRIENDSHIP_REQUEST_DATE or UPDATED_FRIENDSHIP_REQUEST_DATE
        defaultFriendShouldBeFound("friendshipRequestDate.in=" + DEFAULT_FRIENDSHIP_REQUEST_DATE + "," + UPDATED_FRIENDSHIP_REQUEST_DATE);

        // Get all the friendList where friendshipRequestDate equals to UPDATED_FRIENDSHIP_REQUEST_DATE
        defaultFriendShouldNotBeFound("friendshipRequestDate.in=" + UPDATED_FRIENDSHIP_REQUEST_DATE);
    }

    @Test
    @Transactional
    public void getAllFriendsByFriendshipRequestDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get all the friendList where friendshipRequestDate is not null
        defaultFriendShouldBeFound("friendshipRequestDate.specified=true");

        // Get all the friendList where friendshipRequestDate is null
        defaultFriendShouldNotBeFound("friendshipRequestDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllFriendsByFriendshipStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get all the friendList where friendshipStartDate equals to DEFAULT_FRIENDSHIP_START_DATE
        defaultFriendShouldBeFound("friendshipStartDate.equals=" + DEFAULT_FRIENDSHIP_START_DATE);

        // Get all the friendList where friendshipStartDate equals to UPDATED_FRIENDSHIP_START_DATE
        defaultFriendShouldNotBeFound("friendshipStartDate.equals=" + UPDATED_FRIENDSHIP_START_DATE);
    }

    @Test
    @Transactional
    public void getAllFriendsByFriendshipStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get all the friendList where friendshipStartDate in DEFAULT_FRIENDSHIP_START_DATE or UPDATED_FRIENDSHIP_START_DATE
        defaultFriendShouldBeFound("friendshipStartDate.in=" + DEFAULT_FRIENDSHIP_START_DATE + "," + UPDATED_FRIENDSHIP_START_DATE);

        // Get all the friendList where friendshipStartDate equals to UPDATED_FRIENDSHIP_START_DATE
        defaultFriendShouldNotBeFound("friendshipStartDate.in=" + UPDATED_FRIENDSHIP_START_DATE);
    }

    @Test
    @Transactional
    public void getAllFriendsByFriendshipStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        // Get all the friendList where friendshipStartDate is not null
        defaultFriendShouldBeFound("friendshipStartDate.specified=true");

        // Get all the friendList where friendshipStartDate is null
        defaultFriendShouldNotBeFound("friendshipStartDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllFriendsByFriendRequestingIsEqualToSomething() throws Exception {
        // Initialize the database
        UserProfile friendRequesting = UserProfileResourceIntTest.createEntity(em);
        em.persist(friendRequesting);
        em.flush();
        friend.setFriendRequesting(friendRequesting);
        friendRepository.saveAndFlush(friend);
        Long friendRequestingId = friendRequesting.getId();

        // Get all the friendList where friendRequesting equals to friendRequestingId
        defaultFriendShouldBeFound("friendRequestingId.equals=" + friendRequestingId);

        // Get all the friendList where friendRequesting equals to friendRequestingId + 1
        defaultFriendShouldNotBeFound("friendRequestingId.equals=" + (friendRequestingId + 1));
    }


    @Test
    @Transactional
    public void getAllFriendsByFriendAcceptingIsEqualToSomething() throws Exception {
        // Initialize the database
        UserProfile friendAccepting = UserProfileResourceIntTest.createEntity(em);
        em.persist(friendAccepting);
        em.flush();
        friend.setFriendAccepting(friendAccepting);
        friendRepository.saveAndFlush(friend);
        Long friendAcceptingId = friendAccepting.getId();

        // Get all the friendList where friendAccepting equals to friendAcceptingId
        defaultFriendShouldBeFound("friendAcceptingId.equals=" + friendAcceptingId);

        // Get all the friendList where friendAccepting equals to friendAcceptingId + 1
        defaultFriendShouldNotBeFound("friendAcceptingId.equals=" + (friendAcceptingId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultFriendShouldBeFound(String filter) throws Exception {
        restFriendMockMvc.perform(get("/api/friends?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(friend.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].friendshipRequestDate").value(hasItem(DEFAULT_FRIENDSHIP_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].friendshipStartDate").value(hasItem(DEFAULT_FRIENDSHIP_START_DATE.toString())));

        // Check, that the count call also returns 1
        restFriendMockMvc.perform(get("/api/friends/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultFriendShouldNotBeFound(String filter) throws Exception {
        restFriendMockMvc.perform(get("/api/friends?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFriendMockMvc.perform(get("/api/friends/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingFriend() throws Exception {
        // Get the friend
        restFriendMockMvc.perform(get("/api/friends/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFriend() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        int databaseSizeBeforeUpdate = friendRepository.findAll().size();

        // Update the friend
        Friend updatedFriend = friendRepository.findById(friend.getId()).get();
        // Disconnect from session so that the updates on updatedFriend are not directly saved in db
        em.detach(updatedFriend);
        updatedFriend
            .status(UPDATED_STATUS)
            .friendshipRequestDate(UPDATED_FRIENDSHIP_REQUEST_DATE)
            .friendshipStartDate(UPDATED_FRIENDSHIP_START_DATE);
        FriendDTO friendDTO = friendMapper.toDto(updatedFriend);

        restFriendMockMvc.perform(put("/api/friends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(friendDTO)))
            .andExpect(status().isOk());

        // Validate the Friend in the database
        List<Friend> friendList = friendRepository.findAll();
        assertThat(friendList).hasSize(databaseSizeBeforeUpdate);
        Friend testFriend = friendList.get(friendList.size() - 1);
        assertThat(testFriend.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFriend.getFriendshipRequestDate()).isEqualTo(UPDATED_FRIENDSHIP_REQUEST_DATE);
        assertThat(testFriend.getFriendshipStartDate()).isEqualTo(UPDATED_FRIENDSHIP_START_DATE);

        // Validate the Friend in Elasticsearch
        verify(mockFriendSearchRepository, times(1)).save(testFriend);
    }

    @Test
    @Transactional
    public void updateNonExistingFriend() throws Exception {
        int databaseSizeBeforeUpdate = friendRepository.findAll().size();

        // Create the Friend
        FriendDTO friendDTO = friendMapper.toDto(friend);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFriendMockMvc.perform(put("/api/friends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(friendDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Friend in the database
        List<Friend> friendList = friendRepository.findAll();
        assertThat(friendList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Friend in Elasticsearch
        verify(mockFriendSearchRepository, times(0)).save(friend);
    }

    @Test
    @Transactional
    public void deleteFriend() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);

        int databaseSizeBeforeDelete = friendRepository.findAll().size();

        // Get the friend
        restFriendMockMvc.perform(delete("/api/friends/{id}", friend.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Friend> friendList = friendRepository.findAll();
        assertThat(friendList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Friend in Elasticsearch
        verify(mockFriendSearchRepository, times(1)).deleteById(friend.getId());
    }

    @Test
    @Transactional
    public void searchFriend() throws Exception {
        // Initialize the database
        friendRepository.saveAndFlush(friend);
        when(mockFriendSearchRepository.search(queryStringQuery("id:" + friend.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(friend), PageRequest.of(0, 1), 1));
        // Search the friend
        restFriendMockMvc.perform(get("/api/_search/friends?query=id:" + friend.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(friend.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].friendshipRequestDate").value(hasItem(DEFAULT_FRIENDSHIP_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].friendshipStartDate").value(hasItem(DEFAULT_FRIENDSHIP_START_DATE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Friend.class);
        Friend friend1 = new Friend();
        friend1.setId(1L);
        Friend friend2 = new Friend();
        friend2.setId(friend1.getId());
        assertThat(friend1).isEqualTo(friend2);
        friend2.setId(2L);
        assertThat(friend1).isNotEqualTo(friend2);
        friend1.setId(null);
        assertThat(friend1).isNotEqualTo(friend2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FriendDTO.class);
        FriendDTO friendDTO1 = new FriendDTO();
        friendDTO1.setId(1L);
        FriendDTO friendDTO2 = new FriendDTO();
        assertThat(friendDTO1).isNotEqualTo(friendDTO2);
        friendDTO2.setId(friendDTO1.getId());
        assertThat(friendDTO1).isEqualTo(friendDTO2);
        friendDTO2.setId(2L);
        assertThat(friendDTO1).isNotEqualTo(friendDTO2);
        friendDTO1.setId(null);
        assertThat(friendDTO1).isNotEqualTo(friendDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(friendMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(friendMapper.fromId(null)).isNull();
    }
}
