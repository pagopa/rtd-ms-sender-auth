package it.gov.pagopa.rtd.ms.rtdmssenderauth.service;

import java.util.Set;

public interface SenderAuthService {

  Set<String> getSenderCodes(String apiKey);

  void saveApiKey(String senderCode, String apiKey);

  boolean authorize(String senderCode, String apiKey);

  void deleteAssociation(String senderCode, String apiKey);
}
