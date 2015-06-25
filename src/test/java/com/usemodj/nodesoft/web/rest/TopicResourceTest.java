package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Topic;
import com.usemodj.nodesoft.repository.TopicRepository;
import com.usemodj.nodesoft.repository.search.TopicSearchRepository;

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
 * Test class for the TopicResource REST controller.
 *
 * @see TopicResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TopicResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Integer DEFAULT_VIEWS = 0;
    private static final Integer UPDATED_VIEWS = 1;

    private static final Integer DEFAULT_REPLIES = 0;
    private static final Integer UPDATED_REPLIES = 1;

    private static final Boolean DEFAULT_LOCKED = false;
    private static final Boolean UPDATED_LOCKED = true;

    private static final Boolean DEFAULT_STICKY = false;
    private static final Boolean UPDATED_STICKY = true;

    @Inject
    private TopicRepository topicRepository;

    @Inject
    private TopicSearchRepository topicSearchRepository;

    private MockMvc restTopicMockMvc;

    private Topic topic;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TopicResource topicResource = new TopicResource();
        ReflectionTestUtils.setField(topicResource, "topicRepository", topicRepository);
        ReflectionTestUtils.setField(topicResource, "topicSearchRepository", topicSearchRepository);
        this.restTopicMockMvc = MockMvcBuilders.standaloneSetup(topicResource).build();
    }

    @Before
    public void initTest() {
        topic = new Topic();
        topic.setName(DEFAULT_NAME);
        topic.setViews(DEFAULT_VIEWS);
        topic.setReplies(DEFAULT_REPLIES);
        topic.setLocked(DEFAULT_LOCKED);
        topic.setSticky(DEFAULT_STICKY);
    }

    @Test
    @Transactional
    public void createTopic() throws Exception {
        int databaseSizeBeforeCreate = topicRepository.findAll().size();

        // Create the Topic
        restTopicMockMvc.perform(post("/api/topics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(topic)))
                .andExpect(status().isCreated());

        // Validate the Topic in the database
        List<Topic> topics = topicRepository.findAll();
        assertThat(topics).hasSize(databaseSizeBeforeCreate + 1);
        Topic testTopic = topics.get(topics.size() - 1);
        assertThat(testTopic.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTopic.getViews()).isEqualTo(DEFAULT_VIEWS);
        assertThat(testTopic.getReplies()).isEqualTo(DEFAULT_REPLIES);
        assertThat(testTopic.getLocked()).isEqualTo(DEFAULT_LOCKED);
        assertThat(testTopic.getSticky()).isEqualTo(DEFAULT_STICKY);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(topicRepository.findAll()).hasSize(0);
        // set the field null
        topic.setName(null);

        // Create the Topic, which fails.
        restTopicMockMvc.perform(post("/api/topics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(topic)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Topic> topics = topicRepository.findAll();
        assertThat(topics).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllTopics() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topics
        restTopicMockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(topic.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS)))
                .andExpect(jsonPath("$.[*].replies").value(hasItem(DEFAULT_REPLIES)))
                .andExpect(jsonPath("$.[*].locked").value(hasItem(DEFAULT_LOCKED.booleanValue())))
                .andExpect(jsonPath("$.[*].sticky").value(hasItem(DEFAULT_STICKY.booleanValue())));
    }

    @Test
    @Transactional
    public void getTopic() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get the topic
        restTopicMockMvc.perform(get("/api/topics/{id}", topic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(topic.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.views").value(DEFAULT_VIEWS))
            .andExpect(jsonPath("$.replies").value(DEFAULT_REPLIES))
            .andExpect(jsonPath("$.locked").value(DEFAULT_LOCKED.booleanValue()))
            .andExpect(jsonPath("$.sticky").value(DEFAULT_STICKY.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTopic() throws Exception {
        // Get the topic
        restTopicMockMvc.perform(get("/api/topics/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTopic() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

		int databaseSizeBeforeUpdate = topicRepository.findAll().size();

        // Update the topic
        topic.setName(UPDATED_NAME);
        topic.setViews(UPDATED_VIEWS);
        topic.setReplies(UPDATED_REPLIES);
        topic.setLocked(UPDATED_LOCKED);
        topic.setSticky(UPDATED_STICKY);
        restTopicMockMvc.perform(put("/api/topics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(topic)))
                .andExpect(status().isOk());

        // Validate the Topic in the database
        List<Topic> topics = topicRepository.findAll();
        assertThat(topics).hasSize(databaseSizeBeforeUpdate);
        Topic testTopic = topics.get(topics.size() - 1);
        assertThat(testTopic.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTopic.getViews()).isEqualTo(UPDATED_VIEWS);
        assertThat(testTopic.getReplies()).isEqualTo(UPDATED_REPLIES);
        assertThat(testTopic.getLocked()).isEqualTo(UPDATED_LOCKED);
        assertThat(testTopic.getSticky()).isEqualTo(UPDATED_STICKY);
    }

    @Test
    @Transactional
    public void deleteTopic() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

		int databaseSizeBeforeDelete = topicRepository.findAll().size();

        // Get the topic
        restTopicMockMvc.perform(delete("/api/topics/{id}", topic.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Topic> topics = topicRepository.findAll();
        assertThat(topics).hasSize(databaseSizeBeforeDelete - 1);
    }
}
