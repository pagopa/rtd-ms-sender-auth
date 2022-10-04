package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import io.swagger.annotations.Api;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Controller to expose ReST interface to store and retrieve sender related information
 */
@Api(tags = "ReST Controller")
@RequestMapping("")
@Validated
public interface SenderRestController {

  @GetMapping(value = "/sender-code", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  Set<String> getSenderCodes(@RequestParam(value = "internalId") String internalId)
      throws NotFoundException;

  @PutMapping(value = "/{senderCode}/{apiKey}")
  void saveApiKey(@PathVariable("senderCode") String senderCode, @PathVariable("apiKey") String apiKey);

  @GetMapping("/authorize/{senderCode}")
  void authorizeSender(@PathVariable("senderCode") String senderCode, @RequestHeader("internal-id") String internalId);

  @DeleteMapping(value = "/{senderCode}")
  void deleteApiKey(@PathVariable("senderCode") String senderCode, @RequestHeader("internal-id") String internalId);
}
