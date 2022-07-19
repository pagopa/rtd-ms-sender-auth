package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(RestControllerImpl.class)
@Slf4j
class RestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @SpyBean
  RestControllerImpl restController;

  final ObjectMapper mapper = new ObjectMapper();

  static String BASE_URI = "http://localhost:8080";

  static String HEALTHCHECK_ENDPOINT = "/";

  @Test
  void shouldHealthcheck() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_URI + HEALTHCHECK_ENDPOINT)
            .accept(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk());
  }

}
