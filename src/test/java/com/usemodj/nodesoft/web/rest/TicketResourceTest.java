package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Ticket;
import com.usemodj.nodesoft.repository.TicketRepository;
import com.usemodj.nodesoft.repository.search.TicketSearchRepository;

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
 * Test class for the TicketResource REST controller.
 *
 * @see TicketResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TicketResourceTest {

    private static final String DEFAULT_SUBJECT = "SAMPLE_TEXT";
    private static final String UPDATED_SUBJECT = "UPDATED_TEXT";
    private static final String DEFAULT_STATUS = "SAMPLE_TEXT";
    private static final String UPDATED_STATUS = "UPDATED_TEXT";

    private static final Integer DEFAULT_VIEWS = 0;
    private static final Integer UPDATED_VIEWS = 1;

    private static final Integer DEFAULT_REPLIES = 0;
    private static final Integer UPDATED_REPLIES = 1;

    @Inject
    private TicketRepository ticketRepository;

    @Inject
    private TicketSearchRepository ticketSearchRepository;

    private MockMvc restTicketMockMvc;

    private Ticket ticket;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TicketResource ticketResource = new TicketResource();
        ReflectionTestUtils.setField(ticketResource, "ticketRepository", ticketRepository);
        ReflectionTestUtils.setField(ticketResource, "ticketSearchRepository", ticketSearchRepository);
        this.restTicketMockMvc = MockMvcBuilders.standaloneSetup(ticketResource).build();
    }

    @Before
    public void initTest() {
        ticket = new Ticket();
        ticket.setSubject(DEFAULT_SUBJECT);
        ticket.setStatus(DEFAULT_STATUS);
        ticket.setViews(DEFAULT_VIEWS);
        ticket.setReplies(DEFAULT_REPLIES);
    }

    @Test
    @Transactional
    public void createTicket() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().size();

        // Create the Ticket
        restTicketMockMvc.perform(post("/api/tickets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ticket)))
                .andExpect(status().isCreated());

        // Validate the Ticket in the database
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(databaseSizeBeforeCreate + 1);
        Ticket testTicket = tickets.get(tickets.size() - 1);
        assertThat(testTicket.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testTicket.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTicket.getViews()).isEqualTo(DEFAULT_VIEWS);
        assertThat(testTicket.getReplies()).isEqualTo(DEFAULT_REPLIES);
    }

    @Test
    @Transactional
    public void checkSubjectIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(ticketRepository.findAll()).hasSize(0);
        // set the field null
        ticket.setSubject(null);

        // Create the Ticket, which fails.
        restTicketMockMvc.perform(post("/api/tickets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ticket)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllTickets() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the tickets
        restTicketMockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
                .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS)))
                .andExpect(jsonPath("$.[*].replies").value(hasItem(DEFAULT_REPLIES)));
    }

    @Test
    @Transactional
    public void getTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ticket.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.views").value(DEFAULT_VIEWS))
            .andExpect(jsonPath("$.replies").value(DEFAULT_REPLIES));
    }

    @Test
    @Transactional
    public void getNonExistingTicket() throws Exception {
        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

		int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Update the ticket
        ticket.setSubject(UPDATED_SUBJECT);
        ticket.setStatus(UPDATED_STATUS);
        ticket.setViews(UPDATED_VIEWS);
        ticket.setReplies(UPDATED_REPLIES);
        restTicketMockMvc.perform(put("/api/tickets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ticket)))
                .andExpect(status().isOk());

        // Validate the Ticket in the database
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = tickets.get(tickets.size() - 1);
        assertThat(testTicket.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testTicket.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTicket.getViews()).isEqualTo(UPDATED_VIEWS);
        assertThat(testTicket.getReplies()).isEqualTo(UPDATED_REPLIES);
    }

    @Test
    @Transactional
    public void deleteTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

		int databaseSizeBeforeDelete = ticketRepository.findAll().size();

        // Get the ticket
        restTicketMockMvc.perform(delete("/api/tickets/{id}", ticket.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(databaseSizeBeforeDelete - 1);
    }
}
