package it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception;

public class SenderCodeAssociatedToAnotherApiKey extends RuntimeException {
    public final String senderCode;

    public SenderCodeAssociatedToAnotherApiKey(String senderCode) {
        this.senderCode = senderCode;
    }
}
