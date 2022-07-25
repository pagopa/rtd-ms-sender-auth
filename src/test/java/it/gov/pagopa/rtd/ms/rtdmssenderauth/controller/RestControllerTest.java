package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.controller.DefaultRestController.RecordNotPresent;
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
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(RestControllerImpl.class)
class RestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  SenderAuthService service;

  final ObjectMapper mapper = new ObjectMapper();

  private final String BASE_URI = "http://localhost:8080";

  private final String GETSENDERCODE_ENDPOINT = "/sender-code";
  private final String SAVEAPIKEY_ENDPOINT = "/%s/%s";

  @SneakyThrows
  @Test
  void whenGetSenderCodeReturnsStringThenStatusIsOk() {
    BDDMockito.doReturn("senderCode").when(service).getSenderCode(any());

    mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_URI + GETSENDERCODE_ENDPOINT)
            .param("internalId", "xxx"))
        .andExpectAll(status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON_VALUE),
            content().string("senderCode"));
  }

  @SneakyThrows
  @Test
  void whenGetSenderCodeThrowsRecordNotPresentExceptionThenStatusIsNotFound() {
    BDDMockito.doThrow(RecordNotPresent.class).when(service).getSenderCode(any());

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
  void whenSaveApiKeyItIsAlreadyMappedThenThrowException() {
    BDDMockito.doThrow(ResponseStatusException.class).when(service).getSenderCode(any());

    mockMvc.perform(MockMvcRequestBuilders
            .put(BASE_URI + String.format(SAVEAPIKEY_ENDPOINT, "senderCode", "apiKey")))
        .andExpectAll(status().isOk());
  }
}
