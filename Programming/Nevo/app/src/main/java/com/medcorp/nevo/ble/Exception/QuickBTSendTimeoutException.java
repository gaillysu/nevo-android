package com.medcorp.nevo.ble.Exception;

/**
 * Created by gaillysu on 15/5/11.
 * when QuickBT send request timeout,throw this class
 * perhaps connect timeout,or discovery timeout or parameter error, all will lead to send timeout
 */
public class QuickBTSendTimeoutException extends Exception{
}
