package it.gov.pagopa.rtd.ms.rtdmssenderauth.service;

public interface SenderAuthService {

  String getSenderCode(String apiKey);

  void saveApiKey(String senderCode, String apiKey);
}
