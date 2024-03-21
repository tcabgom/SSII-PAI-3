

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;

public class CustomTrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        // No necesitamos implementar esto para la comunicación cliente-servidor
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null); // Usar el truststore predeterminado del sistema
            X509TrustManager defaultTrustManager = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    defaultTrustManager = (X509TrustManager) tm;
                    break;
                }
            }
            if (defaultTrustManager != null) {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } else {
                throw new CertificateException("No se encontró un X509TrustManager predeterminado");
            }
        } catch (Exception e) {
            throw new CertificateException("Error al verificar el certificado del servidor", e);
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

