package it.gov.pagopa.rtd.ms.rtdmssenderauth.service;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.repository.SenderAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SenderAuthServiceImpl implements SenderAuthService {

  private final SenderAuthRepository senderAuthRepository;

}
