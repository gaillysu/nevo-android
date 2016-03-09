package com.medcorp.nevo.validic.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by gaillysu on 16/3/9.
 */
public class VerifyCredentialModel {
    ValidicSummary summary;
    ValidicOrganization organization;

    public ValidicSummary getSummary() {
        return summary;
    }

    public void setSummary(ValidicSummary summary) {
        this.summary = summary;
    }

    public ValidicOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(ValidicOrganization organization) {
        this.organization = organization;
    }
}
