package io.nixer.nixerplugin.core.fingerprint.store_draft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FingerprintStore {

    // attacker's perspective

    Map<String, Long> _24hFingerprintRequests;

    // <fingerprint, List<records>>
    Map<String, List<FingerprintRecord>> store = new HashMap<>();


  

    public void save(String fingerprint, String requestUUID, String url, String IP) {
        new FingerprintRecord(requestUUID, url, IP);
    }

    public List<FingerprintRecord> getRecordsForFingerprint(String fingerprint) {
        return store.get(fingerprint);
    }

    // SQL
    public List<String> detectSuspiciousFingerprints() {
        List<String> suspiciousFingerprints = new ArrayList<>();
        for (List<FingerprintRecord> records : store.values()) {
            String ip = null;
            for (FingerprintRecord fr : records) {
                if (ip == null) {
                    ip = fr.IP;
                } else if (ip != fr.IP) {
//                    suspiciousFingerprints.add()
                    break;
                }

            }
        }

        return suspiciousFingerprints;
    }
    
    static class FingerprintRecord {
        String requestUUID;
        String url;
        String IP;

        public FingerprintRecord(final String requestUUID, final String url, final String IP) {
            this.requestUUID = requestUUID;
            this.url = url;
            this.IP = IP;
        }
    }
}
