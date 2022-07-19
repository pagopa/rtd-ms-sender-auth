package it.gov.pagopa.rtd.ms.rtdmssenderauth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {

  private int id;
  private String name;
  private String hash;

  @Override
  public String toString() {
    return "FileMetadata{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", hash='" + hash + '\'' +
        '}';
  }
}
