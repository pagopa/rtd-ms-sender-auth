package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.RecordNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.SenderUnauthorized;
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
  public Set<String> getSenderCodes(String internalId) {
    log.info("Getting SenderCode from internal ID");
    return authService.getSenderCodes(internalId);
  }

  @Override
  public void saveApiKey(String senderCode, String apiKey) {
    log.info("Saving internal ID for SenderCode {}", senderCode);
    authService.saveApiKey(senderCode, apiKey);
  }

  @Override
  public void authorizeSender(String senderCode, String internalId) {
    log.info("Checking authorization for SenderCode {}", senderCode);
    if (!authService.authorize(senderCode, internalId)) {
      throw new SenderUnauthorized(senderCode);
    }
  }

  @Override
  public void deleteApiKey(String senderCode, String internalId) {
    log.info("Deleting senderCode {}" ,senderCode);
    try {
      authService.deleteAssociation(senderCode, internalId);
    } catch (RecordNotFoundException e) {
      log.warn("Failed to delete a non existing association by sendercode {}", senderCode);
    }
  }
}
