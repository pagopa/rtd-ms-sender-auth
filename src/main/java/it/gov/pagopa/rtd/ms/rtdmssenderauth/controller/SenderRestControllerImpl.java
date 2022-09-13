package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.service.SenderAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;


@RestController
@ResponseBody
@Slf4j
@RequiredArgsConstructor
public class SenderRestControllerImpl implements SenderRestController {

  private final SenderAuthService authService;

  @Override
  public Set<String> getSenderCodes(String apiKey) {
    log.info("Getting SenderCode from internal ID");
    return authService.getSenderCodes(apiKey);
  }

  @Override
  public void saveApiKey(String senderCode, String apiKey) {
    log.info("Saving internal ID for SenderCode {}", senderCode);
    authService.saveApiKey(senderCode, apiKey);
  }
}
