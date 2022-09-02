package it.gov.pagopa.rtd.ms.rtdmssenderauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.exception.InternalIdAlreadyAssociatedException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.exception.RecordNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.model.SenderData;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.repository.SenderAuthRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    assertThatThrownBy(() -> senderAuthService.getSenderCode("any")).isInstanceOf(
        RecordNotFoundException.class);
  }

  @Test
  void whenGetSenderCodeIsFoundThenReturnsTheSenderCode() {
    BDDMockito.doReturn(Optional.of(defaultSenderData)).when(senderAuthRepository)
        .findByApiKey(any());

    String senderCode = senderAuthService.getSenderCode("any");

    assertThat(senderCode).isEqualTo("senderCode");
  }

  @Test
  void whenApiKeyIsAlreadyAssociatedToAnotherSenderCodeThenThrowException() {
    BDDMockito.doReturn(Optional.of(defaultSenderData)).when(senderAuthRepository)
        .findByApiKey("12345");

    assertThatThrownBy(() -> senderAuthService.saveApiKey("anotherSenderCode", "12345"))
        .isInstanceOf(InternalIdAlreadyAssociatedException.class);
    Mockito.verify(senderAuthRepository, Mockito.times(0)).save(any());
    Mockito.verify(senderAuthRepository, Mockito.times(0)).findBySenderCode(any());
  }

  @Test
  void whenApiKeyIsNotSavedThenSaveTheNewAssociationWithSenderCode() {
    BDDMockito.doReturn(Optional.empty()).when(senderAuthRepository)
        .findByApiKey("12345");

    senderAuthService.saveApiKey("senderCode", "12345");

    Mockito.verify(senderAuthRepository, Mockito.times(1)).deleteBySenderCode("senderCode");
    Mockito.verify(senderAuthRepository, Mockito.times(1)).save(defaultSenderData);
  }

  @Test
  void whenTheApiKeyIsAlreadyAssociatedToTheSameSenderCodeThenDoNothing() {
    BDDMockito.doReturn(Optional.of(defaultSenderData)).when(senderAuthRepository)
        .findByApiKey("12345");

    senderAuthService.saveApiKey("senderCode", "12345");

    Mockito.verify(senderAuthRepository, Mockito.times(0)).deleteBySenderCode("senderCode");
    Mockito.verify(senderAuthRepository, Mockito.times(0)).save(defaultSenderData);
  }

  private SenderData createSenderData() {
    return SenderData.builder()
        .senderCode("senderCode")
        .apiKey("12345").build();
  }
}
