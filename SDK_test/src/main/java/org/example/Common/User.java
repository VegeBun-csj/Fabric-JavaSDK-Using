package org.example.Common;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.sdk.Enrollment;

import java.security.PrivateKey;
import java.util.Set;

public class User implements org.hyperledger.fabric.sdk.User {
    private String name;
    private Set<String> roles;
    private String account;
    private String affiliation;
    private Enrollment enrollment;
    private String mspId;

    private User(Builder builder) {
        this.name = builder.name;
        this.roles = builder.roles;
        this.account = builder.account;
        this.affiliation = builder.affiliation;
        this.enrollment = builder.enrollment;
        this.mspId = builder.mspId;
    }

    public static class Builder {
        private String name;
        private Set<String> roles;
        private String account;
        private String affiliation;
        private Enrollment enrollment;
        private String mspId;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder account(String account) {
            this.account = account;
            return this;
        }

        public Builder affiliation(String affiliation) {
            this.affiliation = affiliation;
            return this;
        }

        public Builder enrollment(Enrollment enrollment) {
            this.enrollment = enrollment;
            return this;
        }

        public Builder enrollment(Wallet.Identity identity) {
            this.enrollment = new Enrollment() {

                @Override
                public PrivateKey getKey() {
                    return identity.getPrivateKey();
                }

                @Override
                public String getCert() {
                    return identity.getCertificate();
                }
            };
            return this;
        }

        public Builder mspId(String mspId) {
            this.mspId = mspId;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    @Override
    public String getAccount() {
        return this.account;
    }

    @Override
    public String getAffiliation() {
        return this.affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return this.enrollment;
    }

    @Override
    public String getMspId() {
        return this.mspId;
    }
}
