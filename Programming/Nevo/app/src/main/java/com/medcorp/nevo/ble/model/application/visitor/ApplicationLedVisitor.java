package com.medcorp.nevo.ble.model.application.visitor;

import com.medcorp.nevo.ble.model.application.ApplicationLed;
import com.medcorp.nevo.ble.model.application.CalendarColor;
import com.medcorp.nevo.ble.model.application.EmailColor;
import com.medcorp.nevo.ble.model.application.FacebookColor;
import com.medcorp.nevo.ble.model.application.SmsColor;
import com.medcorp.nevo.ble.model.application.TelephoneColor;
import com.medcorp.nevo.ble.model.application.WeChatColor;
import com.medcorp.nevo.ble.model.application.WhatsappColor;

/**
 * Created by Karl on 9/30/15.
 */
public interface ApplicationLedVisitor<T> {

    public T visit(CalendarColor appColor);
    public T visit(EmailColor appColor);
    public T visit(FacebookColor appColor);
    public T visit(SmsColor appColor);
    public T visit(TelephoneColor appColor);
    public T visit(WeChatColor appColor);
    public T visit(WhatsappColor appColor);
    public T visit(ApplicationLed appColor);

}
