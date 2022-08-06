package com.matejbizjak.smartvillages.solariot;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Optional;

public class SslUtil {

    private final String keyStorePath;
    private final String keyStorePassword;
    private final String trustStorePath;
    private final String trustStorePassword;

    public SslUtil(String keyStorePath, String keyStorePassword, String trustStorePath, String trustStorePassword) {
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.trustStorePath = trustStorePath;
        this.trustStorePassword = trustStorePassword;
    }

    public SSLSocketFactory createSocketFactory() throws Exception {
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(createKeyManagers(), createTrustManagers(), new SecureRandom());
        return ctx.getSocketFactory();
    }

    private KeyManager[] createKeyManagers() throws Exception {
        KeyStore store = loadStore(keyStorePath, keyStorePassword);
        KeyManagerFactory factory = KeyManagerFactory.getInstance("SunX509");
        factory.init(store, Optional.ofNullable(keyStorePassword).map(String::toCharArray).orElse(new char[0]));
        return factory.getKeyManagers();
    }

    private TrustManager[] createTrustManagers() throws Exception {
        KeyStore store = loadStore(trustStorePath, trustStorePassword);
        TrustManagerFactory factory = TrustManagerFactory.getInstance("SunX509");
        factory.init(store);
        return factory.getTrustManagers();
    }

    private KeyStore loadStore(String path, String password) throws Exception {
        KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
        if (path != null && !path.isEmpty()) {
            BufferedInputStream in = null;
            try {
                // full path
                in = new BufferedInputStream(Files.newInputStream(Paths.get(path)));
            } catch (IOException e) {
                // resources
                URL resource = getClass().getClassLoader().getResource(path);
                if (resource != null) {
                    in = new BufferedInputStream(resource.openStream());
                }
            }
            store.load(in, Optional.ofNullable(password).map(String::toCharArray).orElse(new char[0]));
        } else {
            store.load(null);
        }
        return store;
    }
}