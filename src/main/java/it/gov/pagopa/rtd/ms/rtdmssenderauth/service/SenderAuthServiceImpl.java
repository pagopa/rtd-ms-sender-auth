package it.gov.pagopa.rtd.ms.rtdmssenderauth.service;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.exception.InternalIdAlreadyAssociatedException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.exception.RecordNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.model.SenderData;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.repository.SenderAuthRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SenderAuthServiceImpl implements SenderAuthService {

  private final SenderAuthRepository senderAuthRepository;

  @Override
  public String getSenderCode(String apiKey) {
    return senderAuthRepository.findByApiKey(apiKey).orElseThrow(RecordNotFoundException::new)
        .getSenderCode();
  }

  @Override
  public void saveApiKey(String senderCode, String apiKey) {
    Optional<String> senderCodeOpt = senderAuthRepository.findByApiKey(apiKey)
        .map(SenderData::getSenderCode);
    if (senderCodeOpt.isPresent()) {
      if (isApiKeyAssociatedToAnotherSenderCode(senderCode, senderCodeOpt.get())) {
        throw new InternalIdAlreadyAssociatedException();
      } else {
        // senderCode-apiKey is already saved therefore do nothing
        return;
      }
    }

    senderAuthRepository.deleteBySenderCode(senderCode);
    SenderData senderDataToSave = SenderData.builder().senderCode(senderCode).apiKey(apiKey)
        .build();
    senderAuthRepository.save(senderDataToSave);
  }

  private boolean isApiKeyAssociatedToAnotherSenderCode(String senderCodeSaved,
      String senderCodeNew) {
    return !senderCodeSaved.equals(senderCodeNew);
  }
}
