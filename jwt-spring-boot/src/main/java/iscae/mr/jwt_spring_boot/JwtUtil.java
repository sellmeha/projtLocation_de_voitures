package iscae.mr.jwt_spring_boot;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.*;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import jakarta.annotation.PostConstruct;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.List;

import iscae.mr.jwt_spring_boot.dao.entities.Users;
import org.springframework.stereotype.Component;
import iscae.mr.jwt_spring_boot.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class JwtUtil {

    private final JwtConfig jwtConfig;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @Autowired
    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @PostConstruct
    public void init() {
        try {
            byte[] privateKeyBytes = Files.readAllBytes(Paths.get(jwtConfig.getPrivateKeyPath()));
            privateKey = loadPrivateKey(privateKeyBytes, jwtConfig.getPassphrase().toCharArray());
            publicKey = loadPublicKey(jwtConfig.getPublicKeyPath());
            System.out.println("Keys loaded successfully.");
        } catch (Exception e) {
            System.err.println("Error loading keys: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private PrivateKey loadPrivateKey(byte[] keyBytes, char[] passphrase) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        try (PEMParser pemParser = new PEMParser(new StringReader(new String(keyBytes)))) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (object instanceof PEMEncryptedKeyPair) {
                PEMEncryptedKeyPair encryptedKeyPair = (PEMEncryptedKeyPair) object;
                PEMDecryptorProvider decryptorProvider =
                        new JcePEMDecryptorProviderBuilder().build(passphrase);
                PEMKeyPair decryptedKeyPair = encryptedKeyPair.decryptKeyPair(decryptorProvider);
                return converter.getPrivateKey(decryptedKeyPair.getPrivateKeyInfo());
            } else if (object instanceof PEMKeyPair) {
                PEMKeyPair keyPair = (PEMKeyPair) object;
                return converter.getPrivateKey(keyPair.getPrivateKeyInfo());
            } else if (object instanceof PrivateKeyInfo) {

                return converter.getPrivateKey((PrivateKeyInfo) object);
            } else {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePrivate(keySpec);
            }
        }
    }

    private PublicKey loadPublicKey(String publicKeyPath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(publicKeyPath));

        String publicKeyPEM = new String(keyBytes)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");


        byte[] decodedKey = java.util.Base64.getDecoder().decode(publicKeyPEM);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }





    public String generateAccessToken(Users user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 3))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
    public String generateRefreshToken(Users user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }



    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {

            return isTokenValid(token);
        } catch (Exception e) {
            return false;
        }
    }




    public String extractUsername(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }


    public List<String> getRoleFromToken(String token) {
        return (List<String>) Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles");
    }
}
