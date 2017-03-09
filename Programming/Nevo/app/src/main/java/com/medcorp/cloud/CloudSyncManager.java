package com.medcorp.cloud;

import com.medcorp.application.ApplicationModel;
import com.medcorp.cloud.med.MedOperation;
import com.medcorp.cloud.validic.ValidicOperation;
import com.medcorp.event.validic.ValidicReadMoreRoutineRecordsModelEvent;
import com.medcorp.event.validic.ValidicReadMoreSleepRecordsModelEvent;
import com.medcorp.model.Sleep;
import com.medcorp.model.Steps;
import com.medcorp.model.User;
import com.medcorp.network.listener.ResponseListener;
import com.medcorp.network.med.model.CreateUser;
import com.medcorp.network.med.model.CreateUserModel;
import com.medcorp.network.med.model.LoginUser;
import com.medcorp.network.med.model.LoginUserModel;
import com.medcorp.network.med.model.MedReadMoreRoutineRecordsModel;
import com.medcorp.network.med.model.MedReadMoreSleepRecordsModel;
import com.medcorp.network.med.model.UserWithID;
import com.medcorp.network.med.model.UserWithLocation;
import com.medcorp.network.validic.model.ValidicReadMoreRoutineRecordsModel;
import com.medcorp.network.validic.model.ValidicReadMoreSleepRecordsModel;
import com.medcorp.util.Common;
import com.medcorp.event.validic.ValidicException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by med on 16/3/23.
 * this class do cloud sync between validic server and local database
 * when do cloud sync operation?
 * case1: syncController big/little sync done
 * case2: user login
 * case3: ???
 * IMPORTANT, IF TOO MANY RECORDS NEED SYNC, PERHAPS SPEND TOO LONG TIME,
 * START AN BACKGROUND TASK TO DO IT IS A GOOD IDEA.
 * how to have a cloud sync?
 * step1: sync local steps & sleep records if their Valid_record_ID is "0","0" is the default value in the steps/sleep local table
 * step2: read validic records between start_date = Date.now() - 365 and end_date =  Date.now(),every time get limit 100 records.
 * step3: check step2 return list, get those records that not found in local database, save them to local database.
 * step4: goto step2, change the page number,read next page until step2 return summary.next is null
 */
public class CloudSyncManager {
    private final String TAG = "CloudSyncManager";
    final long INTERVAL_DATE = 365 * 24 * 60 * 60 *1000l;//user can get all data in a year
    //here select which one cloud server
    final CloudServerProvider cloudServerProvider = CloudServerProvider.Med;
    
    private ApplicationModel context;
    public CloudSyncManager(ApplicationModel context)
    {
        this.context = context;
    }
    private ApplicationModel getModel(){
        return context;
    }

    public void createUser(CreateUser createUser)
    {
        //TODO if enable validic, here open it
        //ValidicOperation.getInstance(context).createValidicUser(...);
        MedOperation.getInstance(context).createMedUser(createUser, new RequestListener<CreateUserModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                //DO NOTHING
            }

            @Override
            public void onRequestSuccess(CreateUserModel createUserModel) {
                if(createUserModel.getStatus()==1 && createUserModel.getUser()!=null){
                    //save user ID and other profile infomation to local database
                    User nevoUser = getModel().getNevoUser();
                    UserWithID user = createUserModel.getUser();
                    try {
                        nevoUser.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(user.getBirthday().getDate()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    nevoUser.setFirstName(user.getFirst_name());
                    nevoUser.setHeight((int)user.getLength());
                    nevoUser.setLastName(user.getLast_name());
                    nevoUser.setWeight((int)user.getWeight());
                    nevoUser.setNevoUserID(""+user.getId());
                    nevoUser.setNevoUserEmail(user.getEmail());
                    getModel().saveNevoUser(nevoUser);
                }
            }
        });
    }

    public void userLogin(LoginUser loginUser)
    {
        //TODO if enable validic, here call ValidicOperation function
        MedOperation.getInstance(context).userMedLogin(loginUser, new RequestListener<LoginUserModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }
            @Override
            public void onRequestSuccess(LoginUserModel loginUserModel) {
                if (loginUserModel.getStatus() == 1) {
                    UserWithLocation user = loginUserModel.getUser();
                    User nevoUser = getModel().getNevoUser();
                    try {
                        nevoUser.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(user.getBirthday().getDate()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    nevoUser.setFirstName(user.getFirst_name());
                    nevoUser.setHeight(user.getLength());
                    nevoUser.setLastName(user.getLast_name());
                    nevoUser.setWeight(user.getWeight());
                    nevoUser.setNevoUserID(""+user.getId());
                    nevoUser.setNevoUserEmail(user.getEmail());
                    nevoUser.setIsLogin(true);
                    nevoUser.setCreatedDate(new Date().getTime());
                    //save it and sync with watch and cloud server
                    getModel().saveNevoUser(nevoUser);
                    getModel().getSyncController().getDailyTrackerInfo(true);
                    launchSyncAll(nevoUser, getModel().getNeedSyncSteps(nevoUser.getNevoUserID()), getModel().getNeedSyncSleep(nevoUser.getNevoUserID()));
                }
            }
        });
    }

    /**
     * when user login, invoke it
     */
    public void launchSyncAll(User user, List<Steps> stepsList, List<Sleep> sleepList){
        for(Steps steps: stepsList)
        {
            if((cloudServerProvider.getRawValue()&CloudServerProvider.Validic.getRawValue())==CloudServerProvider.Validic.getRawValue()) {
                ValidicOperation.getInstance(context).addValidicRoutineRecord(user, steps, new Date(steps.getDate()), null);
            }
            if((cloudServerProvider.getRawValue()&CloudServerProvider.Med.getRawValue()) == CloudServerProvider.Med.getRawValue()) {
                MedOperation.getInstance(context).addMedRoutineRecord(user, steps, new Date(steps.getDate()), null);
            }
        }
        //calculate today 's last time: 23:59:59
        Date endDate = new Date(Common.removeTimeFromDate(new Date()).getTime()+24*60*60*1000l-1);
        Date startDate = new Date(endDate.getTime() - INTERVAL_DATE);
        downloadSteps(user,startDate,endDate,1,cloudServerProvider);
        for(Sleep sleep: sleepList)
        {
            if((cloudServerProvider.getRawValue() & CloudServerProvider.Validic.getRawValue())==CloudServerProvider.Validic.getRawValue()) {
                ValidicOperation.getInstance(context).addValidicSleepRecord(user, sleep, new Date(sleep.getDate()), null);
            }
            if((cloudServerProvider.getRawValue()&CloudServerProvider.Med.getRawValue()) == CloudServerProvider.Med.getRawValue()) {
                MedOperation.getInstance(context).addMedSleepRecord(user, sleep, new Date(sleep.getDate()), null);
            }
        }
        downloadSleep(user, startDate,endDate,1,cloudServerProvider);
    }

    private void downloadSteps(final User user, final Date startDate, final Date endDate,final int page,CloudServerProvider provider)
    {
        if((provider.getRawValue()&CloudServerProvider.Validic.getRawValue())==CloudServerProvider.Validic.getRawValue()) {
            ValidicOperation.getInstance(context).getMoreValidicRoutineRecord(user, startDate, endDate, page, new ResponseListener<ValidicReadMoreRoutineRecordsModel>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    EventBus.getDefault().post(new ValidicException(spiceException));
                }

                @Override
                public void onRequestSuccess(ValidicReadMoreRoutineRecordsModel validicReadMoreRoutineRecordsModel) {
                    if (validicReadMoreRoutineRecordsModel.getSummary().getResults() > 0) {
                        EventBus.getDefault().post(new ValidicReadMoreRoutineRecordsModelEvent(validicReadMoreRoutineRecordsModel));
                        if (validicReadMoreRoutineRecordsModel.getSummary().getNext() != null) {
                            String nextPageUrl = validicReadMoreRoutineRecordsModel.getSummary().getNext();
                            int pageStart = nextPageUrl.indexOf("page=");
                            int pageEnd = nextPageUrl.substring(pageStart).indexOf("&");
                            int nextPage = Integer.parseInt(nextPageUrl.substring(pageStart).substring(5, pageEnd));
                            downloadSteps(user, startDate, endDate, nextPage,CloudServerProvider.Validic);
                        }
                    }
                }
            });
        }
        if((provider.getRawValue()&CloudServerProvider.Med.getRawValue())==CloudServerProvider.Med.getRawValue()) {
            MedOperation.getInstance(context).getMoreMedRoutineRecord(user, startDate, endDate, new ResponseListener<MedReadMoreRoutineRecordsModel>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {

                }

                @Override
                public void onRequestSuccess(MedReadMoreRoutineRecordsModel medReadMoreRoutineRecordsModel) {

                    if (medReadMoreRoutineRecordsModel.getStatus() == 1 && medReadMoreRoutineRecordsModel.getSteps() != null && medReadMoreRoutineRecordsModel.getSteps().length > 0) {
                        Date endDate = new Date(startDate.getTime() - 24 * 60 * 60 * 1000l);
                        Date startDate = new Date(endDate.getTime() - 30 * 24 * 60 * 60 * 1000l);
                        //no page split
                        downloadSteps(user, startDate, endDate, 0,CloudServerProvider.Med);
                    }
                }
            });
        }
    }


    private void downloadSleep(final User user, final Date startDate, final Date endDate,final int page,CloudServerProvider provider)
    {
        if((provider.getRawValue()&CloudServerProvider.Validic.getRawValue())==CloudServerProvider.Validic.getRawValue()) {
            ValidicOperation.getInstance(context).getMoreValidicSleepRecord(user, startDate, endDate, page, new ResponseListener<ValidicReadMoreSleepRecordsModel>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    EventBus.getDefault().post(new ValidicException(spiceException));
                }

                @Override
                public void onRequestSuccess(ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel) {
                    if (validicReadMoreSleepRecordsModel.getSummary().getResults() > 0) {
                        EventBus.getDefault().post(new ValidicReadMoreSleepRecordsModelEvent(validicReadMoreSleepRecordsModel));
                        if (validicReadMoreSleepRecordsModel.getSummary().getNext() != null) {
                            String nextPageUrl = validicReadMoreSleepRecordsModel.getSummary().getNext();
                            int pageStart = nextPageUrl.indexOf("page=");
                            int pageEnd = nextPageUrl.substring(pageStart).indexOf("&");
                            int nextPage = Integer.parseInt(nextPageUrl.substring(pageStart).substring(5, pageEnd));
                            downloadSleep(user, startDate, endDate, nextPage,CloudServerProvider.Validic);
                        }
                    }
                }
            });
        }
        if((provider.getRawValue()&CloudServerProvider.Med.getRawValue())==CloudServerProvider.Med.getRawValue()) {
            MedOperation.getInstance(context).getMoreMedSleepRecord(user, startDate, endDate, new ResponseListener<MedReadMoreSleepRecordsModel>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {

                }

                @Override
                public void onRequestSuccess(MedReadMoreSleepRecordsModel medReadMoreSleepRecordsModel) {
                    if (medReadMoreSleepRecordsModel.getStatus() == 1 && medReadMoreSleepRecordsModel.getSleep() != null && medReadMoreSleepRecordsModel.getSleep().length > 0) {
                        Date endDate = new Date(startDate.getTime() - 24 * 60 * 60 * 1000l);
                        Date startDate = new Date(endDate.getTime() - 30 * 24 * 60 * 60 * 1000l);
                        //no page split
                        downloadSleep(user, startDate, endDate, 0,CloudServerProvider.Med);
                    }
                }
            });
        }
    }

    /**
     * when today's steps got change, invoke it
     */
    public void launchSyncDaily(User user, Steps steps)
    {
        if((cloudServerProvider.getRawValue()&CloudServerProvider.Validic.getRawValue()) == CloudServerProvider.Validic.getRawValue()) {
            ValidicOperation.getInstance(context).addValidicRoutineRecord(user, steps, new Date(), null);
        }
        if((cloudServerProvider.getRawValue()&CloudServerProvider.Med.getRawValue()) == CloudServerProvider.Med.getRawValue()) {
            MedOperation.getInstance(context).addMedRoutineRecord(user, steps, new Date(), null);
        }
    }

    /**
     * when syncController big sync is done, invoke it
     */
    public void launchSyncWeekly(User user, List<Steps> stepsList, List<Sleep> sleepList)
    {
        for(Steps steps: stepsList)
        {
            if((cloudServerProvider.getRawValue()&CloudServerProvider.Validic.getRawValue()) == CloudServerProvider.Validic.getRawValue()) {
                ValidicOperation.getInstance(context).addValidicRoutineRecord(user, steps,new Date(steps.getDate()),null);
            }
            if((cloudServerProvider.getRawValue()&CloudServerProvider.Med.getRawValue()) == CloudServerProvider.Med.getRawValue()) {
                MedOperation.getInstance(context).addMedRoutineRecord(user,steps,new Date(steps.getDate()),null);
            }
        }

        for(Sleep sleep: sleepList)
        {
            if((cloudServerProvider.getRawValue()&CloudServerProvider.Validic.getRawValue()) == CloudServerProvider.Validic.getRawValue()) {
                ValidicOperation.getInstance(context).addValidicSleepRecord(user, sleep , new Date(sleep.getDate()), null);
            }
            if((cloudServerProvider.getRawValue()&CloudServerProvider.Med.getRawValue()) == CloudServerProvider.Med.getRawValue()) {
                MedOperation.getInstance(context).addMedSleepRecord(user, sleep , new Date(sleep.getDate()), null);
            }
        }
    }
}
