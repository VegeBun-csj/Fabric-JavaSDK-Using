/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import java.nio.file.Paths;
import java.util.Properties;

import org.example.Common.User;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallet.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class RegisterUser {

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    public static void main(String[] args) throws Exception {

        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile", EnrollAdmin.CERTIFICATION_PATH);
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance(EnrollAdmin.PATH_AND_PORT, props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // Create a wallet for managing identities
        Wallet wallet = Wallet.createFileSystemWallet(Paths.get("src/main/resources/wallet"));

        // Check to see if we've already enrolled the user.
        boolean userExists = wallet.exists("ccc");
        if (userExists) {
            System.out.println("An identity for the user \"ccc\" already exists in the wallet");
            return;
        }

        userExists = wallet.exists("admin");
        if (!userExists) {
            System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
            return;
        }

        Identity adminIdentity = wallet.get("admin");
        org.hyperledger.fabric.sdk.User admin = new User.Builder()
                .name("admin")
                .affiliation("org1.department1")
                .mspId("Org1MSP")
                .enrollment(adminIdentity)
                .build();

        // Register the user, enroll the user, and import the new identity into the wallet.
        RegistrationRequest registrationRequest = new RegistrationRequest("ccc");
        registrationRequest.setAffiliation("org1.department1");
        registrationRequest.setEnrollmentID("ccc");
        String enrollmentSecret = caClient.register(registrationRequest, admin);
        Enrollment enrollment = caClient.enroll("ccc", enrollmentSecret);
        Identity user = Identity.createIdentity("Org1MSP", enrollment.getCert(), enrollment.getKey());
        wallet.put("ccc", user);
        System.out.println("Successfully enrolled user \"ccc\" and imported it into the wallet");
    }

}
