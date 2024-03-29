package it.gov.pagopa.rtd.ms.rtdmssenderauth.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.RecordNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.SenderCodeAssociatedToAnotherApiKey;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.model.SenderData;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.service.SenderAuthServiceImpl;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class SenderAuthServiceIntegrationTest {

  @Container
  public static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:4.4.4");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
  }

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private SenderAuthServiceImpl senderAuthService;

  @AfterEach
  void clean() {
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

    assertThat(senderAuthService.getSenderCodes("apiKey")).isEqualTo(
        Set.of("senderCode1", "senderCode2"));
  }

  @Test
  void whenFindApiKeyAfterDeleteTheLatestSenderCodeThenThrowNoRecordFound() {
    senderAuthService.saveApiKey("senderCode2", "apiKey");
    senderAuthService.deleteAssociation("senderCode2", "apiKey");

    assertThatThrownBy(() -> senderAuthService.getSenderCodes("apiKey")).isInstanceOf(
        RecordNotFoundException.class);
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

  @Test
  void whenSenderCodeIsAssociatedThenIsAuthorized() {
    senderAuthService.saveApiKey("senderCode", "12345");
    assertTrue(senderAuthService.authorize("senderCode", "12345"));
  }

  @Test
  void whenSenderCodeIsNotAssociatedToAnApiKeyThenIsUnauthorized() {
    senderAuthService.saveApiKey("senderCode", "12345");
    assertFalse(senderAuthService.authorize("senderCode1", "12345"));
  }

}
