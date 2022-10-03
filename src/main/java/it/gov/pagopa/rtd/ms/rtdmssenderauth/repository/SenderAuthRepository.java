package it.gov.pagopa.rtd.ms.rtdmssenderauth.repository;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.model.SenderData;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SenderAuthRepository extends MongoRepository<SenderData, String> {

  Optional<SenderData> findByApiKey(String apiKey);

  @Query(value = "{ 'senderCodes': ?0 }")
  List<SenderData> findBySenderCode(String senderCode);


  Optional<SenderData> deleteByApiKey(String apiKey);

}
