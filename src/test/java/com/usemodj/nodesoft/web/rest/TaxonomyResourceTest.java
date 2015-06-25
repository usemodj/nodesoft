package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Taxonomy;
import com.usemodj.nodesoft.repository.TaxonomyRepository;
import com.usemodj.nodesoft.repository.search.TaxonomySearchRepository;

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
 * Test class for the TaxonomyResource REST controller.
 *
 * @see TaxonomyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TaxonomyResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Integer DEFAULT_POSITION = 0;
    private static final Integer UPDATED_POSITION = 1;

    @Inject
    private TaxonomyRepository taxonomyRepository;

    @Inject
    private TaxonomySearchRepository taxonomySearchRepository;

    private MockMvc restTaxonomyMockMvc;

    private Taxonomy taxonomy;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TaxonomyResource taxonomyResource = new TaxonomyResource();
        ReflectionTestUtils.setField(taxonomyResource, "taxonomyRepository", taxonomyRepository);
        ReflectionTestUtils.setField(taxonomyResource, "taxonomySearchRepository", taxonomySearchRepository);
        this.restTaxonomyMockMvc = MockMvcBuilders.standaloneSetup(taxonomyResource).build();
    }

    @Before
    public void initTest() {
        taxonomy = new Taxonomy();
        taxonomy.setName(DEFAULT_NAME);
        taxonomy.setPosition(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void createTaxonomy() throws Exception {
        int databaseSizeBeforeCreate = taxonomyRepository.findAll().size();

        // Create the Taxonomy
        restTaxonomyMockMvc.perform(post("/api/taxonomys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(taxonomy)))
                .andExpect(status().isCreated());

        // Validate the Taxonomy in the database
        List<Taxonomy> taxonomys = taxonomyRepository.findAll();
        assertThat(taxonomys).hasSize(databaseSizeBeforeCreate + 1);
        Taxonomy testTaxonomy = taxonomys.get(taxonomys.size() - 1);
        assertThat(testTaxonomy.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTaxonomy.getPosition()).isEqualTo(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void getAllTaxonomys() throws Exception {
        // Initialize the database
        taxonomyRepository.saveAndFlush(taxonomy);

        // Get all the taxonomys
        restTaxonomyMockMvc.perform(get("/api/taxonomys"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(taxonomy.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)));
    }

    @Test
    @Transactional
    public void getTaxonomy() throws Exception {
        // Initialize the database
        taxonomyRepository.saveAndFlush(taxonomy);

        // Get the taxonomy
        restTaxonomyMockMvc.perform(get("/api/taxonomys/{id}", taxonomy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(taxonomy.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION));
    }

    @Test
    @Transactional
    public void getNonExistingTaxonomy() throws Exception {
        // Get the taxonomy
        restTaxonomyMockMvc.perform(get("/api/taxonomys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTaxonomy() throws Exception {
        // Initialize the database
        taxonomyRepository.saveAndFlush(taxonomy);

		int databaseSizeBeforeUpdate = taxonomyRepository.findAll().size();

        // Update the taxonomy
        taxonomy.setName(UPDATED_NAME);
        taxonomy.setPosition(UPDATED_POSITION);
        restTaxonomyMockMvc.perform(put("/api/taxonomys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(taxonomy)))
                .andExpect(status().isOk());

        // Validate the Taxonomy in the database
        List<Taxonomy> taxonomys = taxonomyRepository.findAll();
        assertThat(taxonomys).hasSize(databaseSizeBeforeUpdate);
        Taxonomy testTaxonomy = taxonomys.get(taxonomys.size() - 1);
        assertThat(testTaxonomy.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaxonomy.getPosition()).isEqualTo(UPDATED_POSITION);
    }

    @Test
    @Transactional
    public void deleteTaxonomy() throws Exception {
        // Initialize the database
        taxonomyRepository.saveAndFlush(taxonomy);

		int databaseSizeBeforeDelete = taxonomyRepository.findAll().size();

        // Get the taxonomy
        restTaxonomyMockMvc.perform(delete("/api/taxonomys/{id}", taxonomy.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Taxonomy> taxonomys = taxonomyRepository.findAll();
        assertThat(taxonomys).hasSize(databaseSizeBeforeDelete - 1);
    }
}
