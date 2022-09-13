package it.gov.pagopa.rtd.ms.rtdmssenderauth.service;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.controller.SenderRestController.RecordNotPresent;
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
                .orElseThrow(RecordNotPresent::new)
                .getSenderCodes();
    }

    @Override
    public void saveApiKey(String senderCode, String apiKey) {
        SenderData senderCodeOpt = senderAuthRepository.findByApiKey(apiKey)
                .orElse(SenderData.builder().senderCodes(new HashSet<>()).apiKey(apiKey).build());

        if (senderCodeOpt.addSenderAssociation(senderCode)) {
            senderAuthRepository.save(senderCodeOpt);
        }
    }
}
