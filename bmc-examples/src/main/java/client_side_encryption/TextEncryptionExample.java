/**
 * Copyright (c) 2016, 2020, Oracle and/or its affiliates. All rights reserved.
 */
package client_side_encryption;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.encryption.*;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * This class contains examples for encrypting/decrypting Text or Strings
 * on the client side using KMS service.
 *
 * Instructions:
 * As a pre-requisite for this example you must have a vault created in KMS with at least one master key.
 * OCI KMS has APIs that allow creating master keys or importing a previously generated key, either will work.
 * You can refer to the example at examples/kmsExample.java for an example of how to create a master key.
 *
 */
public class TextEncryptionExample {

    public static void main(final String[] args) throws Exception {

        if (args.length != 3) {
            throw new IllegalArgumentException(
                    "This example expects 3 arguments: Vault ID, Master Key Id and Region.");
        }

        // Read in vaultId, masterKeyId, region and perform basic validations.
        final String vaultId = args[0];
        final String masterKeyId = args[1];
        final String region = args[2];

        if (StringUtils.isBlank(vaultId)
                || StringUtils.isBlank(masterKeyId)
                || StringUtils.isBlank(region)) {
            System.out.println("vaultId or masterKeyId or region cannot be empty or null");
            return;
        }

        // Configuring the AuthenticationDetailsProvider. It's assuming there is a default OCI config file
        // "", and a profile in that config with the name "DEFAULT". Make changes to the following
        // line if needed.
        final String configurationFilePath = "~/.oci/config";
        final String profile = "DEFAULT";

        final AuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider(configurationFilePath, profile);

        // Create Key provider
        KmsMasterKey kmsMasterKey = new KmsMasterKey(provider, region, vaultId, masterKeyId);
        KmsMasterKeyProvider kmsMasterKeyProvider = new KmsMasterKeyProvider(kmsMasterKey);

        OciCrypto ociCrypto = new OciCrypto();

        final byte[] plainText = "Hello World".getBytes(StandardCharsets.UTF_8);
        OciCryptoResult result = ociCrypto.encryptData(kmsMasterKeyProvider, plainText);
        byte[] cipherText = result.getResult();

        OciCryptoResult decryptResult = ociCrypto.decryptData(kmsMasterKeyProvider, cipherText);
        byte[] decryptText = decryptResult.getResult();

        System.out.println("Decrypted text: " + new String(decryptText));
        kmsMasterKey.close();
    }
}
