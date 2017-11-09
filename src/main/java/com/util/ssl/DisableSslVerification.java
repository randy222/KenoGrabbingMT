package com.util.ssl;

import java.security.Security;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class DisableSslVerification {
    private static final Logger log = LoggerFactory.getLogger( DisableSslVerification.class );

    static {
        try {
            //System.setProperty( "javax.net.debug", "all" );
            
            if ( null != ClassUtils.findClass( "org.bouncycastle.jce.provider.BouncyCastleProvider" ) ) {
                Security.removeProvider( org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME );
                Security.insertProviderAt( new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1 );
            }
            if ( null != ClassUtils.findClass( "org.bouncycastle.jsse.provider.BouncyCastleJsseProvider" ) ) {
                Security.removeProvider( org.bouncycastle.jsse.provider.BouncyCastleJsseProvider.PROVIDER_NAME );
                Security.insertProviderAt( new org.bouncycastle.jsse.provider.BouncyCastleJsseProvider(), 2 );
            }

            System.setProperty( "https.protocols", "TLSv1,TLSv1.1,TLSv1.2" );
        } catch ( Exception ex ) {
            log.warn( "Bouncy Castle Provider install failed" );
        }
    }
    
    public static void disable() {
        try {
            // Install the all-trusting trust manager
            TrustManager[] trustAllCerts = {new SelfSignedTrustManager( null )};

            SSLContext sc = SSLContext.getDefault();
            sc.init( null, trustAllCerts, new java.security.SecureRandom() );
            HttpsURLConnection.setDefaultSSLSocketFactory( sc.getSocketFactory() );

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier( allHostsValid );
            log.info( "Disable SSL Verification success" );
        } catch ( Exception ex ) {
            log.error( "Disable SSL Verification failed", ex );
        }

    }

}
