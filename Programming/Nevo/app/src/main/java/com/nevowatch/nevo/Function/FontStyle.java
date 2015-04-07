package com.nevowatch.nevo.Function;

/**
 * FontStyle
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


