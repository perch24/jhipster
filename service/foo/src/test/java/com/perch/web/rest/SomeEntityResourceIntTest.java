package com.perch.web.rest;

import com.perch.FooApp;
import com.perch.domain.SomeEntity;
import com.perch.repository.SomeEntityRepository;
import com.perch.service.dto.SomeEntityDTO;
import com.perch.service.mapper.SomeEntityMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SomeEntityResource REST controller.
 *
 * @see SomeEntityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FooApp.class)
public class SomeEntityResourceIntTest {

  private static final String DEFAULT_NAME = "AAAAAAAAAA";
  private static final String UPDATED_NAME = "BBBBBBBBBB";

  @Inject
  private SomeEntityRepository someEntityRepository;

  @Inject
  private SomeEntityMapper someEntityMapper;

  @Inject
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  @Inject
  private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

  private MockMvc restSomeEntityMockMvc;

  private SomeEntity someEntity;

  /**
   * Create an entity for this test.
   * <p>
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static SomeEntity createEntity() {
    SomeEntity someEntity = new SomeEntity()
      .name(DEFAULT_NAME);
    return someEntity;
  }

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    SomeEntityResource someEntityResource = new SomeEntityResource();
    ReflectionTestUtils.setField(someEntityResource, "someEntityRepository", someEntityRepository);
    ReflectionTestUtils.setField(someEntityResource, "someEntityMapper", someEntityMapper);
    this.restSomeEntityMockMvc = MockMvcBuilders.standaloneSetup(someEntityResource)
      .setCustomArgumentResolvers(pageableArgumentResolver)
      .setMessageConverters(jacksonMessageConverter).build();
  }

  @Before
  public void initTest() {
    someEntityRepository.deleteAll();
    someEntity = createEntity();
  }

  @Test
  public void createSomeEntity() throws Exception {
    int databaseSizeBeforeCreate = someEntityRepository.findAll().size();

    // Create the SomeEntity
    SomeEntityDTO someEntityDTO = someEntityMapper.someEntityToSomeEntityDTO(someEntity);

    restSomeEntityMockMvc.perform(post("/api/some-entities")
      .contentType(TestUtil.APPLICATION_JSON_UTF8)
      .content(TestUtil.convertObjectToJsonBytes(someEntityDTO)))
      .andExpect(status().isCreated());

    // Validate the SomeEntity in the database
    List<SomeEntity> someEntityList = someEntityRepository.findAll();
    assertThat(someEntityList).hasSize(databaseSizeBeforeCreate + 1);
    SomeEntity testSomeEntity = someEntityList.get(someEntityList.size() - 1);
    assertThat(testSomeEntity.getName()).isEqualTo(DEFAULT_NAME);
  }

  @Test
  public void createSomeEntityWithExistingId() throws Exception {
    int databaseSizeBeforeCreate = someEntityRepository.findAll().size();

    // Create the SomeEntity with an existing ID
    SomeEntity existingSomeEntity = new SomeEntity();
    existingSomeEntity.setId("existing_id");
    SomeEntityDTO existingSomeEntityDTO = someEntityMapper.someEntityToSomeEntityDTO(existingSomeEntity);

    // An entity with an existing ID cannot be created, so this API call must fail
    restSomeEntityMockMvc.perform(post("/api/some-entities")
      .contentType(TestUtil.APPLICATION_JSON_UTF8)
      .content(TestUtil.convertObjectToJsonBytes(existingSomeEntityDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Alice in the database
    List<SomeEntity> someEntityList = someEntityRepository.findAll();
    assertThat(someEntityList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  public void checkNameIsRequired() throws Exception {
    int databaseSizeBeforeTest = someEntityRepository.findAll().size();
    // set the field null
    someEntity.setName(null);

    // Create the SomeEntity, which fails.
    SomeEntityDTO someEntityDTO = someEntityMapper.someEntityToSomeEntityDTO(someEntity);

    restSomeEntityMockMvc.perform(post("/api/some-entities")
      .contentType(TestUtil.APPLICATION_JSON_UTF8)
      .content(TestUtil.convertObjectToJsonBytes(someEntityDTO)))
      .andExpect(status().isBadRequest());

    List<SomeEntity> someEntityList = someEntityRepository.findAll();
    assertThat(someEntityList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  public void getAllSomeEntities() throws Exception {
    // Initialize the database
    someEntityRepository.save(someEntity);

    // Get all the someEntityList
    restSomeEntityMockMvc.perform(get("/api/some-entities?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(someEntity.getId())))
      .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
  }

  @Test
  public void getSomeEntity() throws Exception {
    // Initialize the database
    someEntityRepository.save(someEntity);

    // Get the someEntity
    restSomeEntityMockMvc.perform(get("/api/some-entities/{id}", someEntity.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
      .andExpect(jsonPath("$.id").value(someEntity.getId()))
      .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
  }

  @Test
  public void getNonExistingSomeEntity() throws Exception {
    // Get the someEntity
    restSomeEntityMockMvc.perform(get("/api/some-entities/{id}", Long.MAX_VALUE))
      .andExpect(status().isNotFound());
  }

  @Test
  public void updateSomeEntity() throws Exception {
    // Initialize the database
    someEntityRepository.save(someEntity);
    int databaseSizeBeforeUpdate = someEntityRepository.findAll().size();

    // Update the someEntity
    SomeEntity updatedSomeEntity = someEntityRepository.findOne(someEntity.getId());
    updatedSomeEntity
      .name(UPDATED_NAME);
    SomeEntityDTO someEntityDTO = someEntityMapper.someEntityToSomeEntityDTO(updatedSomeEntity);

    restSomeEntityMockMvc.perform(put("/api/some-entities")
      .contentType(TestUtil.APPLICATION_JSON_UTF8)
      .content(TestUtil.convertObjectToJsonBytes(someEntityDTO)))
      .andExpect(status().isOk());

    // Validate the SomeEntity in the database
    List<SomeEntity> someEntityList = someEntityRepository.findAll();
    assertThat(someEntityList).hasSize(databaseSizeBeforeUpdate);
    SomeEntity testSomeEntity = someEntityList.get(someEntityList.size() - 1);
    assertThat(testSomeEntity.getName()).isEqualTo(UPDATED_NAME);
  }

  @Test
  public void updateNonExistingSomeEntity() throws Exception {
    int databaseSizeBeforeUpdate = someEntityRepository.findAll().size();

    // Create the SomeEntity
    SomeEntityDTO someEntityDTO = someEntityMapper.someEntityToSomeEntityDTO(someEntity);

    // If the entity doesn't have an ID, it will be created instead of just being updated
    restSomeEntityMockMvc.perform(put("/api/some-entities")
      .contentType(TestUtil.APPLICATION_JSON_UTF8)
      .content(TestUtil.convertObjectToJsonBytes(someEntityDTO)))
      .andExpect(status().isCreated());

    // Validate the SomeEntity in the database
    List<SomeEntity> someEntityList = someEntityRepository.findAll();
    assertThat(someEntityList).hasSize(databaseSizeBeforeUpdate + 1);
  }

  @Test
  public void deleteSomeEntity() throws Exception {
    // Initialize the database
    someEntityRepository.save(someEntity);
    int databaseSizeBeforeDelete = someEntityRepository.findAll().size();

    // Get the someEntity
    restSomeEntityMockMvc.perform(delete("/api/some-entities/{id}", someEntity.getId())
      .accept(TestUtil.APPLICATION_JSON_UTF8))
      .andExpect(status().isOk());

    // Validate the database is empty
    List<SomeEntity> someEntityList = someEntityRepository.findAll();
    assertThat(someEntityList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
