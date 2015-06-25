package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Variant;
import com.usemodj.nodesoft.repository.VariantRepository;
import com.usemodj.nodesoft.repository.search.VariantSearchRepository;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the VariantResource REST controller.
 *
 * @see VariantResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class VariantResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String DEFAULT_SKU = "SAMPLE_TEXT";
    private static final String UPDATED_SKU = "UPDATED_TEXT";

    private static final Boolean DEFAULT_IS_MASTER = false;
    private static final Boolean UPDATED_IS_MASTER = true;

    private static final DateTime DEFAULT_DELETED_DATE = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_DELETED_DATE = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_DELETED_DATE_STR = dateTimeFormatter.print(DEFAULT_DELETED_DATE);

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);

    private static final BigDecimal DEFAULT_COST_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_COST_PRICE = new BigDecimal(1);
    private static final String DEFAULT_COST_CURRENCY = "SAMPLE_TEXT";
    private static final String UPDATED_COST_CURRENCY = "UPDATED_TEXT";

    private static final Integer DEFAULT_POSITION = 0;
    private static final Integer UPDATED_POSITION = 1;
    private static final String DEFAULT_WEIGHT = "SAMPLE_TEXT";
    private static final String UPDATED_WEIGHT = "UPDATED_TEXT";
    private static final String DEFAULT_HEIGHT = "SAMPLE_TEXT";
    private static final String UPDATED_HEIGHT = "UPDATED_TEXT";
    private static final String DEFAULT_WIDTH = "SAMPLE_TEXT";
    private static final String UPDATED_WIDTH = "UPDATED_TEXT";
    private static final String DEFAULT_DEPTH = "SAMPLE_TEXT";
    private static final String UPDATED_DEPTH = "UPDATED_TEXT";

    @Inject
    private VariantRepository variantRepository;

    @Inject
    private VariantSearchRepository variantSearchRepository;

    private MockMvc restVariantMockMvc;

    private Variant variant;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        VariantResource variantResource = new VariantResource();
        ReflectionTestUtils.setField(variantResource, "variantRepository", variantRepository);
        ReflectionTestUtils.setField(variantResource, "variantSearchRepository", variantSearchRepository);
        this.restVariantMockMvc = MockMvcBuilders.standaloneSetup(variantResource).build();
    }

    @Before
    public void initTest() {
        variant = new Variant();
        variant.setSku(DEFAULT_SKU);
        variant.setIsMaster(DEFAULT_IS_MASTER);
        variant.setDeletedDate(DEFAULT_DELETED_DATE);
        variant.setPrice(DEFAULT_PRICE);
        variant.setCostPrice(DEFAULT_COST_PRICE);
        variant.setCostCurrency(DEFAULT_COST_CURRENCY);
        variant.setPosition(DEFAULT_POSITION);
        variant.setWeight(DEFAULT_WEIGHT);
        variant.setHeight(DEFAULT_HEIGHT);
        variant.setWidth(DEFAULT_WIDTH);
        variant.setDepth(DEFAULT_DEPTH);
    }

    @Test
    @Transactional
    public void createVariant() throws Exception {
        int databaseSizeBeforeCreate = variantRepository.findAll().size();

        // Create the Variant
        restVariantMockMvc.perform(post("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(variant)))
                .andExpect(status().isCreated());

        // Validate the Variant in the database
        List<Variant> variants = variantRepository.findAll();
        assertThat(variants).hasSize(databaseSizeBeforeCreate + 1);
        Variant testVariant = variants.get(variants.size() - 1);
        assertThat(testVariant.getSku()).isEqualTo(DEFAULT_SKU);
        assertThat(testVariant.getIsMaster()).isEqualTo(DEFAULT_IS_MASTER);
        assertThat(testVariant.getDeletedDate().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_DELETED_DATE);
        assertThat(testVariant.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testVariant.getCostPrice()).isEqualTo(DEFAULT_COST_PRICE);
        assertThat(testVariant.getCostCurrency()).isEqualTo(DEFAULT_COST_CURRENCY);
        assertThat(testVariant.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testVariant.getWeight()).isEqualTo(DEFAULT_WEIGHT);
        assertThat(testVariant.getHeight()).isEqualTo(DEFAULT_HEIGHT);
        assertThat(testVariant.getWidth()).isEqualTo(DEFAULT_WIDTH);
        assertThat(testVariant.getDepth()).isEqualTo(DEFAULT_DEPTH);
    }

    @Test
    @Transactional
    public void checkSkuIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(variantRepository.findAll()).hasSize(0);
        // set the field null
        variant.setSku(null);

        // Create the Variant, which fails.
        restVariantMockMvc.perform(post("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(variant)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Variant> variants = variantRepository.findAll();
        assertThat(variants).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllVariants() throws Exception {
        // Initialize the database
        variantRepository.saveAndFlush(variant);

        // Get all the variants
        restVariantMockMvc.perform(get("/api/variants"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(variant.getId().intValue())))
                .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU.toString())))
                .andExpect(jsonPath("$.[*].isMaster").value(hasItem(DEFAULT_IS_MASTER.booleanValue())))
                .andExpect(jsonPath("$.[*].deletedDate").value(hasItem(DEFAULT_DELETED_DATE_STR)))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].costPrice").value(hasItem(DEFAULT_COST_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].costCurrency").value(hasItem(DEFAULT_COST_CURRENCY.toString())))
                .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
                .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT.toString())))
                .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT.toString())))
                .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH.toString())))
                .andExpect(jsonPath("$.[*].depth").value(hasItem(DEFAULT_DEPTH.toString())));
    }

    @Test
    @Transactional
    public void getVariant() throws Exception {
        // Initialize the database
        variantRepository.saveAndFlush(variant);

        // Get the variant
        restVariantMockMvc.perform(get("/api/variants/{id}", variant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(variant.getId().intValue()))
            .andExpect(jsonPath("$.sku").value(DEFAULT_SKU.toString()))
            .andExpect(jsonPath("$.isMaster").value(DEFAULT_IS_MASTER.booleanValue()))
            .andExpect(jsonPath("$.deletedDate").value(DEFAULT_DELETED_DATE_STR))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()))
            .andExpect(jsonPath("$.costPrice").value(DEFAULT_COST_PRICE.intValue()))
            .andExpect(jsonPath("$.costCurrency").value(DEFAULT_COST_CURRENCY.toString()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION))
            .andExpect(jsonPath("$.weight").value(DEFAULT_WEIGHT.toString()))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT.toString()))
            .andExpect(jsonPath("$.width").value(DEFAULT_WIDTH.toString()))
            .andExpect(jsonPath("$.depth").value(DEFAULT_DEPTH.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingVariant() throws Exception {
        // Get the variant
        restVariantMockMvc.perform(get("/api/variants/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVariant() throws Exception {
        // Initialize the database
        variantRepository.saveAndFlush(variant);

		int databaseSizeBeforeUpdate = variantRepository.findAll().size();

        // Update the variant
        variant.setSku(UPDATED_SKU);
        variant.setIsMaster(UPDATED_IS_MASTER);
        variant.setDeletedDate(UPDATED_DELETED_DATE);
        variant.setPrice(UPDATED_PRICE);
        variant.setCostPrice(UPDATED_COST_PRICE);
        variant.setCostCurrency(UPDATED_COST_CURRENCY);
        variant.setPosition(UPDATED_POSITION);
        variant.setWeight(UPDATED_WEIGHT);
        variant.setHeight(UPDATED_HEIGHT);
        variant.setWidth(UPDATED_WIDTH);
        variant.setDepth(UPDATED_DEPTH);
        restVariantMockMvc.perform(put("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(variant)))
                .andExpect(status().isOk());

        // Validate the Variant in the database
        List<Variant> variants = variantRepository.findAll();
        assertThat(variants).hasSize(databaseSizeBeforeUpdate);
        Variant testVariant = variants.get(variants.size() - 1);
        assertThat(testVariant.getSku()).isEqualTo(UPDATED_SKU);
        assertThat(testVariant.getIsMaster()).isEqualTo(UPDATED_IS_MASTER);
        assertThat(testVariant.getDeletedDate().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_DELETED_DATE);
        assertThat(testVariant.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testVariant.getCostPrice()).isEqualTo(UPDATED_COST_PRICE);
        assertThat(testVariant.getCostCurrency()).isEqualTo(UPDATED_COST_CURRENCY);
        assertThat(testVariant.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testVariant.getWeight()).isEqualTo(UPDATED_WEIGHT);
        assertThat(testVariant.getHeight()).isEqualTo(UPDATED_HEIGHT);
        assertThat(testVariant.getWidth()).isEqualTo(UPDATED_WIDTH);
        assertThat(testVariant.getDepth()).isEqualTo(UPDATED_DEPTH);
    }

    @Test
    @Transactional
    public void deleteVariant() throws Exception {
        // Initialize the database
        variantRepository.saveAndFlush(variant);

		int databaseSizeBeforeDelete = variantRepository.findAll().size();

        // Get the variant
        restVariantMockMvc.perform(delete("/api/variants/{id}", variant.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Variant> variants = variantRepository.findAll();
        assertThat(variants).hasSize(databaseSizeBeforeDelete - 1);
    }
}
