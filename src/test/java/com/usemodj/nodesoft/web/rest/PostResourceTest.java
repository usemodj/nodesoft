package com.usemodj.nodesoft.web.rest;

import com.usemodj.nodesoft.Application;
import com.usemodj.nodesoft.domain.Post;
import com.usemodj.nodesoft.repository.PostRepository;
import com.usemodj.nodesoft.repository.search.PostSearchRepository;

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
 * Test class for the PostResource REST controller.
 *
 * @see PostResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PostResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_CONTENT = "SAMPLE_TEXT";
    private static final String UPDATED_CONTENT = "UPDATED_TEXT";

    private static final Boolean DEFAULT_ROOT = false;
    private static final Boolean UPDATED_ROOT = true;

    @Inject
    private PostRepository postRepository;

    @Inject
    private PostSearchRepository postSearchRepository;

    private MockMvc restPostMockMvc;

    private Post post;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PostResource postResource = new PostResource();
        ReflectionTestUtils.setField(postResource, "postRepository", postRepository);
        ReflectionTestUtils.setField(postResource, "postSearchRepository", postSearchRepository);
        this.restPostMockMvc = MockMvcBuilders.standaloneSetup(postResource).build();
    }

    @Before
    public void initTest() {
        post = new Post();
        post.setName(DEFAULT_NAME);
        post.setContent(DEFAULT_CONTENT);
        post.setRoot(DEFAULT_ROOT);
    }

    @Test
    @Transactional
    public void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post
        restPostMockMvc.perform(post("/api/posts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(post)))
                .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = posts.get(posts.size() - 1);
        assertThat(testPost.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPost.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testPost.getRoot()).isEqualTo(DEFAULT_ROOT);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(postRepository.findAll()).hasSize(0);
        // set the field null
        post.setName(null);

        // Create the Post, which fails.
        restPostMockMvc.perform(post("/api/posts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(post)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllPosts() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the posts
        restPostMockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
                .andExpect(jsonPath("$.[*].root").value(hasItem(DEFAULT_ROOT.booleanValue())));
    }

    @Test
    @Transactional
    public void getPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(post.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.root").value(DEFAULT_ROOT.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

		int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post
        post.setName(UPDATED_NAME);
        post.setContent(UPDATED_CONTENT);
        post.setRoot(UPDATED_ROOT);
        restPostMockMvc.perform(put("/api/posts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(post)))
                .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(databaseSizeBeforeUpdate);
        Post testPost = posts.get(posts.size() - 1);
        assertThat(testPost.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPost.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testPost.getRoot()).isEqualTo(UPDATED_ROOT);
    }

    @Test
    @Transactional
    public void deletePost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

		int databaseSizeBeforeDelete = postRepository.findAll().size();

        // Get the post
        restPostMockMvc.perform(delete("/api/posts/{id}", post.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(databaseSizeBeforeDelete - 1);
    }
}
