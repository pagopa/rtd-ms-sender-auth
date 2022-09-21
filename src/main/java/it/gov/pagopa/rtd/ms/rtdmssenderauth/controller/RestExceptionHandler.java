package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.RecordNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.SenderCodeAssociatedToAnotherApiKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(RecordNotFoundException.class)
  protected ResponseEntity<String> handleEntityNotFound(RecordNotFoundException ex) {
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(SenderCodeAssociatedToAnotherApiKey.class)
  protected ResponseEntity<String> handleSenderCodeAssociatedToAnotherApiKey(
          SenderCodeAssociatedToAnotherApiKey ex) {
    return ResponseEntity.badRequest().body(String.format("sender code %s is already associated to another api key", ex.senderCode));
  }
}
