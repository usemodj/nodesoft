package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.OptionType;
import com.usemodj.nodesoft.repository.OptionTypeRepository;
import com.usemodj.nodesoft.repository.search.OptionTypeSearchRepository;

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
 * Test class for the OptionTypeResource REST controller.
 *
 * @see OptionTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class OptionTypeResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_PRESENTATION = "SAMPLE_TEXT";
    private static final String UPDATED_PRESENTATION = "UPDATED_TEXT";

    private static final Integer DEFAULT_POSITION = 0;
    private static final Integer UPDATED_POSITION = 1;

    @Inject
    private OptionTypeRepository optionTypeRepository;

    @Inject
    private OptionTypeSearchRepository optionTypeSearchRepository;

    private MockMvc restOptionTypeMockMvc;

    private OptionType optionType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OptionTypeResource optionTypeResource = new OptionTypeResource();
        ReflectionTestUtils.setField(optionTypeResource, "optionTypeRepository", optionTypeRepository);
        ReflectionTestUtils.setField(optionTypeResource, "optionTypeSearchRepository", optionTypeSearchRepository);
        this.restOptionTypeMockMvc = MockMvcBuilders.standaloneSetup(optionTypeResource).build();
    }

    @Before
    public void initTest() {
        optionType = new OptionType();
        optionType.setName(DEFAULT_NAME);
        optionType.setPresentation(DEFAULT_PRESENTATION);
        optionType.setPosition(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void createOptionType() throws Exception {
        int databaseSizeBeforeCreate = optionTypeRepository.findAll().size();

        // Create the OptionType
        restOptionTypeMockMvc.perform(post("/api/optionTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(optionType)))
                .andExpect(status().isCreated());

        // Validate the OptionType in the database
        List<OptionType> optionTypes = optionTypeRepository.findAll();
        assertThat(optionTypes).hasSize(databaseSizeBeforeCreate + 1);
        OptionType testOptionType = optionTypes.get(optionTypes.size() - 1);
        assertThat(testOptionType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOptionType.getPresentation()).isEqualTo(DEFAULT_PRESENTATION);
        assertThat(testOptionType.getPosition()).isEqualTo(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void getAllOptionTypes() throws Exception {
        // Initialize the database
        optionTypeRepository.saveAndFlush(optionType);

        // Get all the optionTypes
        restOptionTypeMockMvc.perform(get("/api/optionTypes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(optionType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].presentation").value(hasItem(DEFAULT_PRESENTATION.toString())))
                .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)));
    }

    @Test
    @Transactional
    public void getOptionType() throws Exception {
        // Initialize the database
        optionTypeRepository.saveAndFlush(optionType);

        // Get the optionType
        restOptionTypeMockMvc.perform(get("/api/optionTypes/{id}", optionType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(optionType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.presentation").value(DEFAULT_PRESENTATION.toString()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION));
    }

    @Test
    @Transactional
    public void getNonExistingOptionType() throws Exception {
        // Get the optionType
        restOptionTypeMockMvc.perform(get("/api/optionTypes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOptionType() throws Exception {
        // Initialize the database
        optionTypeRepository.saveAndFlush(optionType);

		int databaseSizeBeforeUpdate = optionTypeRepository.findAll().size();

        // Update the optionType
        optionType.setName(UPDATED_NAME);
        optionType.setPresentation(UPDATED_PRESENTATION);
        optionType.setPosition(UPDATED_POSITION);
        restOptionTypeMockMvc.perform(put("/api/optionTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(optionType)))
                .andExpect(status().isOk());

        // Validate the OptionType in the database
        List<OptionType> optionTypes = optionTypeRepository.findAll();
        assertThat(optionTypes).hasSize(databaseSizeBeforeUpdate);
        OptionType testOptionType = optionTypes.get(optionTypes.size() - 1);
        assertThat(testOptionType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOptionType.getPresentation()).isEqualTo(UPDATED_PRESENTATION);
        assertThat(testOptionType.getPosition()).isEqualTo(UPDATED_POSITION);
    }

    @Test
    @Transactional
    public void deleteOptionType() throws Exception {
        // Initialize the database
        optionTypeRepository.saveAndFlush(optionType);

		int databaseSizeBeforeDelete = optionTypeRepository.findAll().size();

        // Get the optionType
        restOptionTypeMockMvc.perform(delete("/api/optionTypes/{id}", optionType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<OptionType> optionTypes = optionTypeRepository.findAll();
        assertThat(optionTypes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
