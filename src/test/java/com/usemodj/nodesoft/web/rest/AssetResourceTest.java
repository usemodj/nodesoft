package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Asset;
import com.usemodj.nodesoft.repository.AssetRepository;
import com.usemodj.nodesoft.repository.search.AssetSearchRepository;

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
 * Test class for the AssetResource REST controller.
 *
 * @see AssetResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AssetResourceTest {


    private static final Long DEFAULT_VIEWABLE_ID = 0L;
    private static final Long UPDATED_VIEWABLE_ID = 1L;
    private static final String DEFAULT_VIEWABLE_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_VIEWABLE_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_CONTENT_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_CONTENT_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_FILE_SIZE = "SAMPLE_TEXT";
    private static final String UPDATED_FILE_SIZE = "UPDATED_TEXT";
    private static final String DEFAULT_FILE_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_FILE_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_FILE_PATH = "SAMPLE_TEXT";
    private static final String UPDATED_FILE_PATH = "UPDATED_TEXT";
    private static final String DEFAULT_ALT = "SAMPLE_TEXT";
    private static final String UPDATED_ALT = "UPDATED_TEXT";

    private static final Integer DEFAULT_POSITION = 0;
    private static final Integer UPDATED_POSITION = 1;

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private AssetSearchRepository assetSearchRepository;

    private MockMvc restAssetMockMvc;

    private Asset asset;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AssetResource assetResource = new AssetResource();
        ReflectionTestUtils.setField(assetResource, "assetRepository", assetRepository);
        ReflectionTestUtils.setField(assetResource, "assetSearchRepository", assetSearchRepository);
        this.restAssetMockMvc = MockMvcBuilders.standaloneSetup(assetResource).build();
    }

    @Before
    public void initTest() {
        asset = new Asset();
        asset.setViewableId(DEFAULT_VIEWABLE_ID);
        asset.setViewableType(DEFAULT_VIEWABLE_TYPE);
        asset.setContentType(DEFAULT_CONTENT_TYPE);
        asset.setFileSize(DEFAULT_FILE_SIZE);
        asset.setFileName(DEFAULT_FILE_NAME);
        asset.setFilePath(DEFAULT_FILE_PATH);
        asset.setAlt(DEFAULT_ALT);
        asset.setPosition(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void createAsset() throws Exception {
        int databaseSizeBeforeCreate = assetRepository.findAll().size();

        // Create the Asset
        restAssetMockMvc.perform(post("/api/assets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(asset)))
                .andExpect(status().isCreated());

        // Validate the Asset in the database
        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeCreate + 1);
        Asset testAsset = assets.get(assets.size() - 1);
        assertThat(testAsset.getViewableId()).isEqualTo(DEFAULT_VIEWABLE_ID);
        assertThat(testAsset.getViewableType()).isEqualTo(DEFAULT_VIEWABLE_TYPE);
        assertThat(testAsset.getContentType()).isEqualTo(DEFAULT_CONTENT_TYPE);
        assertThat(testAsset.getFileSize()).isEqualTo(DEFAULT_FILE_SIZE);
        assertThat(testAsset.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
        assertThat(testAsset.getFilePath()).isEqualTo(DEFAULT_FILE_PATH);
        assertThat(testAsset.getAlt()).isEqualTo(DEFAULT_ALT);
        assertThat(testAsset.getPosition()).isEqualTo(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void getAllAssets() throws Exception {
        // Initialize the database
        assetRepository.saveAndFlush(asset);

        // Get all the assets
        restAssetMockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(asset.getId().intValue())))
                .andExpect(jsonPath("$.[*].viewableId").value(hasItem(DEFAULT_VIEWABLE_ID.intValue())))
                .andExpect(jsonPath("$.[*].viewableType").value(hasItem(DEFAULT_VIEWABLE_TYPE.toString())))
                .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].fileSize").value(hasItem(DEFAULT_FILE_SIZE.toString())))
                .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME.toString())))
                .andExpect(jsonPath("$.[*].filePath").value(hasItem(DEFAULT_FILE_PATH.toString())))
                .andExpect(jsonPath("$.[*].alt").value(hasItem(DEFAULT_ALT.toString())))
                .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)));
    }

    @Test
    @Transactional
    public void getAsset() throws Exception {
        // Initialize the database
        assetRepository.saveAndFlush(asset);

        // Get the asset
        restAssetMockMvc.perform(get("/api/assets/{id}", asset.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(asset.getId().intValue()))
            .andExpect(jsonPath("$.viewableId").value(DEFAULT_VIEWABLE_ID.intValue()))
            .andExpect(jsonPath("$.viewableType").value(DEFAULT_VIEWABLE_TYPE.toString()))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE.toString()))
            .andExpect(jsonPath("$.fileSize").value(DEFAULT_FILE_SIZE.toString()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME.toString()))
            .andExpect(jsonPath("$.filePath").value(DEFAULT_FILE_PATH.toString()))
            .andExpect(jsonPath("$.alt").value(DEFAULT_ALT.toString()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION));
    }

    @Test
    @Transactional
    public void getNonExistingAsset() throws Exception {
        // Get the asset
        restAssetMockMvc.perform(get("/api/assets/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAsset() throws Exception {
        // Initialize the database
        assetRepository.saveAndFlush(asset);

		int databaseSizeBeforeUpdate = assetRepository.findAll().size();

        // Update the asset
        asset.setViewableId(UPDATED_VIEWABLE_ID);
        asset.setViewableType(UPDATED_VIEWABLE_TYPE);
        asset.setContentType(UPDATED_CONTENT_TYPE);
        asset.setFileSize(UPDATED_FILE_SIZE);
        asset.setFileName(UPDATED_FILE_NAME);
        asset.setFilePath(UPDATED_FILE_PATH);
        asset.setAlt(UPDATED_ALT);
        asset.setPosition(UPDATED_POSITION);
        restAssetMockMvc.perform(put("/api/assets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(asset)))
                .andExpect(status().isOk());

        // Validate the Asset in the database
        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeUpdate);
        Asset testAsset = assets.get(assets.size() - 1);
        assertThat(testAsset.getViewableId()).isEqualTo(UPDATED_VIEWABLE_ID);
        assertThat(testAsset.getViewableType()).isEqualTo(UPDATED_VIEWABLE_TYPE);
        assertThat(testAsset.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
        assertThat(testAsset.getFileSize()).isEqualTo(UPDATED_FILE_SIZE);
        assertThat(testAsset.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testAsset.getFilePath()).isEqualTo(UPDATED_FILE_PATH);
        assertThat(testAsset.getAlt()).isEqualTo(UPDATED_ALT);
        assertThat(testAsset.getPosition()).isEqualTo(UPDATED_POSITION);
    }

    @Test
    @Transactional
    public void deleteAsset() throws Exception {
        // Initialize the database
        assetRepository.saveAndFlush(asset);

		int databaseSizeBeforeDelete = assetRepository.findAll().size();

        // Get the asset
        restAssetMockMvc.perform(delete("/api/assets/{id}", asset.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Asset> assets = assetRepository.findAll();
        assertThat(assets).hasSize(databaseSizeBeforeDelete - 1);
    }
}
