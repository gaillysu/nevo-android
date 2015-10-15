package com.medcorp.nevo.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karl on 10/2/15.
 */
public class SleepBehavior {

    public enum SLEEP{
        DEEP,
        LIGHT,
        NOSLEEP
    };

    private List<SleepBehaviorSection> sleepBehaviorSectionList;

    public SleepBehavior(List<Integer> deepSleepList, List<Integer> lightSleepList) {
        sleepBehaviorSectionList = new ArrayList<SleepBehaviorSection>();
        // Assumption is made that a day starts at 00:00
        for (int i = 0; i< 24; i++) {
            int deepSleepTime = deepSleepList.get(i);
            int lightSleepTime = lightSleepList.get(i);
            if (deepSleepTime  == 0 || lightSleepTime == 0){
                sleepBehaviorSectionList.add(new SleepBehaviorSection(SLEEP.NOSLEEP, 0));
                continue;

            }
            sleepBehaviorSectionList.add(new SleepBehaviorSection(SLEEP.LIGHT,deepSleepTime - lightSleepTime));
                // assumption is made that a person always start in a light sleep phase.
            if ((i + 1) < lightSleepList.size() && (i + 1) < deepSleepList.size() )
            sleepBehaviorSectionList.add(new SleepBehaviorSection(SLEEP.DEEP, lightSleepList.get(i + 1) + (60-deepSleepTime)));
        }
    }

    public void printBehavior(){
        for (SleepBehaviorSection sleepBehaviorSection: sleepBehaviorSectionList) {
            Log.w("Karl", "This person slept " + sleepBehaviorSection.getBehavior());
            Log.w("Karl", "For about " + sleepBehaviorSection.getMinutes() + " minutes");
        }
    }
}
