package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Product;
import com.usemodj.nodesoft.repository.ProductRepository;
import com.usemodj.nodesoft.repository.search.ProductSearchRepository;

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
import org.joda.time.LocalDate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProductResource REST controller.
 *
 * @see ProductResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ProductResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_PROPERTIES = "SAMPLE_TEXT";
    private static final String UPDATED_PROPERTIES = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_AVAILABLE_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_AVAILABLE_DATE = new LocalDate();

    private static final DateTime DEFAULT_DELETED_DATE = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_DELETED_DATE = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_DELETED_DATE_STR = dateTimeFormatter.print(DEFAULT_DELETED_DATE);
    private static final String DEFAULT_SLUG = "SAMPLE_TEXT";
    private static final String UPDATED_SLUG = "UPDATED_TEXT";
    private static final String DEFAULT_META_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_META_DESCRIPTION = "UPDATED_TEXT";
    private static final String DEFAULT_META_KEYWORDS = "SAMPLE_TEXT";
    private static final String UPDATED_META_KEYWORDS = "UPDATED_TEXT";

    @Inject
    private ProductRepository productRepository;

    @Inject
    private ProductSearchRepository productSearchRepository;

    private MockMvc restProductMockMvc;

    private Product product;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProductResource productResource = new ProductResource();
        ReflectionTestUtils.setField(productResource, "productRepository", productRepository);
        ReflectionTestUtils.setField(productResource, "productSearchRepository", productSearchRepository);
        this.restProductMockMvc = MockMvcBuilders.standaloneSetup(productResource).build();
    }

    @Before
    public void initTest() {
        product = new Product();
        product.setName(DEFAULT_NAME);
        product.setProperties(DEFAULT_PROPERTIES);
        product.setDescription(DEFAULT_DESCRIPTION);
        product.setAvailableDate(DEFAULT_AVAILABLE_DATE);
        product.setDeletedDate(DEFAULT_DELETED_DATE);
        product.setSlug(DEFAULT_SLUG);
        product.setMetaDescription(DEFAULT_META_DESCRIPTION);
        product.setMetaKeywords(DEFAULT_META_KEYWORDS);
    }

    @Test
    @Transactional
    public void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product
        restProductMockMvc.perform(post("/api/products")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product)))
                .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = products.get(products.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getProperties()).isEqualTo(DEFAULT_PROPERTIES);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getAvailableDate()).isEqualTo(DEFAULT_AVAILABLE_DATE);
        assertThat(testProduct.getDeletedDate().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_DELETED_DATE);
        assertThat(testProduct.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testProduct.getMetaDescription()).isEqualTo(DEFAULT_META_DESCRIPTION);
        assertThat(testProduct.getMetaKeywords()).isEqualTo(DEFAULT_META_KEYWORDS);
    }

    @Test
    @Transactional
    public void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the products
        restProductMockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].properties").value(hasItem(DEFAULT_PROPERTIES.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].availableDate").value(hasItem(DEFAULT_AVAILABLE_DATE.toString())))
                .andExpect(jsonPath("$.[*].deletedDate").value(hasItem(DEFAULT_DELETED_DATE_STR)))
                .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG.toString())))
                .andExpect(jsonPath("$.[*].metaDescription").value(hasItem(DEFAULT_META_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].metaKeywords").value(hasItem(DEFAULT_META_KEYWORDS.toString())));
    }

    @Test
    @Transactional
    public void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.properties").value(DEFAULT_PROPERTIES.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.availableDate").value(DEFAULT_AVAILABLE_DATE.toString()))
            .andExpect(jsonPath("$.deletedDate").value(DEFAULT_DELETED_DATE_STR))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG.toString()))
            .andExpect(jsonPath("$.metaDescription").value(DEFAULT_META_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.metaKeywords").value(DEFAULT_META_KEYWORDS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

		int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        product.setName(UPDATED_NAME);
        product.setProperties(UPDATED_PROPERTIES);
        product.setDescription(UPDATED_DESCRIPTION);
        product.setAvailableDate(UPDATED_AVAILABLE_DATE);
        product.setDeletedDate(UPDATED_DELETED_DATE);
        product.setSlug(UPDATED_SLUG);
        product.setMetaDescription(UPDATED_META_DESCRIPTION);
        product.setMetaKeywords(UPDATED_META_KEYWORDS);
        restProductMockMvc.perform(put("/api/products")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product)))
                .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = products.get(products.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getProperties()).isEqualTo(UPDATED_PROPERTIES);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getAvailableDate()).isEqualTo(UPDATED_AVAILABLE_DATE);
        assertThat(testProduct.getDeletedDate().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_DELETED_DATE);
        assertThat(testProduct.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testProduct.getMetaDescription()).isEqualTo(UPDATED_META_DESCRIPTION);
        assertThat(testProduct.getMetaKeywords()).isEqualTo(UPDATED_META_KEYWORDS);
    }

    @Test
    @Transactional
    public void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

		int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Get the product
        restProductMockMvc.perform(delete("/api/products/{id}", product.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(databaseSizeBeforeDelete - 1);
    }
}
