package me.daisyliao.businessrule.web.rest;

import me.daisyliao.businessrule.BusinessRuleApp;

import me.daisyliao.businessrule.config.SecurityBeanOverrideConfiguration;

import me.daisyliao.businessrule.domain.BusinessRuleModel;
import me.daisyliao.businessrule.repository.BusinessRuleModelRepository;
import me.daisyliao.businessrule.service.BusinessRuleService;
import me.daisyliao.businessrule.web.rest.errors.ExceptionTranslator;

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

import static me.daisyliao.businessrule.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BusinessRuleResource REST controller.
 *
 * @see BusinessRuleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BusinessRuleApp.class, SecurityBeanOverrideConfiguration.class})
public class BusinessRuleResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BUSINESS_RULE_CLASS = "AAAAAAAAAA";
    private static final String UPDATED_BUSINESS_RULE_CLASS = "BBBBBBBBBB";

    @Autowired
    private BusinessRuleModelRepository businessRuleModelRepository;

    @Autowired
    private BusinessRuleService businessRuleService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restBusinessRuleMockMvc;

    private BusinessRuleModel businessRuleModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BusinessRuleResource businessRuleResource = new BusinessRuleResource(businessRuleService);
        this.restBusinessRuleMockMvc = MockMvcBuilders.standaloneSetup(businessRuleResource)
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
    public static BusinessRuleModel createEntity() {
        BusinessRuleModel businessRule = new BusinessRuleModel()
            .name(DEFAULT_NAME)
            .businessRuleClass(DEFAULT_BUSINESS_RULE_CLASS);
        return businessRule;
    }

    @Before
    public void initTest() {
        businessRuleModelRepository.deleteAll();
        businessRuleModel = createEntity();
    }

    @Test
    public void createBusinessRule() throws Exception {
        int databaseSizeBeforeCreate = businessRuleModelRepository.findAll().size();

        // Create the BusinessRule
        restBusinessRuleMockMvc.perform(post("/api/business-rules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessRuleModel)))
            .andExpect(status().isCreated());

        // Validate the BusinessRule in the database
        List<BusinessRuleModel> businessRuleList = businessRuleModelRepository.findAll();
        assertThat(businessRuleList).hasSize(databaseSizeBeforeCreate + 1);
        BusinessRuleModel testBusinessRule = businessRuleList.get(businessRuleList.size() - 1);
        assertThat(testBusinessRule.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBusinessRule.getBusinessRuleClass()).isEqualTo(DEFAULT_BUSINESS_RULE_CLASS);
    }

    @Test
    public void createBusinessRuleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = businessRuleModelRepository.findAll().size();

        // Create the BusinessRule with an existing ID
        businessRuleModel.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessRuleMockMvc.perform(post("/api/business-rules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessRuleModel)))
            .andExpect(status().isBadRequest());

        // Validate the BusinessRule in the database
        List<BusinessRuleModel> businessRuleList = businessRuleModelRepository.findAll();
        assertThat(businessRuleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllBusinessRules() throws Exception {
        // Initialize the database
        businessRuleModelRepository.save(businessRuleModel);

        // Get all the businessRuleList
        restBusinessRuleMockMvc.perform(get("/api/business-rules?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessRuleModel.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].businessRuleClass").value(hasItem(DEFAULT_BUSINESS_RULE_CLASS.toString())));
    }

    @Test
    public void getBusinessRule() throws Exception {
        // Initialize the database
        businessRuleModelRepository.save(businessRuleModel);

        // Get the businessRule
        restBusinessRuleMockMvc.perform(get("/api/business-rules/{id}", businessRuleModel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(businessRuleModel.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.businessRuleClass").value(DEFAULT_BUSINESS_RULE_CLASS.toString()));
    }

    @Test
    public void getNonExistingBusinessRule() throws Exception {
        // Get the businessRule
        restBusinessRuleMockMvc.perform(get("/api/business-rules/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateBusinessRule() throws Exception {
        // Initialize the database
        businessRuleService.save(businessRuleModel);

        int databaseSizeBeforeUpdate = businessRuleModelRepository.findAll().size();

        // Update the businessRule
        BusinessRuleModel updatedBusinessRule = businessRuleModelRepository.findOne(businessRuleModel.getId());
        updatedBusinessRule
            .name(UPDATED_NAME)
            .businessRuleClass(UPDATED_BUSINESS_RULE_CLASS);

        restBusinessRuleMockMvc.perform(put("/api/business-rules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBusinessRule)))
            .andExpect(status().isOk());

        // Validate the BusinessRule in the database
        List<BusinessRuleModel> businessRuleList = businessRuleModelRepository.findAll();
        assertThat(businessRuleList).hasSize(databaseSizeBeforeUpdate);
        BusinessRuleModel testBusinessRule = businessRuleList.get(businessRuleList.size() - 1);
        assertThat(testBusinessRule.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusinessRule.getBusinessRuleClass()).isEqualTo(UPDATED_BUSINESS_RULE_CLASS);
    }

    @Test
    public void updateNonExistingBusinessRule() throws Exception {
        int databaseSizeBeforeUpdate = businessRuleModelRepository.findAll().size();

        // Create the BusinessRule

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBusinessRuleMockMvc.perform(put("/api/business-rules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessRuleModel)))
            .andExpect(status().isCreated());

        // Validate the BusinessRule in the database
        List<BusinessRuleModel> businessRuleList = businessRuleModelRepository.findAll();
        assertThat(businessRuleList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteBusinessRule() throws Exception {
        // Initialize the database
        businessRuleService.save(businessRuleModel);

        int databaseSizeBeforeDelete = businessRuleModelRepository.findAll().size();

        // Get the businessRule
        restBusinessRuleMockMvc.perform(delete("/api/business-rules/{id}", businessRuleModel.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<BusinessRuleModel> businessRuleList = businessRuleModelRepository.findAll();
        assertThat(businessRuleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessRuleModel.class);
        BusinessRuleModel businessRule1 = new BusinessRuleModel();
        businessRule1.setId("id1");
        BusinessRuleModel businessRule2 = new BusinessRuleModel();
        businessRule2.setId(businessRule1.getId());
        assertThat(businessRule1).isEqualTo(businessRule2);
        businessRule2.setId("id2");
        assertThat(businessRule1).isNotEqualTo(businessRule2);
        businessRule1.setId(null);
        assertThat(businessRule1).isNotEqualTo(businessRule2);
    }
}
