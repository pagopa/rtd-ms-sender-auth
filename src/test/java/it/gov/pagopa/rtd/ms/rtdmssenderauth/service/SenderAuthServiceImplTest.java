package it.gov.pagopa.rtd.ms.rtdmssenderauth.service;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.RecordNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.SenderCodeAssociatedToAnotherApiKey;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.model.SenderData;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.repository.SenderAuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SenderAuthServiceImplTest {

  @Mock
  private SenderAuthRepository senderAuthRepository;
  private SenderAuthServiceImpl senderAuthService;

  private SenderData defaultSenderData;

  @BeforeEach
  void setup() {
    senderAuthService = new SenderAuthServiceImpl(senderAuthRepository);
    defaultSenderData = createSenderData();
  }

  @Test
  void whenGetSenderCodeIsNotFoundThenExceptionIsThrow() {
    BDDMockito.doReturn(Optional.empty()).when(senderAuthRepository).findByApiKey(any());

    assertThatThrownBy(() -> senderAuthService.getSenderCodes("any")).isInstanceOf(
        RecordNotFoundException.class);
  }

  @Test
  void whenGetSenderCodeIsFoundThenReturnsTheSenderCode() {
    BDDMockito.doReturn(Optional.of(defaultSenderData)).when(senderAuthRepository)
        .findByApiKey(any());

    Set<String> senderCodes = senderAuthService.getSenderCodes("any");

    assertThat(senderCodes).contains("senderCode");
  }

  @Test
  void whenApiKeyIsAlreadyAssociatedToAnotherSenderCodeThenAddAnotherSenderCode() {
    BDDMockito.doReturn(Optional.of(defaultSenderData)).when(senderAuthRepository)
            .findByApiKey("12345");

    senderAuthService.saveApiKey("anotherSenderCode", "12345");

    Mockito.verify(senderAuthRepository, Mockito.times(1)).save(any());
  }

  @Test
  void whenApiKeyIsNotSavedThenSaveTheNewAssociationWithSenderCode() {
    BDDMockito.doReturn(Optional.empty()).when(senderAuthRepository)
        .findByApiKey("12345");
    BDDMockito.doReturn(Collections.emptyList()).when(senderAuthRepository).findBySenderCode(any());

    senderAuthService.saveApiKey("senderCode", "12345");

    Mockito.verify(senderAuthRepository, Mockito.times(1)).save(defaultSenderData);
  }

  @Test
  void whenTheApiKeyIsAlreadyAssociatedToTheSameSenderCodeThenDoNothing() {
    BDDMockito.doReturn(Optional.of(defaultSenderData)).when(senderAuthRepository)
        .findByApiKey("12345");

    senderAuthService.saveApiKey("senderCode", "12345");

    Mockito.verify(senderAuthRepository, Mockito.times(0)).save(defaultSenderData);
  }

  @Test
  void whenSenderCodeIsAlreadyAssociateToAnotherApiThenExceptionIsThrow() {
    BDDMockito.doReturn(List.of(defaultSenderData)).when(senderAuthRepository)
            .findBySenderCode("senderCode");

    assertThatThrownBy(() -> senderAuthService.saveApiKey("senderCode", "56789"))
            .isInstanceOf(SenderCodeAssociatedToAnotherApiKey.class);
  }

  @Test
  void whenDeleteASenderCodeFromAnApiKeyWithMultipleAssociationsThenKeepOtherAssociation() {
    final var sender = createSenderWithMultipleAssociation();
    BDDMockito.doReturn(Optional.of(sender)).when(senderAuthRepository).findByApiKey(any());

    senderAuthService.deleteAssociation("senderCode1", "any");

    assertFalse(sender.isAssociatedTo("senderCode1"));
    assertTrue(sender.isAssociatedTo("senderCode2"));
    assertFalse(sender.hasNoAssociations());

    Mockito.verify(senderAuthRepository, Mockito.times(1)).save(sender);
  }

  @Test
  void whenDeleteASenderCodeFromAnApiKeyWithOnlyOneThenDeleteWholeAssociation() {
    final var sender = createSenderData();
    BDDMockito.doReturn(Optional.of(sender)).when(senderAuthRepository).findByApiKey(any());

    senderAuthService.deleteAssociation("senderCode", "any");
    assertTrue(sender.hasNoAssociations());

    Mockito.verify(senderAuthRepository, Mockito.times(1)).deleteByApiKey("any");
  }

  @Test
  void whenDeleteNonAssociatedSenderCodeFromAnExistingApiKeyThenNothingHappens() {
    final var sender = createSenderData();
    BDDMockito.doReturn(Optional.of(sender)).when(senderAuthRepository).findByApiKey(any());

    senderAuthService.deleteAssociation("senderCode3", "any");
    assertFalse(sender.hasNoAssociations());

    Mockito.verify(senderAuthRepository, Mockito.times(1)).save(sender);
  }

  @Test
  void whenDeleteASenderCodeFromAnNonExistingApiKeyThenNothingHappens() {
    BDDMockito.doReturn(Optional.empty()).when(senderAuthRepository).findByApiKey(any());

    senderAuthService.deleteAssociation("senderCode", "any");

    Mockito.verify(senderAuthRepository, Mockito.times(0)).save(any());
    Mockito.verify(senderAuthRepository, Mockito.times(0)).deleteByApiKey(any());
  }

  private SenderData createSenderData() {
    return SenderData.builder()
        .senderCodes(new HashSet<>(List.of("senderCode")))
        .apiKey("12345").build();
  }

  private SenderData createSenderWithMultipleAssociation() {
    return SenderData.builder()
            .senderCodes(new HashSet<>(List.of("senderCode1", "senderCode2")))
            .apiKey("12345")
            .build();
  }
}
