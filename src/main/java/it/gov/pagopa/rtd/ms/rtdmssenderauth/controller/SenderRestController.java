package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import io.swagger.annotations.Api;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

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
  Set<String> getSenderCodes(@RequestParam(value = "internalId") String apiKey)
      throws NotFoundException;

  @PutMapping(value = "/{senderCode}/{apiKey}")
  void saveApiKey(@PathVariable("senderCode") String senderCode, @PathVariable("apiKey") String apiKey);
}
