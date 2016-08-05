package com.medcorp.model;

/**
 * Created by med on 16/8/5.
 */
public class ApplicationInfomation {
    /**
     * listNumber: 0~31 when updated
     *           :0x80  when created
     */
    private byte listNumber;
    /**
     * 2 bytes length one led will match one bit,enable is 1, disable is 0
     */
    private short ledPattern;

    /**
     * application ID, use the package name
     */
    private String data;

    public ApplicationInfomation(byte listNumber,short ledPattern,String data) {
        this.listNumber = listNumber;
        this.ledPattern = ledPattern;
        this.data = data;
    }

    public byte getListNumber() {
        return listNumber;
    }

    public short getLedPattern() {
        return ledPattern;
    }

    public String getData() {
        return data;
    }
}
