package it.pagopa.interop.bff.service.producer_keychain;

import it.pagopa.interop.common.service.producer_keychain.ProducerKeychainService;

public interface IBffProducerKeychainService extends ProducerKeychainService {

//    default void deleteAll() {
//        List<ProducerKeychain> keychains;
//
//        do {
//            keychains = this.readAll(BaseReadAllProducerKeychainRequest.unfiltered())
//                    .withPolling(PollingStrategy.UNTIL_SUCCESS)
//                    .getModels();
//
//            if (keychains == null || keychains.isEmpty()) {
//                return;
//            }
//
//            for (ProducerKeychain keychain : keychains) {
//                this.delete(keychain.getId())
//                        .withPolling(((statusCode, body) -> {
//                            System.out.println("StatusCode: " + statusCode.value());
//                            if(statusCode.value() == HttpStatus.NOT_FOUND.value())
//                                System.out.println("Not found");
//                            return statusCode.is2xxSuccessful() || statusCode.equals(HttpStatus.NOT_FOUND);
//                        }));
//            }
//
//        } while (true);
//    }
}
