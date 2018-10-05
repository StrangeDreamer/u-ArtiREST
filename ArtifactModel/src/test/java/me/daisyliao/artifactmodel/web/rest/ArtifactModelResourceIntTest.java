package me.daisyliao.artifactmodel.web.rest;

import me.daisyliao.artifactmodel.ArtifactModelApp;

import me.daisyliao.artifactmodel.domain.ArtifactModel;
import me.daisyliao.artifactmodel.repository.ArtifactModelRepository;
import me.daisyliao.artifactmodel.service.ArtifactModelService;
import me.daisyliao.artifactmodel.web.rest.errors.ExceptionTranslator;

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

import static me.daisyliao.artifactmodel.web.rest.TestUtil.sameInstant;
import static me.daisyliao.artifactmodel.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ArtifactModelResource REST controller.
 *
 * @see ArtifactModelResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArtifactModelApp.class)
public class ArtifactModelResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private ArtifactModelRepository artifactModelRepository;

    @Autowired
    private ArtifactModelService artifactModelService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restArtifactModelMockMvc;

    private ArtifactModel artifactModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ArtifactModelResource artifactModelResource = new ArtifactModelResource(artifactModelService);
        this.restArtifactModelMockMvc = MockMvcBuilders.standaloneSetup(artifactModelResource)
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
    public static ArtifactModel createEntity() {
        ArtifactModel artifactModel = new ArtifactModel()
            .name(DEFAULT_NAME)
            .comment(DEFAULT_COMMENT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return artifactModel;
    }

    @Before
    public void initTest() {
        artifactModelRepository.deleteAll();
        artifactModel = createEntity();
    }

    @Test
    public void createArtifactModel() throws Exception {
        int databaseSizeBeforeCreate = artifactModelRepository.findAll().size();

        // Create the ArtifactModel
        restArtifactModelMockMvc.perform(post("/api/artifact-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(artifactModel)))
            .andExpect(status().isCreated());

        // Validate the ArtifactModel in the database
        List<ArtifactModel> artifactModelList = artifactModelRepository.findAll();
        assertThat(artifactModelList).hasSize(databaseSizeBeforeCreate + 1);
        ArtifactModel testArtifactModel = artifactModelList.get(artifactModelList.size() - 1);
        assertThat(testArtifactModel.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testArtifactModel.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testArtifactModel.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testArtifactModel.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    public void createArtifactModelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = artifactModelRepository.findAll().size();

        // Create the ArtifactModel with an existing ID
        artifactModel.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restArtifactModelMockMvc.perform(post("/api/artifact-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(artifactModel)))
            .andExpect(status().isBadRequest());

        // Validate the ArtifactModel in the database
        List<ArtifactModel> artifactModelList = artifactModelRepository.findAll();
        assertThat(artifactModelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllArtifactModels() throws Exception {
        // Initialize the database
        artifactModelRepository.save(artifactModel);

        // Get all the artifactModelList
        restArtifactModelMockMvc.perform(get("/api/artifact-models?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(artifactModel.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    public void getArtifactModel() throws Exception {
        // Initialize the database
        artifactModelRepository.save(artifactModel);

        // Get the artifactModel
        restArtifactModelMockMvc.perform(get("/api/artifact-models/{id}", artifactModel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(artifactModel.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    public void getNonExistingArtifactModel() throws Exception {
        // Get the artifactModel
        restArtifactModelMockMvc.perform(get("/api/artifact-models/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateArtifactModel() throws Exception {
        // Initialize the database
        artifactModelService.save(artifactModel);

        int databaseSizeBeforeUpdate = artifactModelRepository.findAll().size();

        // Update the artifactModel
        ArtifactModel updatedArtifactModel = artifactModelRepository.findOne(artifactModel.getId());
        updatedArtifactModel
            .name(UPDATED_NAME)
            .comment(UPDATED_COMMENT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restArtifactModelMockMvc.perform(put("/api/artifact-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedArtifactModel)))
            .andExpect(status().isOk());

        // Validate the ArtifactModel in the database
        List<ArtifactModel> artifactModelList = artifactModelRepository.findAll();
        assertThat(artifactModelList).hasSize(databaseSizeBeforeUpdate);
        ArtifactModel testArtifactModel = artifactModelList.get(artifactModelList.size() - 1);
        assertThat(testArtifactModel.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArtifactModel.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testArtifactModel.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testArtifactModel.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    public void updateNonExistingArtifactModel() throws Exception {
        int databaseSizeBeforeUpdate = artifactModelRepository.findAll().size();

        // Create the ArtifactModel

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restArtifactModelMockMvc.perform(put("/api/artifact-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(artifactModel)))
            .andExpect(status().isCreated());

        // Validate the ArtifactModel in the database
        List<ArtifactModel> artifactModelList = artifactModelRepository.findAll();
        assertThat(artifactModelList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteArtifactModel() throws Exception {
        // Initialize the database
        artifactModelService.save(artifactModel);

        int databaseSizeBeforeDelete = artifactModelRepository.findAll().size();

        // Get the artifactModel
        restArtifactModelMockMvc.perform(delete("/api/artifact-models/{id}", artifactModel.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ArtifactModel> artifactModelList = artifactModelRepository.findAll();
        assertThat(artifactModelList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ArtifactModel.class);
        ArtifactModel artifactModel1 = new ArtifactModel();
        artifactModel1.setId("id1");
        ArtifactModel artifactModel2 = new ArtifactModel();
        artifactModel2.setId(artifactModel1.getId());
        assertThat(artifactModel1).isEqualTo(artifactModel2);
        artifactModel2.setId("id2");
        assertThat(artifactModel1).isNotEqualTo(artifactModel2);
        artifactModel1.setId(null);
        assertThat(artifactModel1).isNotEqualTo(artifactModel2);
    }
}
