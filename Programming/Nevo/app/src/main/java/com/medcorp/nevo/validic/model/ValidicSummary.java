package com.medcorp.nevo.validic.model;

/**
 * Created by gaillysu on 16/3/9.
 */
public class ValidicSummary {
    int status;
    String message;
    int result;
    long start_date;
    long end_date;
    int offset;
    int limit;
    ValidicParams params;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setStart_date(long start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setParams(ValidicParams params) {
        this.params = params;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getResult() {
        return result;
    }

    public long getStart_date() {
        return start_date;
    }

    public long getEnd_date() {
        return end_date;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public ValidicParams getParams() {
        return params;
    }
}
