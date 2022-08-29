package it.gov.pagopa.rtd.ms.rtdmssenderauth.repository;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.model.SenderData;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SenderAuthRepository extends MongoRepository<SenderData, String> {

  Optional<SenderData> findByApiKey(String apiKey);

  Optional<SenderData> findBySenderCode(String senderCode);

  void deleteBySenderCode(String senderCode);
}
