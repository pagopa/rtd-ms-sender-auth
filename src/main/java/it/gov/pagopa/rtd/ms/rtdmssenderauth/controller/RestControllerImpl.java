package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.service.SenderAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@ResponseBody
@Slf4j
@RequiredArgsConstructor
public class RestControllerImpl implements DefaultRestController {

  private final SenderAuthService authService;

  @Override
  public String getSenderCode(String apiKey) {
    log.info("Getting SenderCode from apikey={}", apiKey);
    return authService.getSenderCode(apiKey);
  }

  @Override
  public void saveApiKey(String senderCode, String apiKey) {
    log.info("Saving senderCode:{} and apiKey:{}", senderCode, apiKey);
    authService.saveApiKey(senderCode, apiKey);
  }
}
