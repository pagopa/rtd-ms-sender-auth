package it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception;

public class SenderUnauthorized extends RuntimeException {
  public final String senderCode;

  public SenderUnauthorized(String senderCode) {
    this.senderCode = senderCode;
  }
}
