package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Message;
import com.usemodj.nodesoft.repository.MessageRepository;
import com.usemodj.nodesoft.repository.search.MessageSearchRepository;

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
 * Test class for the MessageResource REST controller.
 *
 * @see MessageResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class MessageResourceTest {

    private static final String DEFAULT_CONTENT = "SAMPLE_TEXT";
    private static final String UPDATED_CONTENT = "UPDATED_TEXT";

    private static final Boolean DEFAULT_ROOT = false;
    private static final Boolean UPDATED_ROOT = true;
    private static final String DEFAULT_IPADDRESS = "SAMPLE_TEXT";
    private static final String UPDATED_IPADDRESS = "UPDATED_TEXT";

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private MessageSearchRepository messageSearchRepository;

    private MockMvc restMessageMockMvc;

    private Message message;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MessageResource messageResource = new MessageResource();
        ReflectionTestUtils.setField(messageResource, "messageRepository", messageRepository);
        ReflectionTestUtils.setField(messageResource, "messageSearchRepository", messageSearchRepository);
        this.restMessageMockMvc = MockMvcBuilders.standaloneSetup(messageResource).build();
    }

    @Before
    public void initTest() {
        message = new Message();
        message.setContent(DEFAULT_CONTENT);
        message.setRoot(DEFAULT_ROOT);
        message.setIpaddress(DEFAULT_IPADDRESS);
    }

    @Test
    @Transactional
    public void createMessage() throws Exception {
        int databaseSizeBeforeCreate = messageRepository.findAll().size();

        // Create the Message
        restMessageMockMvc.perform(post("/api/messages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(message)))
                .andExpect(status().isCreated());

        // Validate the Message in the database
        List<Message> messages = messageRepository.findAll();
        assertThat(messages).hasSize(databaseSizeBeforeCreate + 1);
        Message testMessage = messages.get(messages.size() - 1);
        assertThat(testMessage.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testMessage.getRoot()).isEqualTo(DEFAULT_ROOT);
        assertThat(testMessage.getIpaddress()).isEqualTo(DEFAULT_IPADDRESS);
    }

    @Test
    @Transactional
    public void checkContentIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(messageRepository.findAll()).hasSize(0);
        // set the field null
        message.setContent(null);

        // Create the Message, which fails.
        restMessageMockMvc.perform(post("/api/messages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(message)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Message> messages = messageRepository.findAll();
        assertThat(messages).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllMessages() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messages
        restMessageMockMvc.perform(get("/api/messages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
                .andExpect(jsonPath("$.[*].root").value(hasItem(DEFAULT_ROOT.booleanValue())))
                .andExpect(jsonPath("$.[*].ipaddress").value(hasItem(DEFAULT_IPADDRESS.toString())));
    }

    @Test
    @Transactional
    public void getMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get the message
        restMessageMockMvc.perform(get("/api/messages/{id}", message.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(message.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.root").value(DEFAULT_ROOT.booleanValue()))
            .andExpect(jsonPath("$.ipaddress").value(DEFAULT_IPADDRESS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMessage() throws Exception {
        // Get the message
        restMessageMockMvc.perform(get("/api/messages/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

		int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message
        message.setContent(UPDATED_CONTENT);
        message.setRoot(UPDATED_ROOT);
        message.setIpaddress(UPDATED_IPADDRESS);
        restMessageMockMvc.perform(put("/api/messages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(message)))
                .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messages = messageRepository.findAll();
        assertThat(messages).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messages.get(messages.size() - 1);
        assertThat(testMessage.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testMessage.getRoot()).isEqualTo(UPDATED_ROOT);
        assertThat(testMessage.getIpaddress()).isEqualTo(UPDATED_IPADDRESS);
    }

    @Test
    @Transactional
    public void deleteMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

		int databaseSizeBeforeDelete = messageRepository.findAll().size();

        // Get the message
        restMessageMockMvc.perform(delete("/api/messages/{id}", message.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Message> messages = messageRepository.findAll();
        assertThat(messages).hasSize(databaseSizeBeforeDelete - 1);
    }
}
