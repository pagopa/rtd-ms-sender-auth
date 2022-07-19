package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@ResponseBody
@Slf4j
public class RestControllerImpl implements DefaultRestController {

  @Override
  public void healthCheck() {
    //Return OK if the service is reachable
  }
}
