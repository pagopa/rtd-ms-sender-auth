package it.gov.pagopa.rtd.ms.rtdmssenderauth.controller;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.RecordNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.SenderCodeAssociatedToAnotherApiKey;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.SenderUnauthorized;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(RecordNotFoundException.class)
  protected ResponseEntity<String> handleEntityNotFound(RecordNotFoundException ex) {
    return ResponseEntity.status(401).build();
  }

  @ExceptionHandler(SenderCodeAssociatedToAnotherApiKey.class)
  protected ResponseEntity<String> handleSenderCodeAssociatedToAnotherApiKey(
          SenderCodeAssociatedToAnotherApiKey ex) {
    return ResponseEntity.badRequest().body(String.format("sender code %s is already associated to another api key", ex.senderCode));
  }

  @ExceptionHandler(SenderUnauthorized.class)
  protected ResponseEntity<String> handleSenderUnauthorized(SenderUnauthorized ex) {
    logger.warn("Unauthorized sender to given apikey: " + ex.senderCode);
    return ResponseEntity.status(401).build();
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<?> handleUnhandledException(
          Exception unhandledException
  ) {
    logger.warn("Detected unhandled exception " + unhandledException.getMessage());
    return ResponseEntity.internalServerError().build();
  }
}
