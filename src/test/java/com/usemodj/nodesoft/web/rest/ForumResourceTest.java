package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Forum;
import com.usemodj.nodesoft.repository.ForumRepository;
import com.usemodj.nodesoft.repository.search.ForumSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ForumResource REST controller.
 *
 * @see ForumResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ForumResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final Integer DEFAULT_DISPLAY = 0;
    private static final Integer UPDATED_DISPLAY = 1;

    private static final Integer DEFAULT_LFT = 0;
    private static final Integer UPDATED_LFT = 1;

    private static final Integer DEFAULT_RGT = 0;
    private static final Integer UPDATED_RGT = 1;

    private static final Integer DEFAULT_POST_COUNT = 0;
    private static final Integer UPDATED_POST_COUNT = 1;

    private static final Integer DEFAULT_TOPIC_COUNT = 0;
    private static final Integer UPDATED_TOPIC_COUNT = 1;

    private static final Boolean DEFAULT_LOCKED = false;
    private static final Boolean UPDATED_LOCKED = true;

    @Inject
    private ForumRepository forumRepository;

    @Inject
    private ForumSearchRepository forumSearchRepository;

    private MockMvc restForumMockMvc;

    private Forum forum;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ForumResource forumResource = new ForumResource();
        ReflectionTestUtils.setField(forumResource, "forumRepository", forumRepository);
        ReflectionTestUtils.setField(forumResource, "forumSearchRepository", forumSearchRepository);
        this.restForumMockMvc = MockMvcBuilders.standaloneSetup(forumResource).build();
    }

    @Before
    public void initTest() {
        forum = new Forum();
        forum.setName(DEFAULT_NAME);
        forum.setDescription(DEFAULT_DESCRIPTION);
        forum.setDisplay(DEFAULT_DISPLAY);
        forum.setLft(DEFAULT_LFT);
        forum.setRgt(DEFAULT_RGT);
        forum.setPostCount(DEFAULT_POST_COUNT);
        forum.setTopicCount(DEFAULT_TOPIC_COUNT);
        forum.setLocked(DEFAULT_LOCKED);
    }

    @Test
    @Transactional
    public void createForum() throws Exception {
        int databaseSizeBeforeCreate = forumRepository.findAll().size();

        // Create the Forum
        restForumMockMvc.perform(post("/api/forums")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(forum)))
                .andExpect(status().isCreated());

        // Validate the Forum in the database
        List<Forum> forums = forumRepository.findAll();
        assertThat(forums).hasSize(databaseSizeBeforeCreate + 1);
        Forum testForum = forums.get(forums.size() - 1);
        assertThat(testForum.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testForum.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testForum.getDisplay()).isEqualTo(DEFAULT_DISPLAY);
        assertThat(testForum.getLft()).isEqualTo(DEFAULT_LFT);
        assertThat(testForum.getRgt()).isEqualTo(DEFAULT_RGT);
        assertThat(testForum.getPostCount()).isEqualTo(DEFAULT_POST_COUNT);
        assertThat(testForum.getTopicCount()).isEqualTo(DEFAULT_TOPIC_COUNT);
        assertThat(testForum.getLocked()).isEqualTo(DEFAULT_LOCKED);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(forumRepository.findAll()).hasSize(0);
        // set the field null
        forum.setName(null);

        // Create the Forum, which fails.
        restForumMockMvc.perform(post("/api/forums")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(forum)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Forum> forums = forumRepository.findAll();
        assertThat(forums).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllForums() throws Exception {
        // Initialize the database
        forumRepository.saveAndFlush(forum);

        // Get all the forums
        restForumMockMvc.perform(get("/api/forums"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(forum.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].display").value(hasItem(DEFAULT_DISPLAY)))
                .andExpect(jsonPath("$.[*].lft").value(hasItem(DEFAULT_LFT)))
                .andExpect(jsonPath("$.[*].rgt").value(hasItem(DEFAULT_RGT)))
                .andExpect(jsonPath("$.[*].postCount").value(hasItem(DEFAULT_POST_COUNT)))
                .andExpect(jsonPath("$.[*].topicCount").value(hasItem(DEFAULT_TOPIC_COUNT)))
                .andExpect(jsonPath("$.[*].locked").value(hasItem(DEFAULT_LOCKED.booleanValue())));
    }

    @Test
    @Transactional
    public void getForum() throws Exception {
        // Initialize the database
        forumRepository.saveAndFlush(forum);

        // Get the forum
        restForumMockMvc.perform(get("/api/forums/{id}", forum.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(forum.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.display").value(DEFAULT_DISPLAY))
            .andExpect(jsonPath("$.lft").value(DEFAULT_LFT))
            .andExpect(jsonPath("$.rgt").value(DEFAULT_RGT))
            .andExpect(jsonPath("$.postCount").value(DEFAULT_POST_COUNT))
            .andExpect(jsonPath("$.topicCount").value(DEFAULT_TOPIC_COUNT))
            .andExpect(jsonPath("$.locked").value(DEFAULT_LOCKED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingForum() throws Exception {
        // Get the forum
        restForumMockMvc.perform(get("/api/forums/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateForum() throws Exception {
        // Initialize the database
        forumRepository.saveAndFlush(forum);

		int databaseSizeBeforeUpdate = forumRepository.findAll().size();

        // Update the forum
        forum.setName(UPDATED_NAME);
        forum.setDescription(UPDATED_DESCRIPTION);
        forum.setDisplay(UPDATED_DISPLAY);
        forum.setLft(UPDATED_LFT);
        forum.setRgt(UPDATED_RGT);
        forum.setPostCount(UPDATED_POST_COUNT);
        forum.setTopicCount(UPDATED_TOPIC_COUNT);
        forum.setLocked(UPDATED_LOCKED);
        restForumMockMvc.perform(put("/api/forums")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(forum)))
                .andExpect(status().isOk());

        // Validate the Forum in the database
        List<Forum> forums = forumRepository.findAll();
        assertThat(forums).hasSize(databaseSizeBeforeUpdate);
        Forum testForum = forums.get(forums.size() - 1);
        assertThat(testForum.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testForum.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testForum.getDisplay()).isEqualTo(UPDATED_DISPLAY);
        assertThat(testForum.getLft()).isEqualTo(UPDATED_LFT);
        assertThat(testForum.getRgt()).isEqualTo(UPDATED_RGT);
        assertThat(testForum.getPostCount()).isEqualTo(UPDATED_POST_COUNT);
        assertThat(testForum.getTopicCount()).isEqualTo(UPDATED_TOPIC_COUNT);
        assertThat(testForum.getLocked()).isEqualTo(UPDATED_LOCKED);
    }

    @Test
    @Transactional
    public void deleteForum() throws Exception {
        // Initialize the database
        forumRepository.saveAndFlush(forum);

		int databaseSizeBeforeDelete = forumRepository.findAll().size();

        // Get the forum
        restForumMockMvc.perform(delete("/api/forums/{id}", forum.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Forum> forums = forumRepository.findAll();
        assertThat(forums).hasSize(databaseSizeBeforeDelete - 1);
    }
}
