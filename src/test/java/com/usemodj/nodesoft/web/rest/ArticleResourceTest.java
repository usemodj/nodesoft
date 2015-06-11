package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Article;
import com.usemodj.nodesoft.repository.ArticleRepository;
import com.usemodj.nodesoft.repository.search.ArticleSearchRepository;

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
 * Test class for the ArticleResource REST controller.
 *
 * @see ArticleResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ArticleResourceTest {

    private static final String DEFAULT_SUBJECT = "SAMPLE_TEXT";
    private static final String UPDATED_SUBJECT = "UPDATED_TEXT";
    private static final String DEFAULT_SUMMARY = "SAMPLE_TEXT";
    private static final String UPDATED_SUMMARY = "UPDATED_TEXT";
    private static final String DEFAULT_CONTENT = "SAMPLE_TEXT";
    private static final String UPDATED_CONTENT = "UPDATED_TEXT";
    private static final String DEFAULT_IMG_URL = "SAMPLE_TEXT";
    private static final String UPDATED_IMG_URL = "UPDATED_TEXT";

    @Inject
    private ArticleRepository articleRepository;

    @Inject
    private ArticleSearchRepository articleSearchRepository;

    private MockMvc restArticleMockMvc;

    private Article article;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ArticleResource articleResource = new ArticleResource();
        ReflectionTestUtils.setField(articleResource, "articleRepository", articleRepository);
        ReflectionTestUtils.setField(articleResource, "articleSearchRepository", articleSearchRepository);
        this.restArticleMockMvc = MockMvcBuilders.standaloneSetup(articleResource).build();
    }

    @Before
    public void initTest() {
        article = new Article();
        article.setSubject(DEFAULT_SUBJECT);
        article.setSummary(DEFAULT_SUMMARY);
        article.setContent(DEFAULT_CONTENT);
        article.setImgUrl(DEFAULT_IMG_URL);
    }

    @Test
    @Transactional
    public void createArticle() throws Exception {
        int databaseSizeBeforeCreate = articleRepository.findAll().size();

        // Create the Article
        restArticleMockMvc.perform(post("/api/articles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(article)))
                .andExpect(status().isCreated());

        // Validate the Article in the database
        List<Article> articles = articleRepository.findAll();
        assertThat(articles).hasSize(databaseSizeBeforeCreate + 1);
        Article testArticle = articles.get(articles.size() - 1);
        assertThat(testArticle.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testArticle.getSummary()).isEqualTo(DEFAULT_SUMMARY);
        assertThat(testArticle.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testArticle.getImgUrl()).isEqualTo(DEFAULT_IMG_URL);
    }

    @Test
    @Transactional
    public void checkSubjectIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(articleRepository.findAll()).hasSize(0);
        // set the field null
        article.setSubject(null);

        // Create the Article, which fails.
        restArticleMockMvc.perform(post("/api/articles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(article)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Article> articles = articleRepository.findAll();
        assertThat(articles).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllArticles() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);

        // Get all the articles
        restArticleMockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(article.getId().intValue())))
                .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT.toString())))
                .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY.toString())))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
                .andExpect(jsonPath("$.[*].imgUrl").value(hasItem(DEFAULT_IMG_URL.toString())));
    }

    @Test
    @Transactional
    public void getArticle() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);

        // Get the article
        restArticleMockMvc.perform(get("/api/articles/{id}", article.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(article.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT.toString()))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.imgUrl").value(DEFAULT_IMG_URL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingArticle() throws Exception {
        // Get the article
        restArticleMockMvc.perform(get("/api/articles/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateArticle() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);

		int databaseSizeBeforeUpdate = articleRepository.findAll().size();

        // Update the article
        article.setSubject(UPDATED_SUBJECT);
        article.setSummary(UPDATED_SUMMARY);
        article.setContent(UPDATED_CONTENT);
        article.setImgUrl(UPDATED_IMG_URL);
        restArticleMockMvc.perform(put("/api/articles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(article)))
                .andExpect(status().isOk());

        // Validate the Article in the database
        List<Article> articles = articleRepository.findAll();
        assertThat(articles).hasSize(databaseSizeBeforeUpdate);
        Article testArticle = articles.get(articles.size() - 1);
        assertThat(testArticle.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testArticle.getSummary()).isEqualTo(UPDATED_SUMMARY);
        assertThat(testArticle.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testArticle.getImgUrl()).isEqualTo(UPDATED_IMG_URL);
    }

    @Test
    @Transactional
    public void deleteArticle() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);

		int databaseSizeBeforeDelete = articleRepository.findAll().size();

        // Get the article
        restArticleMockMvc.perform(delete("/api/articles/{id}", article.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Article> articles = articleRepository.findAll();
        assertThat(articles).hasSize(databaseSizeBeforeDelete - 1);
    }
}
