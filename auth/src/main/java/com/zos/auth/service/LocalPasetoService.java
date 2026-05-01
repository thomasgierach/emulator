package com.zos.auth.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.SignatureException;
import java.security.interfaces.EdECPrivateKey;


import org.paseto4j.commons.PasetoException;
import org.paseto4j.commons.SecretKey;
import org.paseto4j.commons.Version;
import org.paseto4j.version4.Paseto;
import org.paseto4j.version4.PasetoPublic;
import org.springframework.beans.factory.annotation.Value;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.PEMParser;



public class LocalPasetoService {
    /* 
      "name": "4-E-1",
      "key": "707172737475767778797a7b7c7d7e7f808182838485868788898a8b8c8d8e8f",
      "nonce": "0000000000000000000000000000000000000000000000000000000000000000",
      "token": "v4.local.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAr68PS4AXe7If_ZgesdkUMvSwscFlAl1pk5HC0e8kApeaqMfGo_7OpBnwJOAbY9V7WU6abu74MmcUE8YWAiaArVI8XJ5hOb_4v9RmDkneN0S92dx0OW4pgy7omxgf3S8c3LlQg",
      "payload": "{\"data\":\"this is a secret message\",\"exp\":\"2022-01-01T00:00:00+00:00\"}",
      "footer": "",
      "implicit-assertion": ""
    */
    @Value("${app.token.secret}")
    public String key;
    public String payload;
    public String footer;
    public String implicitAssertion;
    public String signedToken;

    public LocalPasetoService() {}
    public LocalPasetoService(String key, 
                              String payload, 
                              String footer, 
                              String implicitAssertion) {
        this.key = key;
        this.payload = payload;
        this.footer = footer;
        this.implicitAssertion = implicitAssertion;
        
    }
    public void setSignedToken () throws IOException, SignatureException {
        Reader rdr = new StringReader(this.key);
        Object parsed = new PEMParser(rdr).readObject();
        var privateKey = (EdECPrivateKey) new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) parsed);
        System.out.println(privateKey);
        System.out.println(privateKey.getClass().getName());
        /* 
        this.signedToken = PasetoPublic.sign(privateKey, 
                                              this.payload, 
                                              this.footer, 
                                              this.implicitAssertion);
        */
    }
    public String getSignedToken () {
        return this.signedToken;
    }
}
