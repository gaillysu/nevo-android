package com.medcorp.nevo.validic.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by gaillysu on 16/3/9.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyCredentialModel {
    ValidicSummary summary;
    ValidicOrganization organization;
}
