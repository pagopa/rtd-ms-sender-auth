package it.gov.pagopa.rtd.ms.rtdmssenderauth.repository;

import it.gov.pagopa.rtd.ms.rtdmssenderauth.model.SenderData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SenderAuthRepository extends MongoRepository<SenderData, String> {

}
