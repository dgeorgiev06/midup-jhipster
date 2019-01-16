package com.bluehoodie.midup.web.rest;

import com.bluehoodie.midup.MidupApp;

import com.bluehoodie.midup.domain.Invitee;
import com.bluehoodie.midup.domain.UserProfile;
import com.bluehoodie.midup.domain.Event;
import com.bluehoodie.midup.repository.InviteeRepository;
import com.bluehoodie.midup.repository.search.InviteeSearchRepository;
import com.bluehoodie.midup.service.InviteeService;
import com.bluehoodie.midup.service.dto.InviteeDTO;
import com.bluehoodie.midup.service.mapper.InviteeMapper;
import com.bluehoodie.midup.web.rest.errors.ExceptionTranslator;
import com.bluehoodie.midup.service.dto.InviteeCriteria;
import com.bluehoodie.midup.service.InviteeQueryService;

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
 * Test class for the InviteeResource REST controller.
 *
 * @see InviteeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MidupApp.class)
public class InviteeResourceIntTest {

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    @Autowired
    private InviteeRepository inviteeRepository;

    @Autowired
    private InviteeMapper inviteeMapper;
    
    @Autowired
    private InviteeService inviteeService;

    /**
     * This repository is mocked in the com.bluehoodie.midup.repository.search test package.
     *
     * @see com.bluehoodie.midup.repository.search.InviteeSearchRepositoryMockConfiguration
     */
    @Autowired
    private InviteeSearchRepository mockInviteeSearchRepository;

    @Autowired
    private InviteeQueryService inviteeQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restInviteeMockMvc;

    private Invitee invitee;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final InviteeResource inviteeResource = new InviteeResource(inviteeService, inviteeQueryService);
        this.restInviteeMockMvc = MockMvcBuilders.standaloneSetup(inviteeResource)
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
    public static Invitee createEntity(EntityManager em) {
        Invitee invitee = new Invitee()
            .status(DEFAULT_STATUS);
        return invitee;
    }

    @Before
    public void initTest() {
        invitee = createEntity(em);
    }

    @Test
    @Transactional
    public void createInvitee() throws Exception {
        int databaseSizeBeforeCreate = inviteeRepository.findAll().size();

        // Create the Invitee
        InviteeDTO inviteeDTO = inviteeMapper.toDto(invitee);
        restInviteeMockMvc.perform(post("/api/invitees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(inviteeDTO)))
            .andExpect(status().isCreated());

        // Validate the Invitee in the database
        List<Invitee> inviteeList = inviteeRepository.findAll();
        assertThat(inviteeList).hasSize(databaseSizeBeforeCreate + 1);
        Invitee testInvitee = inviteeList.get(inviteeList.size() - 1);
        assertThat(testInvitee.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Invitee in Elasticsearch
        verify(mockInviteeSearchRepository, times(1)).save(testInvitee);
    }

    @Test
    @Transactional
    public void createInviteeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = inviteeRepository.findAll().size();

        // Create the Invitee with an existing ID
        invitee.setId(1L);
        InviteeDTO inviteeDTO = inviteeMapper.toDto(invitee);

        // An entity with an existing ID cannot be created, so this API call must fail
        restInviteeMockMvc.perform(post("/api/invitees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(inviteeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Invitee in the database
        List<Invitee> inviteeList = inviteeRepository.findAll();
        assertThat(inviteeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Invitee in Elasticsearch
        verify(mockInviteeSearchRepository, times(0)).save(invitee);
    }

    @Test
    @Transactional
    public void getAllInvitees() throws Exception {
        // Initialize the database
        inviteeRepository.saveAndFlush(invitee);

        // Get all the inviteeList
        restInviteeMockMvc.perform(get("/api/invitees?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invitee.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }
    
    @Test
    @Transactional
    public void getInvitee() throws Exception {
        // Initialize the database
        inviteeRepository.saveAndFlush(invitee);

        // Get the invitee
        restInviteeMockMvc.perform(get("/api/invitees/{id}", invitee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(invitee.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    public void getAllInviteesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        inviteeRepository.saveAndFlush(invitee);

        // Get all the inviteeList where status equals to DEFAULT_STATUS
        defaultInviteeShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the inviteeList where status equals to UPDATED_STATUS
        defaultInviteeShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllInviteesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        inviteeRepository.saveAndFlush(invitee);

        // Get all the inviteeList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultInviteeShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the inviteeList where status equals to UPDATED_STATUS
        defaultInviteeShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllInviteesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        inviteeRepository.saveAndFlush(invitee);

        // Get all the inviteeList where status is not null
        defaultInviteeShouldBeFound("status.specified=true");

        // Get all the inviteeList where status is null
        defaultInviteeShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllInviteesByStatusIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inviteeRepository.saveAndFlush(invitee);

        // Get all the inviteeList where status greater than or equals to DEFAULT_STATUS
        defaultInviteeShouldBeFound("status.greaterOrEqualThan=" + DEFAULT_STATUS);

        // Get all the inviteeList where status greater than or equals to UPDATED_STATUS
        defaultInviteeShouldNotBeFound("status.greaterOrEqualThan=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllInviteesByStatusIsLessThanSomething() throws Exception {
        // Initialize the database
        inviteeRepository.saveAndFlush(invitee);

        // Get all the inviteeList where status less than or equals to DEFAULT_STATUS
        defaultInviteeShouldNotBeFound("status.lessThan=" + DEFAULT_STATUS);

        // Get all the inviteeList where status less than or equals to UPDATED_STATUS
        defaultInviteeShouldBeFound("status.lessThan=" + UPDATED_STATUS);
    }


    @Test
    @Transactional
    public void getAllInviteesByUserProfileIsEqualToSomething() throws Exception {
        // Initialize the database
        UserProfile userProfile = UserProfileResourceIntTest.createEntity(em);
        em.persist(userProfile);
        em.flush();
        invitee.setUserProfile(userProfile);
        inviteeRepository.saveAndFlush(invitee);
        Long userProfileId = userProfile.getId();

        // Get all the inviteeList where userProfile equals to userProfileId
        defaultInviteeShouldBeFound("userProfileId.equals=" + userProfileId);

        // Get all the inviteeList where userProfile equals to userProfileId + 1
        defaultInviteeShouldNotBeFound("userProfileId.equals=" + (userProfileId + 1));
    }


    @Test
    @Transactional
    public void getAllInviteesByEventIsEqualToSomething() throws Exception {
        // Initialize the database
        Event event = EventResourceIntTest.createEntity(em);
        em.persist(event);
        em.flush();
        invitee.setEvent(event);
        inviteeRepository.saveAndFlush(invitee);
        Long eventId = event.getId();

        // Get all the inviteeList where event equals to eventId
        defaultInviteeShouldBeFound("eventId.equals=" + eventId);

        // Get all the inviteeList where event equals to eventId + 1
        defaultInviteeShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultInviteeShouldBeFound(String filter) throws Exception {
        restInviteeMockMvc.perform(get("/api/invitees?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invitee.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));

        // Check, that the count call also returns 1
        restInviteeMockMvc.perform(get("/api/invitees/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultInviteeShouldNotBeFound(String filter) throws Exception {
        restInviteeMockMvc.perform(get("/api/invitees?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInviteeMockMvc.perform(get("/api/invitees/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingInvitee() throws Exception {
        // Get the invitee
        restInviteeMockMvc.perform(get("/api/invitees/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInvitee() throws Exception {
        // Initialize the database
        inviteeRepository.saveAndFlush(invitee);

        int databaseSizeBeforeUpdate = inviteeRepository.findAll().size();

        // Update the invitee
        Invitee updatedInvitee = inviteeRepository.findById(invitee.getId()).get();
        // Disconnect from session so that the updates on updatedInvitee are not directly saved in db
        em.detach(updatedInvitee);
        updatedInvitee
            .status(UPDATED_STATUS);
        InviteeDTO inviteeDTO = inviteeMapper.toDto(updatedInvitee);

        restInviteeMockMvc.perform(put("/api/invitees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(inviteeDTO)))
            .andExpect(status().isOk());

        // Validate the Invitee in the database
        List<Invitee> inviteeList = inviteeRepository.findAll();
        assertThat(inviteeList).hasSize(databaseSizeBeforeUpdate);
        Invitee testInvitee = inviteeList.get(inviteeList.size() - 1);
        assertThat(testInvitee.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Invitee in Elasticsearch
        verify(mockInviteeSearchRepository, times(1)).save(testInvitee);
    }

    @Test
    @Transactional
    public void updateNonExistingInvitee() throws Exception {
        int databaseSizeBeforeUpdate = inviteeRepository.findAll().size();

        // Create the Invitee
        InviteeDTO inviteeDTO = inviteeMapper.toDto(invitee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInviteeMockMvc.perform(put("/api/invitees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(inviteeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Invitee in the database
        List<Invitee> inviteeList = inviteeRepository.findAll();
        assertThat(inviteeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Invitee in Elasticsearch
        verify(mockInviteeSearchRepository, times(0)).save(invitee);
    }

    @Test
    @Transactional
    public void deleteInvitee() throws Exception {
        // Initialize the database
        inviteeRepository.saveAndFlush(invitee);

        int databaseSizeBeforeDelete = inviteeRepository.findAll().size();

        // Get the invitee
        restInviteeMockMvc.perform(delete("/api/invitees/{id}", invitee.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Invitee> inviteeList = inviteeRepository.findAll();
        assertThat(inviteeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Invitee in Elasticsearch
        verify(mockInviteeSearchRepository, times(1)).deleteById(invitee.getId());
    }

    @Test
    @Transactional
    public void searchInvitee() throws Exception {
        // Initialize the database
        inviteeRepository.saveAndFlush(invitee);
        when(mockInviteeSearchRepository.search(queryStringQuery("id:" + invitee.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(invitee), PageRequest.of(0, 1), 1));
        // Search the invitee
        restInviteeMockMvc.perform(get("/api/_search/invitees?query=id:" + invitee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invitee.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Invitee.class);
        Invitee invitee1 = new Invitee();
        invitee1.setId(1L);
        Invitee invitee2 = new Invitee();
        invitee2.setId(invitee1.getId());
        assertThat(invitee1).isEqualTo(invitee2);
        invitee2.setId(2L);
        assertThat(invitee1).isNotEqualTo(invitee2);
        invitee1.setId(null);
        assertThat(invitee1).isNotEqualTo(invitee2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InviteeDTO.class);
        InviteeDTO inviteeDTO1 = new InviteeDTO();
        inviteeDTO1.setId(1L);
        InviteeDTO inviteeDTO2 = new InviteeDTO();
        assertThat(inviteeDTO1).isNotEqualTo(inviteeDTO2);
        inviteeDTO2.setId(inviteeDTO1.getId());
        assertThat(inviteeDTO1).isEqualTo(inviteeDTO2);
        inviteeDTO2.setId(2L);
        assertThat(inviteeDTO1).isNotEqualTo(inviteeDTO2);
        inviteeDTO1.setId(null);
        assertThat(inviteeDTO1).isNotEqualTo(inviteeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(inviteeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(inviteeMapper.fromId(null)).isNull();
    }
}
