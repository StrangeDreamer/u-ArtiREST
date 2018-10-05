package me.daisyliao.processes.web.rest;

import me.daisyliao.processes.ProcessesApp;

import me.daisyliao.processes.domain.Artifact;
import me.daisyliao.processes.repository.ArtifactRepository;
import me.daisyliao.processes.service.ArtifactService;
import me.daisyliao.processes.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static me.daisyliao.processes.web.rest.TestUtil.sameInstant;
import static me.daisyliao.processes.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ArtifactResource REST controller.
 *
 * @see ArtifactResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessesApp.class)
public class ArtifactResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENT_STATE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ArtifactService artifactService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restArtifactMockMvc;

    private Artifact artifact;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ArtifactResource artifactResource = new ArtifactResource(artifactService);
        this.restArtifactMockMvc = MockMvcBuilders.standaloneSetup(artifactResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artifact createEntity() {
        Artifact artifact = new Artifact()
            .name(DEFAULT_NAME)
            .currentState(DEFAULT_CURRENT_STATE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return artifact;
    }

    @Before
    public void initTest() {
        artifactRepository.deleteAll();
        artifact = createEntity();
    }

    @Test
    public void createArtifact() throws Exception {
        int databaseSizeBeforeCreate = artifactRepository.findAll().size();

        // Create the Artifact
        restArtifactMockMvc.perform(post("/api/artifacts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(artifact)))
            .andExpect(status().isCreated());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeCreate + 1);
        Artifact testArtifact = artifactList.get(artifactList.size() - 1);
        assertThat(testArtifact.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testArtifact.getCurrentState()).isEqualTo(DEFAULT_CURRENT_STATE);
        assertThat(testArtifact.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testArtifact.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    public void createArtifactWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = artifactRepository.findAll().size();

        // Create the Artifact with an existing ID
        artifact.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restArtifactMockMvc.perform(post("/api/artifacts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(artifact)))
            .andExpect(status().isBadRequest());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllArtifacts() throws Exception {
        // Initialize the database
        artifactRepository.save(artifact);

        // Get all the artifactList
        restArtifactMockMvc.perform(get("/api/artifacts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(artifact.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].currentState").value(hasItem(DEFAULT_CURRENT_STATE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    public void getArtifact() throws Exception {
        // Initialize the database
        artifactRepository.save(artifact);

        // Get the artifact
        restArtifactMockMvc.perform(get("/api/artifacts/{id}", artifact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(artifact.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.currentState").value(DEFAULT_CURRENT_STATE.toString()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    public void getNonExistingArtifact() throws Exception {
        // Get the artifact
        restArtifactMockMvc.perform(get("/api/artifacts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateArtifact() throws Exception {
        // Initialize the database
        artifactService.save(artifact);

        int databaseSizeBeforeUpdate = artifactRepository.findAll().size();

        // Update the artifact
        Artifact updatedArtifact = artifactRepository.findOne(artifact.getId());
        updatedArtifact
            .name(UPDATED_NAME)
            .currentState(UPDATED_CURRENT_STATE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restArtifactMockMvc.perform(put("/api/artifacts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedArtifact)))
            .andExpect(status().isOk());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeUpdate);
        Artifact testArtifact = artifactList.get(artifactList.size() - 1);
        assertThat(testArtifact.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArtifact.getCurrentState()).isEqualTo(UPDATED_CURRENT_STATE);
        assertThat(testArtifact.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testArtifact.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    public void updateNonExistingArtifact() throws Exception {
        int databaseSizeBeforeUpdate = artifactRepository.findAll().size();

        // Create the Artifact

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restArtifactMockMvc.perform(put("/api/artifacts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(artifact)))
            .andExpect(status().isCreated());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteArtifact() throws Exception {
        // Initialize the database
        artifactService.save(artifact);

        int databaseSizeBeforeDelete = artifactRepository.findAll().size();

        // Get the artifact
        restArtifactMockMvc.perform(delete("/api/artifacts/{id}", artifact.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Artifact.class);
        Artifact artifact1 = new Artifact();
        artifact1.setId("id1");
        Artifact artifact2 = new Artifact();
        artifact2.setId(artifact1.getId());
        assertThat(artifact1).isEqualTo(artifact2);
        artifact2.setId("id2");
        assertThat(artifact1).isNotEqualTo(artifact2);
        artifact1.setId(null);
        assertThat(artifact1).isNotEqualTo(artifact2);
    }
}
