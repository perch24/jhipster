package com.perch.service.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the SomeEntity entity.
 */
public class SomeEntityDTO implements Serializable {

  private String id;

  @NotNull
  private String name;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SomeEntityDTO someEntityDTO = (SomeEntityDTO) o;

    if (!Objects.equals(id, someEntityDTO.id)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "SomeEntityDTO{" +
      "id=" + id +
      ", name='" + name + "'" +
      '}';
  }
}
