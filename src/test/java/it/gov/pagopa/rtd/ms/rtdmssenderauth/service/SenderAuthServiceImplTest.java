package it.gov.pagopa.rtd.ms.rtdmssenderauth.service;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.controller.SenderRestController.RecordNotPresent;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.controller.SenderRestController.SenderCodeAssociatedToAnotherApiKey;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.model.SenderData;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.repository.SenderAuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        RecordNotPresent.class);
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

  private SenderData createSenderData() {
    return SenderData.builder()
        .senderCodes(new HashSet<>(List.of("senderCode")))
        .apiKey("12345").build();
  }
}
