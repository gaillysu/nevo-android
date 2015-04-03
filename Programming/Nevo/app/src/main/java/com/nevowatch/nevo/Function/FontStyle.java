package com.nevowatch.nevo.Function;

/**
 * Created by imaze on 4/3/15.
 */
public class FontStyle {

    private static FontStyle mFontSingleInstance = null;

    private FontStyle(){

    }

    public static FontStyle getFontSingleInstance(){
        if(mFontSingleInstance == null){
            mFontSingleInstance = new FontStyle();
        }
        return mFontSingleInstance;
    }
}


