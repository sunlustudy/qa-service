package io.choerodon.infra.utils;

import io.choerodon.infra.exception.CommonException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.stream.Collectors;


public class JwtTokenUtil {

    private static final String AUTHORITIES_KEY = "auth";
    private static InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("root.jks");
    private static PrivateKey privateKey = null;
    private static PublicKey publicKey = null;

    static {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, "top-sales".toCharArray());
            privateKey = (PrivateKey) keyStore.getKey("jwt", "top-sales".toCharArray());
            publicKey = keyStore.getCertificate("jwt").getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String generateToken(String subject, int expirationSeconds) {
        return Jwts.builder()
                .setClaims(null)
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }


    public static String parseToken(String token) {
        String subject;
        Claims claims = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token).getBody();
        subject = claims.getSubject();
        return subject;
    }

    public static Date getDate(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token).getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            throw new CommonException("错误的签名");
        }

    }
}
