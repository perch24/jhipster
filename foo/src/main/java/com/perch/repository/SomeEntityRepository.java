package com.perch.repository;

import com.perch.domain.SomeEntity;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the SomeEntity entity.
 */
@SuppressWarnings("unused")
public interface SomeEntityRepository extends MongoRepository<SomeEntity,String> {

}
