package it.gov.pagopa.rtd.ms.rtdmssenderauth.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@EqualsAndHashCode(exclude = {"id"})
@Builder
@Document("senderauth")
public class SenderData {

  @Id
  private String id;
  private Set<String> senderCodes;
  private String apiKey;


  public boolean addSenderAssociation(String senderCode) {
    return this.senderCodes.add(senderCode);
  }
}
