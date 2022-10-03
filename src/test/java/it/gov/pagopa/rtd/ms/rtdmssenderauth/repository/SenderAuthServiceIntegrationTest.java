package it.gov.pagopa.rtd.ms.rtdmssenderauth.repository;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.RecordNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.SenderCodeAssociatedToAnotherApiKey;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.model.SenderData;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.service.SenderAuthServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest()
@AutoConfigureDataMongo
class SenderAuthServiceIntegrationTest {

  @Autowired
  private SenderAuthServiceImpl senderAuthService;

  @AfterEach
  void clean(@Autowired MongoTemplate mongoTemplate) {
    mongoTemplate.dropCollection(SenderData.class);
  }

  @Test
  void whenSaveAnAssociationThenMustBeFound() {
    senderAuthService.saveApiKey("senderCode1", "apiKey1");

    assertThat(senderAuthService.getSenderCodes("apiKey1")).isEqualTo(Set.of("senderCode1"));
  }

  @Test
  void whenSaveMultipleAssociationThenMustBeFound() {
    senderAuthService.saveApiKey("senderCode1", "apiKey");
    senderAuthService.saveApiKey("senderCode2", "apiKey");

    assertThat(senderAuthService.getSenderCodes("apiKey")).isEqualTo(Set.of("senderCode1", "senderCode2"));
  }

  @Test
  void whenFindApiKeyAfterDeleteTheLatestSenderCodeThenThrowNoRecordFound() {
    senderAuthService.saveApiKey("senderCode2", "apiKey");
    senderAuthService.deleteAssociation("senderCode2", "apiKey");

    assertThatThrownBy(() -> senderAuthService.getSenderCodes("apiKey")).isInstanceOf(RecordNotFoundException.class);
  }

  @Test
  void whenDeleteASenderCodeFromApiKeyWithMultipleAssociationThenFindOtherSenderCode() {
    senderAuthService.saveApiKey("senderCode1", "apiKey");
    senderAuthService.saveApiKey("senderCode2", "apiKey");
    senderAuthService.deleteAssociation("senderCode1", "apiKey");

    assertThat(senderAuthService.getSenderCodes("apiKey")).isEqualTo(Set.of("senderCode2"));
  }

  @Test
  void whenSenderCodeIsAlreadyAssociateToAnotherApiThenExceptionIsThrow() {
    senderAuthService.saveApiKey("senderCode1", "apiKey");

    Assertions.assertThatThrownBy(() -> senderAuthService.saveApiKey("senderCode1", "apiKey2"))
            .isInstanceOf(SenderCodeAssociatedToAnotherApiKey.class);
  }

  private SenderData createSenderData(Set<String> senderCodes) {
    return SenderData.builder()
            .apiKey("12345")
            .senderCodes(senderCodes)
            .build();
  }

}
