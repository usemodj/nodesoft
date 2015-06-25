package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Taxon;
import com.usemodj.nodesoft.repository.TaxonRepository;
import com.usemodj.nodesoft.repository.search.TaxonSearchRepository;

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
 * Test class for the TaxonResource REST controller.
 *
 * @see TaxonResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TaxonResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Integer DEFAULT_POSITION = 0;
    private static final Integer UPDATED_POSITION = 1;
    private static final String DEFAULT_PERMALINK = "SAMPLE_TEXT";
    private static final String UPDATED_PERMALINK = "UPDATED_TEXT";

    private static final Integer DEFAULT_LFT = 0;
    private static final Integer UPDATED_LFT = 1;

    private static final Integer DEFAULT_RGT = 0;
    private static final Integer UPDATED_RGT = 1;
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";
    private static final String DEFAULT_META_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_META_TITLE = "UPDATED_TEXT";
    private static final String DEFAULT_META_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_META_DESCRIPTION = "UPDATED_TEXT";
    private static final String DEFAULT_META_KEYWORDS = "SAMPLE_TEXT";
    private static final String UPDATED_META_KEYWORDS = "UPDATED_TEXT";

    private static final Integer DEFAULT_DEPTH = 0;
    private static final Integer UPDATED_DEPTH = 1;
    private static final String DEFAULT_ICON_FILE_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_ICON_FILE_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_ICON_CONTENT_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_ICON_CONTENT_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_ICON_FILE_SIZE = "SAMPLE_TEXT";
    private static final String UPDATED_ICON_FILE_SIZE = "UPDATED_TEXT";

    @Inject
    private TaxonRepository taxonRepository;

    @Inject
    private TaxonSearchRepository taxonSearchRepository;

    private MockMvc restTaxonMockMvc;

    private Taxon taxon;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TaxonResource taxonResource = new TaxonResource();
        ReflectionTestUtils.setField(taxonResource, "taxonRepository", taxonRepository);
        ReflectionTestUtils.setField(taxonResource, "taxonSearchRepository", taxonSearchRepository);
        this.restTaxonMockMvc = MockMvcBuilders.standaloneSetup(taxonResource).build();
    }

    @Before
    public void initTest() {
        taxon = new Taxon();
        taxon.setName(DEFAULT_NAME);
        taxon.setPosition(DEFAULT_POSITION);
        taxon.setPermalink(DEFAULT_PERMALINK);
        taxon.setLft(DEFAULT_LFT);
        taxon.setRgt(DEFAULT_RGT);
        taxon.setDescription(DEFAULT_DESCRIPTION);
        taxon.setMetaTitle(DEFAULT_META_TITLE);
        taxon.setMetaDescription(DEFAULT_META_DESCRIPTION);
        taxon.setMetaKeywords(DEFAULT_META_KEYWORDS);
        taxon.setDepth(DEFAULT_DEPTH);
        taxon.setIconFileName(DEFAULT_ICON_FILE_NAME);
        taxon.setIconContentType(DEFAULT_ICON_CONTENT_TYPE);
        taxon.setIconFileSize(DEFAULT_ICON_FILE_SIZE);
    }

    @Test
    @Transactional
    public void createTaxon() throws Exception {
        int databaseSizeBeforeCreate = taxonRepository.findAll().size();

        // Create the Taxon
        restTaxonMockMvc.perform(post("/api/taxons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(taxon)))
                .andExpect(status().isCreated());

        // Validate the Taxon in the database
        List<Taxon> taxons = taxonRepository.findAll();
        assertThat(taxons).hasSize(databaseSizeBeforeCreate + 1);
        Taxon testTaxon = taxons.get(taxons.size() - 1);
        assertThat(testTaxon.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTaxon.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testTaxon.getPermalink()).isEqualTo(DEFAULT_PERMALINK);
        assertThat(testTaxon.getLft()).isEqualTo(DEFAULT_LFT);
        assertThat(testTaxon.getRgt()).isEqualTo(DEFAULT_RGT);
        assertThat(testTaxon.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTaxon.getMetaTitle()).isEqualTo(DEFAULT_META_TITLE);
        assertThat(testTaxon.getMetaDescription()).isEqualTo(DEFAULT_META_DESCRIPTION);
        assertThat(testTaxon.getMetaKeywords()).isEqualTo(DEFAULT_META_KEYWORDS);
        assertThat(testTaxon.getDepth()).isEqualTo(DEFAULT_DEPTH);
        assertThat(testTaxon.getIconFileName()).isEqualTo(DEFAULT_ICON_FILE_NAME);
        assertThat(testTaxon.getIconContentType()).isEqualTo(DEFAULT_ICON_CONTENT_TYPE);
        assertThat(testTaxon.getIconFileSize()).isEqualTo(DEFAULT_ICON_FILE_SIZE);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(taxonRepository.findAll()).hasSize(0);
        // set the field null
        taxon.setName(null);

        // Create the Taxon, which fails.
        restTaxonMockMvc.perform(post("/api/taxons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(taxon)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Taxon> taxons = taxonRepository.findAll();
        assertThat(taxons).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllTaxons() throws Exception {
        // Initialize the database
        taxonRepository.saveAndFlush(taxon);

        // Get all the taxons
        restTaxonMockMvc.perform(get("/api/taxons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(taxon.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
                .andExpect(jsonPath("$.[*].permalink").value(hasItem(DEFAULT_PERMALINK.toString())))
                .andExpect(jsonPath("$.[*].lft").value(hasItem(DEFAULT_LFT)))
                .andExpect(jsonPath("$.[*].rgt").value(hasItem(DEFAULT_RGT)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].metaTitle").value(hasItem(DEFAULT_META_TITLE.toString())))
                .andExpect(jsonPath("$.[*].metaDescription").value(hasItem(DEFAULT_META_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].metaKeywords").value(hasItem(DEFAULT_META_KEYWORDS.toString())))
                .andExpect(jsonPath("$.[*].depth").value(hasItem(DEFAULT_DEPTH)))
                .andExpect(jsonPath("$.[*].iconFileName").value(hasItem(DEFAULT_ICON_FILE_NAME.toString())))
                .andExpect(jsonPath("$.[*].iconContentType").value(hasItem(DEFAULT_ICON_CONTENT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].iconFileSize").value(hasItem(DEFAULT_ICON_FILE_SIZE.toString())));
    }

    @Test
    @Transactional
    public void getTaxon() throws Exception {
        // Initialize the database
        taxonRepository.saveAndFlush(taxon);

        // Get the taxon
        restTaxonMockMvc.perform(get("/api/taxons/{id}", taxon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(taxon.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION))
            .andExpect(jsonPath("$.permalink").value(DEFAULT_PERMALINK.toString()))
            .andExpect(jsonPath("$.lft").value(DEFAULT_LFT))
            .andExpect(jsonPath("$.rgt").value(DEFAULT_RGT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.metaTitle").value(DEFAULT_META_TITLE.toString()))
            .andExpect(jsonPath("$.metaDescription").value(DEFAULT_META_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.metaKeywords").value(DEFAULT_META_KEYWORDS.toString()))
            .andExpect(jsonPath("$.depth").value(DEFAULT_DEPTH))
            .andExpect(jsonPath("$.iconFileName").value(DEFAULT_ICON_FILE_NAME.toString()))
            .andExpect(jsonPath("$.iconContentType").value(DEFAULT_ICON_CONTENT_TYPE.toString()))
            .andExpect(jsonPath("$.iconFileSize").value(DEFAULT_ICON_FILE_SIZE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTaxon() throws Exception {
        // Get the taxon
        restTaxonMockMvc.perform(get("/api/taxons/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTaxon() throws Exception {
        // Initialize the database
        taxonRepository.saveAndFlush(taxon);

		int databaseSizeBeforeUpdate = taxonRepository.findAll().size();

        // Update the taxon
        taxon.setName(UPDATED_NAME);
        taxon.setPosition(UPDATED_POSITION);
        taxon.setPermalink(UPDATED_PERMALINK);
        taxon.setLft(UPDATED_LFT);
        taxon.setRgt(UPDATED_RGT);
        taxon.setDescription(UPDATED_DESCRIPTION);
        taxon.setMetaTitle(UPDATED_META_TITLE);
        taxon.setMetaDescription(UPDATED_META_DESCRIPTION);
        taxon.setMetaKeywords(UPDATED_META_KEYWORDS);
        taxon.setDepth(UPDATED_DEPTH);
        taxon.setIconFileName(UPDATED_ICON_FILE_NAME);
        taxon.setIconContentType(UPDATED_ICON_CONTENT_TYPE);
        taxon.setIconFileSize(UPDATED_ICON_FILE_SIZE);
        restTaxonMockMvc.perform(put("/api/taxons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(taxon)))
                .andExpect(status().isOk());

        // Validate the Taxon in the database
        List<Taxon> taxons = taxonRepository.findAll();
        assertThat(taxons).hasSize(databaseSizeBeforeUpdate);
        Taxon testTaxon = taxons.get(taxons.size() - 1);
        assertThat(testTaxon.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaxon.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testTaxon.getPermalink()).isEqualTo(UPDATED_PERMALINK);
        assertThat(testTaxon.getLft()).isEqualTo(UPDATED_LFT);
        assertThat(testTaxon.getRgt()).isEqualTo(UPDATED_RGT);
        assertThat(testTaxon.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTaxon.getMetaTitle()).isEqualTo(UPDATED_META_TITLE);
        assertThat(testTaxon.getMetaDescription()).isEqualTo(UPDATED_META_DESCRIPTION);
        assertThat(testTaxon.getMetaKeywords()).isEqualTo(UPDATED_META_KEYWORDS);
        assertThat(testTaxon.getDepth()).isEqualTo(UPDATED_DEPTH);
        assertThat(testTaxon.getIconFileName()).isEqualTo(UPDATED_ICON_FILE_NAME);
        assertThat(testTaxon.getIconContentType()).isEqualTo(UPDATED_ICON_CONTENT_TYPE);
        assertThat(testTaxon.getIconFileSize()).isEqualTo(UPDATED_ICON_FILE_SIZE);
    }

    @Test
    @Transactional
    public void deleteTaxon() throws Exception {
        // Initialize the database
        taxonRepository.saveAndFlush(taxon);

		int databaseSizeBeforeDelete = taxonRepository.findAll().size();

        // Get the taxon
        restTaxonMockMvc.perform(delete("/api/taxons/{id}", taxon.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Taxon> taxons = taxonRepository.findAll();
        assertThat(taxons).hasSize(databaseSizeBeforeDelete - 1);
    }
}
