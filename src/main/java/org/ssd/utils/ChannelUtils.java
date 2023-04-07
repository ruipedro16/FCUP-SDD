package org.ssd.utils;

import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.TlsChannelCredentials;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;
import org.ssd.p2p.NodeContact;

import javax.net.ssl.SSLException;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

public class ChannelUtils {


    private final Provider bcProvider;

    public ChannelUtils(Provider prov) {
        this.bcProvider = prov;
    }

    /**
     * Initialize an unsecure channel to a specific address and port (used for testing)
     *
     * @param target  Address
     * @return ManagedChannel Object to give to Stubs
     */
    public static ManagedChannel initUnsecureChannel(NodeContact target) {
        return ManagedChannelBuilder.forAddress(target.getAddress().getHostAddress(), target.getPort())
                //change this to TLS
                .usePlaintext()
                .build();
    }

    /**
     * Initialize a TLS secured channel for a specific address and port
     * @return Managed Channel with creds
     */
    public static ManagedChannel initSecureChannel(NodeContact target, String cert) throws IOException, CertificateException {
        X509Certificate serverCertificate = readCertificate(cert);
        SslContext ctx = GrpcSslContexts.forClient().trustManager(serverCertificate).build();

        return NettyChannelBuilder.forAddress(target.getAddress().getHostAddress(), target.getPort())
                .overrideAuthority("SSD")
                .sslContext(ctx)
                .build();
    }

    /**
     * Initialize a mutually TLS secured channel for a specific address and port
     *
     * @param trustCertCollectionFile Custom CA root certificates
     * @param clientCertChainFile     Client's Certificate Chain
     * @param clientPrivateKeyFile    Client's Private Key
     * @return Managed Channel with creds
     */
    public static ManagedChannel initMutualSecureChannel(NodeContact target, File trustCertCollectionFile, File clientCertChainFile, File clientPrivateKeyFile) throws IOException {
        //TODO
        TlsChannelCredentials.Builder tlsBuilder = TlsChannelCredentials.newBuilder();
        if (trustCertCollectionFile != null && clientCertChainFile != null && clientPrivateKeyFile != null) {
            tlsBuilder.keyManager(clientCertChainFile, clientPrivateKeyFile);
            tlsBuilder.trustManager(trustCertCollectionFile);
        }

        return Grpc.newChannelBuilderForAddress(target.getAddress().getHostAddress(), target.getPort(), tlsBuilder.build())
                .build();
    }

    /**
     * Generate keyPair
     */
    public KeyPair genKeyPair(int keySize) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", bcProvider);
        KeyPair pair;
        if (keySize == 1024) {
            generator.initialize(new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4));
            pair = generator.generateKeyPair();
        } else if (keySize == 2048) {
            generator.initialize(new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4));
            pair = generator.generateKeyPair();
        } else if (keySize == 3072) {
            generator.initialize(new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4));
            pair = generator.generateKeyPair();
        } else if (keySize == 4096) {
            generator.initialize(new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4));
            pair = generator.generateKeyPair();
        } else {
            return null;
        }
        return pair;
    }

    public Certificate genSelfSignedCertificate(KeyPair keyPair) throws CertificateException, OperatorCreationException {
        X500Name dnName = new X500Name("CN=" + "PubSub");
        BigInteger certSerialNumber = BigInteger.valueOf(System.currentTimeMillis());
        String signatureAlgorithm = "SHA256WithRSA";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.add(Calendar.YEAR, 1);

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair
                .getPublic().getEncoded());

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(
                dnName,
                certSerialNumber,
                new Date(System.currentTimeMillis()),
                calendar.getTime(),
                dnName,
                subjectPublicKeyInfo
        );

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm)
                .setProvider(bcProvider)
                .build(keyPair.getPrivate());

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);
        return new JcaX509CertificateConverter().getCertificate(certificateHolder);
    }

    public void writeCertificate(X509Certificate certificate, String path) throws IOException {
        File toWrite = new File(path, "cert.crt");
        toWrite.getParentFile().mkdirs();
        toWrite.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(toWrite, false));
        JcaPEMWriter jpw = new JcaPEMWriter(out);
        jpw.writeObject(certificate);
        jpw.close();
        out.close();
    }

    public static X509Certificate readCertificate(String path) throws IOException, CertificateException {

        CertificateFactory certFactory = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());
        FileInputStream in = new FileInputStream(path);
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
        in.close();
        return cert;
    }

    public void writePrivateKey(PrivateKey privateKey, String path) throws IOException {
        File toWrite = new File(path, "privKey.key");
        toWrite.getParentFile().mkdirs();
        toWrite.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(toWrite, false));
        JcaPEMWriter jpw = new JcaPEMWriter(out);

        jpw.writeObject(new JcaPKCS8Generator(privateKey, null));
        jpw.close();
        out.close();
    }

    public void writeEncryptedPrivateKey(PrivateKey privateKey, char[] passwd, String path) throws IOException, OperatorCreationException {
        File toWrite = new File(path, "privKey.key");
        toWrite.getParentFile().mkdirs();
        toWrite.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(toWrite, false));
        JcaPEMWriter jpw = new JcaPEMWriter(out);
        //SHA256WithRSA
        jpw.writeObject(new JcaPKCS8Generator(privateKey, new JcePKCSPBEOutputEncryptorBuilder(NISTObjectIdentifiers.id_aes256_CBC)
                .setProvider(bcProvider)
                .build(passwd)));
        jpw.close();
        out.close();
    }

    public PrivateKey readPrivateKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public PrivateKey readEncryptedPrivateKey(String passwd, String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, OperatorCreationException, PKCSException {
        String pem = Files.readString(Paths.get(path));

        PEMParser parser = new PEMParser(new StringReader(pem));
        PKCS8EncryptedPrivateKeyInfo encPrivKeyInfo = (PKCS8EncryptedPrivateKeyInfo)parser.readObject();
        InputDecryptorProvider pkcs8Prov = new JcePKCSPBEInputDecryptorProviderBuilder()
                .setProvider(bcProvider).build(passwd.toCharArray());
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(bcProvider);

        return converter.getPrivateKey(encPrivKeyInfo.decryptPrivateKeyInfo(pkcs8Prov));
    }

    public void writePublicKey(PublicKey publicKey, String path) throws IOException {
        File toWrite = new File(path, "pubKey.key");
        toWrite.getParentFile().mkdirs();
        toWrite.createNewFile();
        FileWriter out = new FileWriter(toWrite, false);
        JcaPEMWriter jpw = new JcaPEMWriter(out);
        jpw.writeObject(publicKey);
        jpw.close();
        out.close();
    }

    public static PublicKey readPublicKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private SslContext loadCerts() throws SSLException {
        File serverCert = new File("./certs/cert.crt");
        File serverPrivKey = new File("./certs/privKey.key");

        SslContextBuilder ctx = SslContextBuilder.forServer(serverCert, serverPrivKey)
                .clientAuth(ClientAuth.NONE);
        return GrpcSslContexts.configure(ctx).build();
    }

    public SslContext loadEncryptedCerts() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, OperatorCreationException, PKCSException {
        PrivateKey privKey = readEncryptedPrivateKey("test","./certs/privKey.key");
        X509Certificate cert = readCertificate("./certs/cert.crt");
        SslContextBuilder ctx = SslContextBuilder.forServer(privKey, "test", cert)
                .clientAuth(ClientAuth.NONE);
        return GrpcSslContexts.configure(ctx).build();
    }

    public SslContext loadServerCert() throws CertificateException, IOException {
        X509Certificate cert = readCertificate("./certs/cert.crt");
        return GrpcSslContexts.forClient()
                .trustManager(cert)
                .build();
    }

    public static void sign(PrivateKey privateKey, String algorithm, String payload) throws Exception {
        Signature signer = Signature.getInstance(algorithm, "BC");
        signer.initSign(privateKey);
        signer.update(payload.getBytes());
        signer.sign();
    }

    public boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                return !directory.iterator().hasNext();
            }
        }
        return false;
    }

}
