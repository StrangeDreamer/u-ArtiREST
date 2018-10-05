package me.daisyliao.service.web.rest;

import me.daisyliao.service.ServiceApp;

import me.daisyliao.service.client.ProcessClient;
import me.daisyliao.service.config.SecurityBeanOverrideConfiguration;

import me.daisyliao.service.domain.ServiceModel;
import me.daisyliao.service.domain.enumeration.ServiceType;
import me.daisyliao.service.repository.ServiceModelRepository;
import me.daisyliao.service.service.ServiceService;
import me.daisyliao.service.web.rest.errors.ExceptionTranslator;

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

import java.util.List;

import static me.daisyliao.service.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ServiceModelResource REST controller.
 *
 * @see ServiceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ServiceApp.class, SecurityBeanOverrideConfiguration.class})
public class ServiceModelResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICE_CLASS = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_CLASS = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final ServiceModel.RestMethod DEFAULT_METHOD = ServiceModel.RestMethod.GET;
    private static final ServiceModel.RestMethod UPDATED_METHOD = ServiceModel.RestMethod.PUT;

    private static final String DEFAULT_INPUT_ARTIFACT = "AAAAAAAAAA";
    private static final String UPDATED_INPUT_ARTIFACT = "BBBBBBBBBB";

    private static final String DEFAULT_OUTPUT_ARTIFACT = "AAAAAAAAAA";
    private static final String UPDATED_OUTPUT_ARTIFACT = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final ServiceType DEFAULT_TYPE = ServiceType.HUMAN_TASK;
    private static final ServiceType UPDATED_TYPE = ServiceType.INVOKE_SERVICE;

    @Autowired
    private ServiceModelRepository serviceModelRepository;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restServiceModelMockMvc;

    private ServiceModel serviceModel;
    private ProcessClient processClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ServiceResource serviceModelResource = new ServiceResource(processClient, serviceService);
        this.restServiceModelMockMvc = MockMvcBuilders.standaloneSetup(serviceModelResource)
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
    public static ServiceModel createEntity() {
        ServiceModel serviceModel = new ServiceModel()
            .name(DEFAULT_NAME)
            .serviceClass(DEFAULT_SERVICE_CLASS)
            .url(DEFAULT_URL)
            .method(DEFAULT_METHOD)
            .inputArtifact(DEFAULT_INPUT_ARTIFACT)
            .outputArtifact(DEFAULT_OUTPUT_ARTIFACT)
            .comment(DEFAULT_COMMENT)
            .type(DEFAULT_TYPE);
        return serviceModel;
    }

    @Before
    public void initTest() {
        serviceModelRepository.deleteAll();
        serviceModel = createEntity();
    }

    @Test
    public void createServiceModel() throws Exception {
        int databaseSizeBeforeCreate = serviceModelRepository.findAll().size();

        // Create the ServiceModel
        restServiceModelMockMvc.perform(post("/api/service-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceModel)))
            .andExpect(status().isCreated());

        // Validate the ServiceModel in the database
        List<ServiceModel> serviceModelList = serviceModelRepository.findAll();
        assertThat(serviceModelList).hasSize(databaseSizeBeforeCreate + 1);
        ServiceModel testServiceModel = serviceModelList.get(serviceModelList.size() - 1);
        assertThat(testServiceModel.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testServiceModel.getServiceClass()).isEqualTo(DEFAULT_SERVICE_CLASS);
        assertThat(testServiceModel.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testServiceModel.getMethod()).isEqualTo(DEFAULT_METHOD);
        assertThat(testServiceModel.getInputArtifact()).isEqualTo(DEFAULT_INPUT_ARTIFACT);
        assertThat(testServiceModel.getOutputArtifact()).isEqualTo(DEFAULT_OUTPUT_ARTIFACT);
        assertThat(testServiceModel.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testServiceModel.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    public void createServiceModelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = serviceModelRepository.findAll().size();

        // Create the ServiceModel with an existing ID
        serviceModel.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceModelMockMvc.perform(post("/api/service-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceModel)))
            .andExpect(status().isBadRequest());

        // Validate the ServiceModel in the database
        List<ServiceModel> serviceModelList = serviceModelRepository.findAll();
        assertThat(serviceModelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllServiceModels() throws Exception {
        // Initialize the database
        serviceModelRepository.save(serviceModel);

        // Get all the serviceModelList
        restServiceModelMockMvc.perform(get("/api/service-models?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceModel.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].serviceClass").value(hasItem(DEFAULT_SERVICE_CLASS.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].inputArtifact").value(hasItem(DEFAULT_INPUT_ARTIFACT.toString())))
            .andExpect(jsonPath("$.[*].outputArtifact").value(hasItem(DEFAULT_OUTPUT_ARTIFACT.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    public void getServiceModel() throws Exception {
        // Initialize the database
        serviceModelRepository.save(serviceModel);

        // Get the serviceModel
        restServiceModelMockMvc.perform(get("/api/service-models/{id}", serviceModel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(serviceModel.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.serviceClass").value(DEFAULT_SERVICE_CLASS.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.method").value(DEFAULT_METHOD.toString()))
            .andExpect(jsonPath("$.inputArtifact").value(DEFAULT_INPUT_ARTIFACT.toString()))
            .andExpect(jsonPath("$.outputArtifact").value(DEFAULT_OUTPUT_ARTIFACT.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    public void getNonExistingServiceModel() throws Exception {
        // Get the serviceModel
        restServiceModelMockMvc.perform(get("/api/service-models/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateServiceModel() throws Exception {
        // Initialize the database
        serviceService.save(serviceModel);

        int databaseSizeBeforeUpdate = serviceModelRepository.findAll().size();

        // Update the serviceModel
        ServiceModel updatedServiceModel = serviceModelRepository.findOne(serviceModel.getId());
        updatedServiceModel
            .name(UPDATED_NAME)
            .serviceClass(UPDATED_SERVICE_CLASS)
            .url(UPDATED_URL)
            .method(UPDATED_METHOD)
            .inputArtifact(UPDATED_INPUT_ARTIFACT)
            .outputArtifact(UPDATED_OUTPUT_ARTIFACT)
            .comment(UPDATED_COMMENT)
            .type(UPDATED_TYPE);

        restServiceModelMockMvc.perform(put("/api/service-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedServiceModel)))
            .andExpect(status().isOk());

        // Validate the ServiceModel in the database
        List<ServiceModel> serviceModelList = serviceModelRepository.findAll();
        assertThat(serviceModelList).hasSize(databaseSizeBeforeUpdate);
        ServiceModel testServiceModel = serviceModelList.get(serviceModelList.size() - 1);
        assertThat(testServiceModel.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testServiceModel.getServiceClass()).isEqualTo(UPDATED_SERVICE_CLASS);
        assertThat(testServiceModel.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testServiceModel.getMethod()).isEqualTo(UPDATED_METHOD);
        assertThat(testServiceModel.getInputArtifact()).isEqualTo(UPDATED_INPUT_ARTIFACT);
        assertThat(testServiceModel.getOutputArtifact()).isEqualTo(UPDATED_OUTPUT_ARTIFACT);
        assertThat(testServiceModel.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testServiceModel.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    public void updateNonExistingServiceModel() throws Exception {
        int databaseSizeBeforeUpdate = serviceModelRepository.findAll().size();

        // Create the ServiceModel

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restServiceModelMockMvc.perform(put("/api/service-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(serviceModel)))
            .andExpect(status().isCreated());

        // Validate the ServiceModel in the database
        List<ServiceModel> serviceModelList = serviceModelRepository.findAll();
        assertThat(serviceModelList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteServiceModel() throws Exception {
        // Initialize the database
        serviceService.save(serviceModel);

        int databaseSizeBeforeDelete = serviceModelRepository.findAll().size();

        // Get the serviceModel
        restServiceModelMockMvc.perform(delete("/api/service-models/{id}", serviceModel.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ServiceModel> serviceModelList = serviceModelRepository.findAll();
        assertThat(serviceModelList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceModel.class);
        ServiceModel serviceModel1 = new ServiceModel();
        serviceModel1.setId("id1");
        ServiceModel serviceModel2 = new ServiceModel();
        serviceModel2.setId(serviceModel1.getId());
        assertThat(serviceModel1).isEqualTo(serviceModel2);
        serviceModel2.setId("id2");
        assertThat(serviceModel1).isNotEqualTo(serviceModel2);
        serviceModel1.setId(null);
        assertThat(serviceModel1).isNotEqualTo(serviceModel2);
    }
}
