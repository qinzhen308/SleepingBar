package com.core.framework.auth;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLException;

import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;

import com.core.framework.app.MyApplication;
import com.core.framework.develop.LogUtil;


public class SSLCustomSocketFactory extends SSLSocketFactory {


    private static final String KEY_PASS = "pw12306";

    public SSLCustomSocketFactory(KeyStore trustStore) throws Exception {
        super(trustStore);
    }

    public static SSLSocketFactory getSocketFactoryByBKS(String key,String keyStringType) {
        SSLSocketFactory factory = SSLSocketFactory.getSocketFactory();
        try {
            InputStream ins = MyApplication.getInstance().getAssets().open(key);
//          String keyStringType=KeyStore.getDefaultType();
            LogUtil.d("keyStringType "+keyStringType);

            KeyStore trustStore = KeyStore.getInstance(keyStringType);

            try {
                trustStore.load(ins, KEY_PASS.toCharArray());
            } finally {
                ins.close();
            }
            factory = new SSLCustomSocketFactory(trustStore);

//          factory.setHostnameVerifier(new AllowAllHostnameVerifier());//放过域名检测
            factory.setHostnameVerifier(new AbstractVerifier() {
                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                    LogUtil.d("host: " + host.toString());
                    for (String key : cns) {
                        LogUtil.d("cns " + key.toString());
                    }
                    for (String key : subjectAlts) {
                        LogUtil.d("subjectAlts " + key.toString());
                    }
                }
            });
            return factory;
        } catch (Exception e) {
//            e.printStackTrace();
            LogUtil.d(e, "SSLSocketFactory error");
        }
        return factory;
    }

    public static SocketFactory getSocketFactoryByCer(String key) {

        SSLSocketFactory factory = SSLSocketFactory.getSocketFactory();
        try {
            InputStream ins = MyApplication.getInstance().getAssets().open(key);
            CertificateFactory cerFactory = CertificateFactory
                    .getInstance("X.509");
            Certificate cer = cerFactory.generateCertificate(ins);
//            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
//            keyStore.setCertificateEntry("trust", cer);
            keyStore.setCertificateEntry("ca", cer);
            factory = new SSLSocketFactory(keyStore);

//            factory.setHostnameVerifier(new AllowAllHostnameVerifier());//放过域名检测
            factory.setHostnameVerifier(new AbstractVerifier() {
                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                    LogUtil.d("host: " + host.toString());
                    for (String key : cns) {
                        LogUtil.d("cns " + key.toString());
                    }
                    for (String key : subjectAlts) {
                        LogUtil.d("subjectAlts " + key.toString());
                    }
                }
            });

            return factory;
        } catch (Exception e) {
//            e.printStackTrace();
            LogUtil.d(e, "SSLSocketFactory error");
        }
        return factory;
    }

    public static SocketFactory getSocketFactoryDef() {
        return  SSLSocketFactory.getSocketFactory();
    }
}