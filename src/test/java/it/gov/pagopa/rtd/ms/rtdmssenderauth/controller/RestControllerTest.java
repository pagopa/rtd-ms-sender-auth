package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.controller.SenderRestController.RecordNotPresent;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.controller.SenderRestController.SenderCodeAssociatedToAnotherApiKey;
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

  final ObjectMapper mapper = new ObjectMapper();

  private final String BASE_URI = "http://localhost:8080";

  private static final String GETSENDERCODE_ENDPOINT = "/sender-code";
  private static final String SAVEAPIKEY_ENDPOINT = "/%s/%s";

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
  void whenGetSenderCodeThrowsRecordNotPresentExceptionThenStatusIsNotFound() {
    BDDMockito.doThrow(RecordNotPresent.class).when(service).getSenderCodes(any());

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
}
