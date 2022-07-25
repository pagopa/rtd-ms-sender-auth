package it.gov.pagopa.rtd.ms.rtdmssenderauth.model;

import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@EqualsAndHashCode(exclude = {"id"})
@Builder
@Document("senderauth")
public class SenderData {

  @Id
  private String id;
  private String senderCode;
  private Set<String> apiKeys;

  public void addApiKey(String apiKey) {
    apiKeys.add(apiKey);
  }
}
