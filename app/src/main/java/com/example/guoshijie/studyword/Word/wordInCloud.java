package com.example.guoshijie.studyword.Word;

import org.litepal.crud.LitePalSupport;

public class wordInCloud extends LitePalSupport {
    private String queryText;
    private String voiceEnText;
    private String voiceEnUrlText;
    private String voiceAmText;
    private String voiceAmUrlText;
    private String meanText;
    private String exampleText;
public wordInCloud(String[] data){
    this.queryText=data[0];
    this.voiceEnText=data[1];
    this.voiceEnUrlText=data[2];
    this.voiceAmText=data[3];
    this.voiceAmUrlText=data[4];
    this.meanText=data[5];
    this.exampleText=data[6];

}
public wordInCloud(){

}
    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getVoiceEnText() {
        return voiceEnText;
    }

    public void setVoiceEnText(String voiceEnText) {
        this.voiceEnText = voiceEnText;
    }

    public String getVoiceEnUrlText() {
        return voiceEnUrlText;
    }

    public void setVoiceEnUrlText(String voiceEnUrlText) {
        this.voiceEnUrlText = voiceEnUrlText;
    }

    public String getVoiceAmText() {
        return voiceAmText;
    }

    public void setVoiceAmText(String voiceAmText) {
        this.voiceAmText = voiceAmText;
    }

    public String getVoiceAmUrlText() {
        return voiceAmUrlText;
    }

    public void setVoiceAmUrlText(String voiceAmUrlText) {
        this.voiceAmUrlText = voiceAmUrlText;
    }

    public String getMeanText() {
        return meanText;
    }

    public void setMeanText(String meanText) {
        this.meanText = meanText;
    }

    public String getExampleText() {
        return exampleText;
    }

    public void setExampleText(String exampleText) {
        this.exampleText = exampleText;
    }
}
