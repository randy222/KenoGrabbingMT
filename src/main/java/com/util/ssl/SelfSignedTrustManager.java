/**
 * TrustSelfSignedXX.java
 *
 * Copyright (c) by you
 *
 */
package com.util.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * 
 *
 * @date Jun 12, 2015
 * @author you
 *
 * @version history
 * <pre>
 * version: 1.0.0
 * date：Jun 12, 2015
 * author : you
 * remark: initialized version
 *</pre>
 */
public class SelfSignedTrustManager implements X509TrustManager {
    private final X509TrustManager trustManager;

    public SelfSignedTrustManager(final X509TrustManager trustManager) {
        super();
        this.trustManager = trustManager;
    }

    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        //no op
    }

    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        //no op        
    }

    public X509Certificate[] getAcceptedIssuers() {
        return trustManager != null ? trustManager.getAcceptedIssuers() : new X509Certificate[0];
    }
}
