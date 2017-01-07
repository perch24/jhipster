package com.perch.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perch.domain.SomeEntity;
import com.perch.repository.SomeEntityRepository;
import com.perch.service.dto.SomeEntityDTO;
import com.perch.service.mapper.SomeEntityMapper;
import com.perch.web.rest.util.HeaderUtil;
import com.perch.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing SomeEntity.
 */
@RestController
@RequestMapping("/api")
public class SomeEntityResource {

  private final Logger log = LoggerFactory.getLogger(SomeEntityResource.class);

  @Inject
  private SomeEntityRepository someEntityRepository;

  @Inject
  private SomeEntityMapper someEntityMapper;

  /**
   * POST  /some-entities : Create a new someEntity.
   *
   * @param someEntityDTO the someEntityDTO to create
   * @return the ResponseEntity with status 201 (Created) and with body the new someEntityDTO, or with status 400 (Bad Request) if the someEntity has already an ID
   * @throws URISyntaxException if the Location URI syntax is incorrect
   */
  @PostMapping("/some-entities")
  @Timed
  public ResponseEntity<SomeEntityDTO> createSomeEntity(@Valid @RequestBody SomeEntityDTO someEntityDTO) throws URISyntaxException {
    log.debug("REST request to save SomeEntity : {}", someEntityDTO);
    if (someEntityDTO.getId() != null) {
      return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("someEntity", "idexists", "A new someEntity cannot already have an ID")).body(null);
    }
    SomeEntity someEntity = someEntityMapper.someEntityDTOToSomeEntity(someEntityDTO);
    someEntity = someEntityRepository.save(someEntity);
    SomeEntityDTO result = someEntityMapper.someEntityToSomeEntityDTO(someEntity);
    return ResponseEntity.created(new URI("/api/some-entities/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert("someEntity", result.getId().toString()))
      .body(result);
  }

  /**
   * PUT  /some-entities : Updates an existing someEntity.
   *
   * @param someEntityDTO the someEntityDTO to update
   * @return the ResponseEntity with status 200 (OK) and with body the updated someEntityDTO,
   * or with status 400 (Bad Request) if the someEntityDTO is not valid,
   * or with status 500 (Internal Server Error) if the someEntityDTO couldnt be updated
   * @throws URISyntaxException if the Location URI syntax is incorrect
   */
  @PutMapping("/some-entities")
  @Timed
  public ResponseEntity<SomeEntityDTO> updateSomeEntity(@Valid @RequestBody SomeEntityDTO someEntityDTO) throws URISyntaxException {
    log.debug("REST request to update SomeEntity : {}", someEntityDTO);
    if (someEntityDTO.getId() == null) {
      return createSomeEntity(someEntityDTO);
    }
    SomeEntity someEntity = someEntityMapper.someEntityDTOToSomeEntity(someEntityDTO);
    someEntity = someEntityRepository.save(someEntity);
    SomeEntityDTO result = someEntityMapper.someEntityToSomeEntityDTO(someEntity);
    return ResponseEntity.ok()
      .headers(HeaderUtil.createEntityUpdateAlert("someEntity", someEntityDTO.getId().toString()))
      .body(result);
  }

  /**
   * GET  /some-entities : get all the someEntities.
   *
   * @param pageable the pagination information
   * @return the ResponseEntity with status 200 (OK) and the list of someEntities in body
   * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
   */
  @GetMapping("/some-entities")
  @Timed
  public ResponseEntity<List<SomeEntityDTO>> getAllSomeEntities(@ApiParam Pageable pageable)
    throws URISyntaxException {
    log.debug("REST request to get a page of SomeEntities");
    Page<SomeEntity> page = someEntityRepository.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/some-entities");
    return new ResponseEntity<>(someEntityMapper.someEntitiesToSomeEntityDTOs(page.getContent()), headers, HttpStatus.OK);
  }

  /**
   * GET  /some-entities/:id : get the "id" someEntity.
   *
   * @param id the id of the someEntityDTO to retrieve
   * @return the ResponseEntity with status 200 (OK) and with body the someEntityDTO, or with status 404 (Not Found)
   */
  @GetMapping("/some-entities/{id}")
  @Timed
  public ResponseEntity<SomeEntityDTO> getSomeEntity(@PathVariable String id) {
    log.debug("REST request to get SomeEntity : {}", id);
    SomeEntity someEntity = someEntityRepository.findOne(id);
    SomeEntityDTO someEntityDTO = someEntityMapper.someEntityToSomeEntityDTO(someEntity);
    return Optional.ofNullable(someEntityDTO)
      .map(result -> new ResponseEntity<>(
        result,
        HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * DELETE  /some-entities/:id : delete the "id" someEntity.
   *
   * @param id the id of the someEntityDTO to delete
   * @return the ResponseEntity with status 200 (OK)
   */
  @DeleteMapping("/some-entities/{id}")
  @Timed
  public ResponseEntity<Void> deleteSomeEntity(@PathVariable String id) {
    log.debug("REST request to delete SomeEntity : {}", id);
    someEntityRepository.delete(id);
    return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("someEntity", id.toString())).build();
  }

}
