package io.quarkus.tls.cli.acme;

import static java.lang.System.Logger.Level.INFO;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.wildfly.security.x500.cert.X509CertificateChainAndSigningKey;
import org.wildfly.security.x500.cert.acme.AcmeAccount;
import org.wildfly.security.x500.cert.acme.AcmeException;

import io.quarkus.tls.cli.helpers.LetsEncryptHelpers;
import io.vertx.core.json.JsonObject;

public final class AcmeActions {
    static System.Logger LOGGER = System.getLogger("lets-encrypt-actions");

    public static void prepare(String letsEncryptFolder) {
        try {
            LOGGER.log(INFO, "Creating a working directory");
            Files.createDirectories(Paths.get(letsEncryptFolder));
        } catch (IOException ex) {
            LOGGER.log(INFO, "Failed to create a working directory");
            throw new RuntimeException(ex);
        }
    }

    public static String createAccount(AcmeClient acmeClient,
            String letsEncryptPath,
            boolean staging,
            String contactEmail) {
        prepare(letsEncryptPath);

        LOGGER.log(INFO, "Creating {0} Let's Encrypt account", (staging ? "staging" : ""));

        AcmeAccount acmeAccount = AcmeAccount.builder().setTermsOfServiceAgreed(true)
                .setServerUrl("https://acme-v02.api.letsencrypt.org/directory")
                .setStagingServerUrl("https://acme-staging-v02.api.letsencrypt.org/directory")
                .setContactUrls(new String[] { "mailto:" + contactEmail }).build();
        try {
            if (!acmeClient.createAccount(acmeAccount, staging)) {
                LOGGER.log(INFO, "{0} Let's Encrypt account {1} already exists", (staging ? "Staging" : ""), contactEmail);
            } else {
                LOGGER.log(INFO, "{0} Let's Encrypt account {1} has been created", (staging ? "Staging" : ""), contactEmail);
            }
        } catch (AcmeException ex) {
            LOGGER.log(INFO, "Failed to create account");
            throw new RuntimeException(ex);
        }
        JsonObject accountJson = convertAccountToJson(acmeAccount);
        saveAccount(letsEncryptPath, accountJson);
        return accountJson.encode();
    }

    public static String firstCertificate(AcmeClient acmeClient,
            String letsEncryptPath,
            boolean staging,
            String domain,
            String certChainPemLoc,
            String privateKeyPemLoc) {
        LOGGER.log(INFO, "Requesting first {0} Let's Encrypt certificate chain and private key", (staging ? "staging" : ""));
        JsonObject chainAndKey = obtainCertificateChain(acmeClient, letsEncryptPath, staging, domain,
                certChainPemLoc, privateKeyPemLoc);
        return chainAndKey.encode();
    }

    public static String renewCertificate(AcmeClient acmeClient,
            String letsEncryptPath,
            boolean staging,
            String domain,
            String certChainPemLoc,
            String privateKeyPemLoc) {
        LOGGER.log(INFO, "Renewing {0} Let's Encrypt certificate chain and private key", (staging ? "staging" : ""));
        JsonObject chainAndKey = obtainCertificateChain(acmeClient, letsEncryptPath, staging, domain,
                certChainPemLoc, privateKeyPemLoc);
        return chainAndKey.encode();
    }

    public static void deactivateAccount(AcmeClient acmeClient, String letsEncryptPath, boolean staging) throws IOException {
        AcmeAccount acmeAccount = getAccount(letsEncryptPath);
        LOGGER.log(INFO, "Deactivating {0} Let's Encrypt account", (staging ? "staging" : ""));
        acmeClient.deactivateAccount(acmeAccount, staging);

        LOGGER.log(INFO, "Removing account file from {0}", letsEncryptPath);

        java.nio.file.Path accountPath = Paths.get(letsEncryptPath + "/account.json");
        Files.deleteIfExists(accountPath);
    }

    public static JsonObject obtainCertificateChain(
            AcmeClient acmeClient,
            String letsEncryptPath,
            boolean staging,
            String domain,
            String certChainPemLoc,
            String privateKeyPemLoc) {

        acmeClient.checkReadiness();

        AcmeAccount acmeAccount = getAccount(letsEncryptPath);

        X509CertificateChainAndSigningKey certChainAndPrivateKey;
        try {
            certChainAndPrivateKey = acmeClient.obtainCertificateChain(acmeAccount, staging, domain);
        } catch (AcmeException t) {
            throw new RuntimeException(t.getMessage());
        }
        LOGGER.log(INFO, "Converting certificate chain and private key to PEM");

        java.nio.file.Path certPemPath = Paths.get(certChainPemLoc);
        java.nio.file.Path keyPemPath = Paths.get(privateKeyPemLoc);
        try {
            LetsEncryptHelpers.writePrivateKeyAndCertificateChainsAsPem(certChainAndPrivateKey.getSigningKey(),
                    certChainAndPrivateKey.getCertificateChain(), keyPemPath.toFile(), certPemPath.toFile());
        } catch (Exception ex) {
            throw new RuntimeException("Failure to copy certificate pem");
        }

        acmeClient.certificateChainAndKeyAreReady();

        return new JsonObject().put("account", convertAccountToJson(acmeAccount));
    }

    private static JsonObject convertAccountToJson(AcmeAccount acmeAccount) {
        JsonObject json = new JsonObject();
        json.put("account-url", acmeAccount.getAccountUrl());
        json.put("contact-url", acmeAccount.getContactUrls()[0]);
        if (acmeAccount.getPrivateKey() != null) {
            json.put("private-key", new String(Base64.getEncoder().encode(acmeAccount.getPrivateKey().getEncoded()),
                    StandardCharsets.US_ASCII));
        }
        if (acmeAccount.getCertificate() != null) {
            try {
                json.put("certificate", new String(Base64.getEncoder().encode(acmeAccount.getCertificate().getEncoded()),
                        StandardCharsets.US_ASCII));
            } catch (CertificateEncodingException ex) {
                LOGGER.log(INFO, "Failed to get encoded certificate data");
                throw new RuntimeException(ex);
            }
        }
        if (acmeAccount.getKeyAlgorithmName() != null) {
            json.put("key-algorithm", acmeAccount.getKeyAlgorithmName());
        }
        json.put("key-size", acmeAccount.getKeySize());
        return json;
    }

    private static AcmeAccount getAccount(String letsEncryptPath) {
        LOGGER.log(INFO, "Getting account from {0}", letsEncryptPath);

        JsonObject json = readAccountJson(letsEncryptPath);
        AcmeAccount.Builder builder = AcmeAccount.builder().setTermsOfServiceAgreed(true)
                .setServerUrl("https://acme-v02.api.letsencrypt.org/directory")
                .setStagingServerUrl("https://acme-staging-v02.api.letsencrypt.org/directory");

        String keyAlgorithm = json.getString("key-algorithm");
        builder.setKeyAlgorithmName(keyAlgorithm);
        builder.setKeySize(json.getInteger("key-size"));

        if (json.containsKey("private-key") && json.containsKey("certificate")) {
            PrivateKey privateKey = getPrivateKey(json.getString("private-key"), keyAlgorithm);
            X509Certificate certificate = getCertificate(json.getString("certificate"));

            builder.setKey(certificate, privateKey);
        }

        AcmeAccount acmeAccount = builder.build();

        acmeAccount.setContactUrls(new String[] { json.getString("contact-url") });
        acmeAccount.setAccountUrl(json.getString("account-url"));

        return acmeAccount;
    }

    private static JsonObject readAccountJson(String letsEncryptPath) {
        LOGGER.log(INFO, "Reading account information from {0}", letsEncryptPath);
        java.nio.file.Path accountPath = Paths.get(letsEncryptPath + "/account.json");
        try (FileInputStream fis = new FileInputStream(accountPath.toString())) {
            return new JsonObject(new String(fis.readAllBytes(), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read the account file, you must create account first");
        }
    }

    private static void saveAccount(String letsEncryptPath, JsonObject accountJson) {
        LOGGER.log(INFO, "Saving account to {0}", letsEncryptPath);

        // If more than one account must be supported, we can save accounts to unique files in .lets-encrypt/accounts
        // and require an account alias/id during operations requiring an account
        java.nio.file.Path accountPath = Paths.get(letsEncryptPath + "/account.json");
        try {
            Files.copy(new ByteArrayInputStream(accountJson.encode().getBytes(StandardCharsets.US_ASCII)), accountPath,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Failure to save the account", ex);
        }
    }

    private static X509Certificate getCertificate(String encodedCert) {
        try {
            byte[] encodedBytes = Base64.getDecoder().decode(encodedCert);
            return (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(encodedBytes));
        } catch (Exception ex) {
            throw new RuntimeException("Failure to create a certificate", ex);
        }
    }

    private static PrivateKey getPrivateKey(String encodedKey, String keyAlgorithm) {
        try {
            KeyFactory f = KeyFactory.getInstance((keyAlgorithm == null || "RSA".equals(keyAlgorithm) ? "RSA" : "EC"));
            byte[] encodedBytes = Base64.getDecoder().decode(encodedKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedBytes);
            return f.generatePrivate(spec);
        } catch (Exception ex) {
            throw new RuntimeException("Failure to create a private key", ex);
        }
    }
}