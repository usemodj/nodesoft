package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.OptionValue;
import com.usemodj.nodesoft.repository.OptionValueRepository;
import com.usemodj.nodesoft.repository.search.OptionValueSearchRepository;

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
 * Test class for the OptionValueResource REST controller.
 *
 * @see OptionValueResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class OptionValueResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_PRESENTATION = "SAMPLE_TEXT";
    private static final String UPDATED_PRESENTATION = "UPDATED_TEXT";

    private static final Integer DEFAULT_POSITION = 0;
    private static final Integer UPDATED_POSITION = 1;

    @Inject
    private OptionValueRepository optionValueRepository;

    @Inject
    private OptionValueSearchRepository optionValueSearchRepository;

    private MockMvc restOptionValueMockMvc;

    private OptionValue optionValue;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OptionValueResource optionValueResource = new OptionValueResource();
        ReflectionTestUtils.setField(optionValueResource, "optionValueRepository", optionValueRepository);
        ReflectionTestUtils.setField(optionValueResource, "optionValueSearchRepository", optionValueSearchRepository);
        this.restOptionValueMockMvc = MockMvcBuilders.standaloneSetup(optionValueResource).build();
    }

    @Before
    public void initTest() {
        optionValue = new OptionValue();
        optionValue.setName(DEFAULT_NAME);
        optionValue.setPresentation(DEFAULT_PRESENTATION);
        optionValue.setPosition(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void createOptionValue() throws Exception {
        int databaseSizeBeforeCreate = optionValueRepository.findAll().size();

        // Create the OptionValue
        restOptionValueMockMvc.perform(post("/api/optionValues")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(optionValue)))
                .andExpect(status().isCreated());

        // Validate the OptionValue in the database
        List<OptionValue> optionValues = optionValueRepository.findAll();
        assertThat(optionValues).hasSize(databaseSizeBeforeCreate + 1);
        OptionValue testOptionValue = optionValues.get(optionValues.size() - 1);
        assertThat(testOptionValue.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOptionValue.getPresentation()).isEqualTo(DEFAULT_PRESENTATION);
        assertThat(testOptionValue.getPosition()).isEqualTo(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void getAllOptionValues() throws Exception {
        // Initialize the database
        optionValueRepository.saveAndFlush(optionValue);

        // Get all the optionValues
        restOptionValueMockMvc.perform(get("/api/optionValues"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(optionValue.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].presentation").value(hasItem(DEFAULT_PRESENTATION.toString())))
                .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)));
    }

    @Test
    @Transactional
    public void getOptionValue() throws Exception {
        // Initialize the database
        optionValueRepository.saveAndFlush(optionValue);

        // Get the optionValue
        restOptionValueMockMvc.perform(get("/api/optionValues/{id}", optionValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(optionValue.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.presentation").value(DEFAULT_PRESENTATION.toString()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION));
    }

    @Test
    @Transactional
    public void getNonExistingOptionValue() throws Exception {
        // Get the optionValue
        restOptionValueMockMvc.perform(get("/api/optionValues/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOptionValue() throws Exception {
        // Initialize the database
        optionValueRepository.saveAndFlush(optionValue);

		int databaseSizeBeforeUpdate = optionValueRepository.findAll().size();

        // Update the optionValue
        optionValue.setName(UPDATED_NAME);
        optionValue.setPresentation(UPDATED_PRESENTATION);
        optionValue.setPosition(UPDATED_POSITION);
        restOptionValueMockMvc.perform(put("/api/optionValues")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(optionValue)))
                .andExpect(status().isOk());

        // Validate the OptionValue in the database
        List<OptionValue> optionValues = optionValueRepository.findAll();
        assertThat(optionValues).hasSize(databaseSizeBeforeUpdate);
        OptionValue testOptionValue = optionValues.get(optionValues.size() - 1);
        assertThat(testOptionValue.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOptionValue.getPresentation()).isEqualTo(UPDATED_PRESENTATION);
        assertThat(testOptionValue.getPosition()).isEqualTo(UPDATED_POSITION);
    }

    @Test
    @Transactional
    public void deleteOptionValue() throws Exception {
        // Initialize the database
        optionValueRepository.saveAndFlush(optionValue);

		int databaseSizeBeforeDelete = optionValueRepository.findAll().size();

        // Get the optionValue
        restOptionValueMockMvc.perform(delete("/api/optionValues/{id}", optionValue.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<OptionValue> optionValues = optionValueRepository.findAll();
        assertThat(optionValues).hasSize(databaseSizeBeforeDelete - 1);
    }
}
