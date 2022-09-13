package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.exception.InternalIdAlreadyAssociatedException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.exception.RecordNotFoundException;
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

  @ExceptionHandler(InternalIdAlreadyAssociatedException.class)
  protected ResponseEntity<String> handleInternalIdAlreadyAssociated(
      InternalIdAlreadyAssociatedException ex) {
    return ResponseEntity.badRequest().body("internal id already associated");
  }
}
