package com.nevowatch.nevo.ble.model.request;

public class SetNotificationNevoRequest extends NevoRequest {
	public  final static  byte HEADER = 0x02;
    public static class SetNortificationRequestValues {
        //default vibrator number is 3
        final static byte VIBRATION_ON = 0x03;
        final static byte VIBRATION_OFF = 0x00;
        //motor control bit is bit23
        final static int VIB_MOTOR = 0x800000;
        final static int LED_OFF = 0x000000;
        //color LED control bit is bit16~21
        final static int BLUE_LED   = 0x010000;
        final static int GREEN_LED  = 0x100000;
        final static int YELLOW_LED = 0x040000;
        final static int RED_LED    = 0x200000;
        final static int ORANGE_LED = 0x080000;
        final static int LIGHTGREEN_LED = 0x020000;
        //white LED control bit is bit0~10
        final static int WHITE_1_LED = 0x000001;
        final static int WHITE_3_LED = 0x000004;
        final static int WHITE_5_LED = 0x000010;
        final static int WHITE_7_LED = 0x000040;
        final static int WHITE_9_LED = 0x000100;
        final static int WHITE_11_LED = 0x000400;
    }

    private byte call_vib_number = 0;
    private int call_led_pattern = 0;

    private byte sms_vib_number = 0;
    private int sms_led_pattern = 0;

    private byte email_vib_number = 0;
    private int email_led_pattern = 0;

    private byte facebook_vib_number = 0;
    private int facebook_led_pattern = 0;

    private byte calendar_vib_number = 0;
    private int calendar_led_pattern = 0;

    private byte wechat_vib_number = 0;
    private int wechat_led_pattern = 0;

    @Override
	public byte[] getRawData() {

		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
        call_vib_number = SetNortificationRequestValues.VIBRATION_ON;
        call_led_pattern = SetNortificationRequestValues.VIB_MOTOR | SetNortificationRequestValues.ORANGE_LED;

        sms_vib_number = SetNortificationRequestValues.VIBRATION_ON;
        sms_led_pattern = SetNortificationRequestValues.VIB_MOTOR | SetNortificationRequestValues.GREEN_LED;

        email_vib_number = SetNortificationRequestValues.VIBRATION_ON;
        email_led_pattern = SetNortificationRequestValues.VIB_MOTOR | SetNortificationRequestValues.YELLOW_LED;

        facebook_vib_number = SetNortificationRequestValues.VIBRATION_ON;
        facebook_led_pattern = SetNortificationRequestValues.VIB_MOTOR | SetNortificationRequestValues.BLUE_LED;

        calendar_vib_number = SetNortificationRequestValues.VIBRATION_ON;
        calendar_led_pattern = SetNortificationRequestValues.VIB_MOTOR | SetNortificationRequestValues.RED_LED;

        wechat_vib_number = SetNortificationRequestValues.VIBRATION_ON;
        wechat_led_pattern = SetNortificationRequestValues.VIB_MOTOR | SetNortificationRequestValues.LIGHTGREEN_LED;

        return new byte[][] {
                {0,HEADER,
                        (byte)(call_vib_number&0xFF),
                        (byte)(call_led_pattern&0xFF),
                        (byte)((call_led_pattern>>8)&0xFF),
                        (byte)((call_led_pattern>>16)&0xFF),

                        (byte)(sms_vib_number&0xFF),
                        (byte)(sms_led_pattern&0xFF),
                        (byte)((sms_led_pattern>>8)&0xFF),
                        (byte)((sms_led_pattern>>16)&0xFF),

                        (byte)(email_vib_number&0xFF),
                        (byte)(email_led_pattern&0xFF),
                        (byte)((email_led_pattern>>8)&0xFF),
                        (byte)((email_led_pattern>>16)&0xFF),

                        (byte)(facebook_vib_number&0xFF),
                        (byte)(facebook_led_pattern&0xFF),
                        (byte)((facebook_led_pattern>>8)&0xFF),
                        (byte)((facebook_led_pattern>>16)&0xFF),

                        (byte)(calendar_vib_number&0xFF),
                        (byte)(calendar_led_pattern&0xFF)
                },

                {(byte) 0xFF,HEADER,
                        (byte)((calendar_led_pattern>>8)&0xFF),
                        (byte)((calendar_led_pattern>>16)&0xFF),
                        (byte)(wechat_vib_number&0xFF),
                        (byte)(wechat_led_pattern&0xFF),
                        (byte)((wechat_led_pattern>>8)&0xFF),
                        (byte)((wechat_led_pattern>>16)&0xFF),
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0
                }
        };

	}

	@Override
	public byte getHeader() {
			return HEADER;
	}

}
