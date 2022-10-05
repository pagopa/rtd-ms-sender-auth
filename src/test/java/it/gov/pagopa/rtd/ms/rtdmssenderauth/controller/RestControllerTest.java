package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.RecordNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.SenderCodeAssociatedToAnotherApiKey;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.service.SenderAuthService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SenderRestControllerImpl.class)
class RestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  SenderAuthService service;

  private final String BASE_URI = "http://localhost:8080";

  private static final String GETSENDERCODE_ENDPOINT = "/sender-code";
  private static final String SAVEAPIKEY_ENDPOINT = "/%s/%s";
  private static final String DELETE_ASSOCIATION_ENDPOINT = "/%s";
  private static final String AUTHORIZE_ENDPOINT = "/authorize/%s";
  private static final String HEADER_INTERNAL_ID = "internal-id";

  @SneakyThrows
  @Test
  void whenGetSenderCodeReturnsStringThenStatusIsOk() {
    BDDMockito.doReturn(Set.of("senderCode1", "senderCode2")).when(service).getSenderCodes(any());

    final var expectedResponseContent = String.join(",", Set.of("senderCode1", "senderCode2"));

    mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_URI + GETSENDERCODE_ENDPOINT)
            .param("internalId", "xxx"))
        .andExpectAll(status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON_VALUE),
            content().json(String.format("[%s]", expectedResponseContent)));
  }

  @SneakyThrows
  @Test
  void whenGetSenderCodeThrowsRecordNotFoundExceptionThenStatusIsNotFound() {
    BDDMockito.doThrow(RecordNotFoundException.class).when(service).getSenderCodes(any());

    mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_URI + GETSENDERCODE_ENDPOINT)
            .param("internalId", "xxx"))
        .andExpectAll(status().isNotFound());
  }

  @SneakyThrows
  @Test
  void whenSaveApiKeyReturns200() {
    mockMvc.perform(MockMvcRequestBuilders
            .put(BASE_URI + String.format(SAVEAPIKEY_ENDPOINT, "senderCode", "apiKey")))
        .andExpectAll(status().isOk());
  }

  @SneakyThrows
  @Test
  void whenAssociateSenderCodeToDifferentApiKeyThenBadRequest() {
    BDDMockito.doThrow(SenderCodeAssociatedToAnotherApiKey.class).when(service).saveApiKey(any(), any());

    mockMvc.perform(MockMvcRequestBuilders
            .put(BASE_URI + String.format(SAVEAPIKEY_ENDPOINT, "senderCode", "apiKey")))
        .andExpectAll(status().isBadRequest());
  }

  @SneakyThrows
  @Test
  void whenDeleteAnAssociationThenReturnsOk() {
    BDDMockito.doNothing().when(service).deleteAssociation(any(), any());
    mockMvc.perform(
            MockMvcRequestBuilders.delete(BASE_URI + String.format(DELETE_ASSOCIATION_ENDPOINT, "senderCode"))
                    .header(HEADER_INTERNAL_ID, "apiKey")
    ).andExpectAll(status().isOk());
  }

  @SneakyThrows
  @Test
  void whenDeleteAssociationForNonExistingApiKeyThenReturnsOk() { // verify indepotency
    BDDMockito.doThrow(RecordNotFoundException.class).when(service).deleteAssociation(any(), any());
    mockMvc.perform(
            MockMvcRequestBuilders.delete(BASE_URI + String.format(DELETE_ASSOCIATION_ENDPOINT, "senderCode"))
                    .header(HEADER_INTERNAL_ID, "apiKey")
    ).andExpectAll(status().isOk());
  }

  @SneakyThrows
  @Test
  void whenInternalIdIsMissingThenDeleteReturnBadRequest() {
    BDDMockito.doNothing().when(service).deleteAssociation(any(), any());
    mockMvc.perform(
            MockMvcRequestBuilders.delete(BASE_URI + String.format(DELETE_ASSOCIATION_ENDPOINT, "senderCode"))
    ).andExpectAll(status().isBadRequest());
  }

  @SneakyThrows
  @Test
  void whenDeleteThrowUnhandledExceptionThenReturnInternalError() {
    BDDMockito.doThrow(IllegalArgumentException.class).when(service).deleteAssociation(any(), any());
    mockMvc.perform(
            MockMvcRequestBuilders.delete(BASE_URI + String.format(DELETE_ASSOCIATION_ENDPOINT, "senderCode"))
                    .header(HEADER_INTERNAL_ID, "apiKey")
    ).andExpectAll(status().isInternalServerError());
  }

  @SneakyThrows
  @Test
  void whenSenderCodeIsAssociatedToGivenApiKeyThenReturnsOk() {
    BDDMockito.doReturn(true).when(service).authorize("senderCode", "apiKey");
    mockMvc.perform(MockMvcRequestBuilders
                    .get(BASE_URI + String.format(AUTHORIZE_ENDPOINT, "senderCode"))
                    .header(HEADER_INTERNAL_ID, "apiKey"))
            .andExpectAll(status().isOk());
  }

  @SneakyThrows
  @Test
  void whenSenderCodeIsNotAssociatedToGivenApiKeyThenReturnsUnauthorized() {
    BDDMockito.doReturn(false).when(service).authorize("senderCode", "apiKey");
    mockMvc.perform(MockMvcRequestBuilders
                    .get(BASE_URI + String.format(AUTHORIZE_ENDPOINT, "senderCode"))
                    .header(HEADER_INTERNAL_ID, "apiKey"))
            .andExpectAll(status().isUnauthorized());
  }

  @SneakyThrows
  @Test
  void whenInternalIdIsMissingThenAuthorizeReturnBadRequest() {
    BDDMockito.doNothing().when(service).deleteAssociation(any(), any());
    mockMvc.perform(
            MockMvcRequestBuilders.get(BASE_URI + String.format(AUTHORIZE_ENDPOINT, "senderCode"))
    ).andExpectAll(status().isBadRequest());
  }
}
