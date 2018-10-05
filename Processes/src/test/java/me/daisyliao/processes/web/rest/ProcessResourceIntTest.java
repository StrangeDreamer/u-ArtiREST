package me.daisyliao.processes.web.rest;

import me.daisyliao.processes.ProcessesApp;

import me.daisyliao.processes.domain.Process;
import me.daisyliao.processes.repository.ProcessRepository;
import me.daisyliao.processes.service.ProcessService;
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
 * Test class for the ProcessResource REST controller.
 *
 * @see ProcessResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessesApp.class)
public class ProcessResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_RUNNING = false;
    private static final Boolean UPDATED_IS_RUNNING = true;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ProcessService processService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restProcessMockMvc;

    private Process process;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProcessResource processResource = new ProcessResource(processService);
        this.restProcessMockMvc = MockMvcBuilders.standaloneSetup(processResource)
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
    public static Process createEntity() {
        Process process = new Process()
            .name(DEFAULT_NAME)
            .isRunning(DEFAULT_IS_RUNNING)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return process;
    }

    @Before
    public void initTest() {
        processRepository.deleteAll();
        process = createEntity();
    }

    @Test
    public void createProcess() throws Exception {
        int databaseSizeBeforeCreate = processRepository.findAll().size();

        // Create the Process
        restProcessMockMvc.perform(post("/api/processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(process)))
            .andExpect(status().isCreated());

        // Validate the Process in the database
        List<Process> processList = processRepository.findAll();
        assertThat(processList).hasSize(databaseSizeBeforeCreate + 1);
        Process testProcess = processList.get(processList.size() - 1);
        assertThat(testProcess.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProcess.isIsRunning()).isEqualTo(DEFAULT_IS_RUNNING);
        assertThat(testProcess.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testProcess.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    public void createProcessWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = processRepository.findAll().size();

        // Create the Process with an existing ID
        process.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restProcessMockMvc.perform(post("/api/processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(process)))
            .andExpect(status().isBadRequest());

        // Validate the Process in the database
        List<Process> processList = processRepository.findAll();
        assertThat(processList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllProcesses() throws Exception {
        // Initialize the database
        processRepository.save(process);

        // Get all the processList
        restProcessMockMvc.perform(get("/api/processes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(process.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].isRunning").value(hasItem(DEFAULT_IS_RUNNING.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    public void getProcess() throws Exception {
        // Initialize the database
        processRepository.save(process);

        // Get the process
        restProcessMockMvc.perform(get("/api/processes/{id}", process.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(process.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.isRunning").value(DEFAULT_IS_RUNNING.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    public void getNonExistingProcess() throws Exception {
        // Get the process
        restProcessMockMvc.perform(get("/api/processes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateProcess() throws Exception {
        // Initialize the database
        processService.save(process);

        int databaseSizeBeforeUpdate = processRepository.findAll().size();

        // Update the process
        Process updatedProcess = processRepository.findOne(process.getId());
        updatedProcess
            .name(UPDATED_NAME)
            .isRunning(UPDATED_IS_RUNNING)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restProcessMockMvc.perform(put("/api/processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProcess)))
            .andExpect(status().isOk());

        // Validate the Process in the database
        List<Process> processList = processRepository.findAll();
        assertThat(processList).hasSize(databaseSizeBeforeUpdate);
        Process testProcess = processList.get(processList.size() - 1);
        assertThat(testProcess.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProcess.isIsRunning()).isEqualTo(UPDATED_IS_RUNNING);
        assertThat(testProcess.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testProcess.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    public void updateNonExistingProcess() throws Exception {
        int databaseSizeBeforeUpdate = processRepository.findAll().size();

        // Create the Process

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProcessMockMvc.perform(put("/api/processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(process)))
            .andExpect(status().isCreated());

        // Validate the Process in the database
        List<Process> processList = processRepository.findAll();
        assertThat(processList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteProcess() throws Exception {
        // Initialize the database
        processService.save(process);

        int databaseSizeBeforeDelete = processRepository.findAll().size();

        // Get the process
        restProcessMockMvc.perform(delete("/api/processes/{id}", process.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Process> processList = processRepository.findAll();
        assertThat(processList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Process.class);
        Process process1 = new Process();
        process1.setId("id1");
        Process process2 = new Process();
        process2.setId(process1.getId());
        assertThat(process1).isEqualTo(process2);
        process2.setId("id2");
        assertThat(process1).isNotEqualTo(process2);
        process1.setId(null);
        assertThat(process1).isNotEqualTo(process2);
    }
}
