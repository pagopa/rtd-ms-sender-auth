package it.gov.pagopa.rtd.ms.rtdmssenderauth.service;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.RecordNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.domain.exception.SenderCodeAssociatedToAnotherApiKey;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.model.SenderData;
import it.gov.pagopa.rtd.ms.rtdmssenderauth.repository.SenderAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SenderAuthServiceImpl implements SenderAuthService {

  private final SenderAuthRepository senderAuthRepository;

    @Override
    public Set<String> getSenderCodes(String apiKey) {
        return senderAuthRepository.findByApiKey(apiKey)
                .orElseThrow(RecordNotFoundException::new)
                .getSenderCodes();
    }

    @Override
    public void saveApiKey(String senderCode, String apiKey) {
        final var isSenderCodeUsedByAnotherApiKey = senderAuthRepository.findBySenderCode(senderCode)
                .stream()
                .anyMatch(it -> !it.getApiKey().equals(apiKey));

        if (isSenderCodeUsedByAnotherApiKey) {
            throw new SenderCodeAssociatedToAnotherApiKey(senderCode);
        } else {
            SenderData senderCodeOpt = senderAuthRepository.findByApiKey(apiKey)
                    .orElse(SenderData.builder().senderCodes(new HashSet<>()).apiKey(apiKey).build());

            if (senderCodeOpt.addSenderAssociation(senderCode)) {
                senderAuthRepository.save(senderCodeOpt);
            }
        }
    }

  @Override
  public void deleteAssociation(String senderCode, String apiKey) {
    final var apiKeyAssociationsOrEmpty = senderAuthRepository.findByApiKey(apiKey);
    if (apiKeyAssociationsOrEmpty.isPresent()) {
      final var apiKeyAssociations = apiKeyAssociationsOrEmpty.get();
      apiKeyAssociations.removeSenderAssociation(senderCode);
      // when all association are removed delete it
      if (apiKeyAssociations.hasNoAssociations()) {
        senderAuthRepository.deleteByApiKey(apiKey);
      } else {
        senderAuthRepository.save(apiKeyAssociations);
      }
    }
  }
}
