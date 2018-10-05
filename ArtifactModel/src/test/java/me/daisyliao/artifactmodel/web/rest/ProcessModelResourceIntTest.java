package me.daisyliao.artifactmodel.web.rest;

import me.daisyliao.artifactmodel.ArtifactModelApp;
import me.daisyliao.artifactmodel.domain.enumeration.Status;

import me.daisyliao.artifactmodel.domain.ProcessModel;
import me.daisyliao.artifactmodel.repository.ProcessModelRepository;
import me.daisyliao.artifactmodel.service.ProcessModelService;
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

import static me.daisyliao.artifactmodel.domain.enumeration.Status.ENACTED;
import static me.daisyliao.artifactmodel.web.rest.TestUtil.sameInstant;
import static me.daisyliao.artifactmodel.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProcessModelResource REST controller.
 *
 * @see ProcessModelResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArtifactModelApp.class)
public class ProcessModelResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = ENACTED;
    private static final Status UPDATED_STATUS = ENACTED;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private ProcessModelRepository processModelRepository;

    @Autowired
    private ProcessModelService processModelService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restProcessModelMockMvc;

    private ProcessModel processModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProcessModelResource processModelResource = new ProcessModelResource(processModelService);
        this.restProcessModelMockMvc = MockMvcBuilders.standaloneSetup(processModelResource)
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
    public static ProcessModel createEntity() {
        ProcessModel processModel = new ProcessModel()
            .name(DEFAULT_NAME)
            .comment(DEFAULT_COMMENT)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return processModel;
    }

    @Before
    public void initTest() {
        processModelRepository.deleteAll();
        processModel = createEntity();
    }

    @Test
    public void createProcessModel() throws Exception {
        int databaseSizeBeforeCreate = processModelRepository.findAll().size();

        // Create the ProcessModel
        restProcessModelMockMvc.perform(post("/api/process-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(processModel)))
            .andExpect(status().isCreated());

        // Validate the ProcessModel in the database
        List<ProcessModel> processModelList = processModelRepository.findAll();
        assertThat(processModelList).hasSize(databaseSizeBeforeCreate + 1);
        ProcessModel testProcessModel = processModelList.get(processModelList.size() - 1);
        assertThat(testProcessModel.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProcessModel.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testProcessModel.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProcessModel.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testProcessModel.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    public void createProcessModelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = processModelRepository.findAll().size();

        // Create the ProcessModel with an existing ID
        processModel.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restProcessModelMockMvc.perform(post("/api/process-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(processModel)))
            .andExpect(status().isBadRequest());

        // Validate the ProcessModel in the database
        List<ProcessModel> processModelList = processModelRepository.findAll();
        assertThat(processModelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllProcessModels() throws Exception {
        // Initialize the database
        processModelRepository.save(processModel);

        // Get all the processModelList
        restProcessModelMockMvc.perform(get("/api/process-models?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(processModel.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    public void getProcessModel() throws Exception {
        // Initialize the database
        processModelRepository.save(processModel);

        // Get the processModel
        restProcessModelMockMvc.perform(get("/api/process-models/{id}", processModel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(processModel.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    public void getNonExistingProcessModel() throws Exception {
        // Get the processModel
        restProcessModelMockMvc.perform(get("/api/process-models/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateProcessModel() throws Exception {
        // Initialize the database
        processModelService.save(processModel);

        int databaseSizeBeforeUpdate = processModelRepository.findAll().size();

        // Update the processModel
        ProcessModel updatedProcessModel = processModelRepository.findOne(processModel.getId());
        updatedProcessModel
            .name(UPDATED_NAME)
            .comment(UPDATED_COMMENT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restProcessModelMockMvc.perform(put("/api/process-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProcessModel)))
            .andExpect(status().isOk());

        // Validate the ProcessModel in the database
        List<ProcessModel> processModelList = processModelRepository.findAll();
        assertThat(processModelList).hasSize(databaseSizeBeforeUpdate);
        ProcessModel testProcessModel = processModelList.get(processModelList.size() - 1);
        assertThat(testProcessModel.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProcessModel.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testProcessModel.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProcessModel.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testProcessModel.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    public void updateNonExistingProcessModel() throws Exception {
        int databaseSizeBeforeUpdate = processModelRepository.findAll().size();

        // Create the ProcessModel

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProcessModelMockMvc.perform(put("/api/process-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(processModel)))
            .andExpect(status().isCreated());

        // Validate the ProcessModel in the database
        List<ProcessModel> processModelList = processModelRepository.findAll();
        assertThat(processModelList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteProcessModel() throws Exception {
        // Initialize the database
        processModelService.save(processModel);

        int databaseSizeBeforeDelete = processModelRepository.findAll().size();

        // Get the processModel
        restProcessModelMockMvc.perform(delete("/api/process-models/{id}", processModel.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ProcessModel> processModelList = processModelRepository.findAll();
        assertThat(processModelList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProcessModel.class);
        ProcessModel processModel1 = new ProcessModel();
        processModel1.setId("id1");
        ProcessModel processModel2 = new ProcessModel();
        processModel2.setId(processModel1.getId());
        assertThat(processModel1).isEqualTo(processModel2);
        processModel2.setId("id2");
        assertThat(processModel1).isNotEqualTo(processModel2);
        processModel1.setId(null);
        assertThat(processModel1).isNotEqualTo(processModel2);
    }
}
