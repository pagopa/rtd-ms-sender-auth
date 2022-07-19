package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller to expose ReST interface
 */
@Api(tags = "ReST Controller")
@RequestMapping("")
@Validated
public interface DefaultRestController {

  @GetMapping(value = "/")
  @ResponseStatus(HttpStatus.OK)
  void healthCheck();

}
