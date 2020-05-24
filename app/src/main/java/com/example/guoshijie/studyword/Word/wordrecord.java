package com.example.guoshijie.studyword.Word;

public class wordrecord {
    private String word;
    private String voiceEnText;
    private String voiceEnUrlText;
    private String voiceAmText;
    private String voiceAmUrlText;
    private String exampleText;
    public wordrecord(){

    }
    public wordrecord(String word, String voiceEnText, String voiceEnUrlText,String voiceAmText,String voiceAmUrlText,String exampleText){
        this.word=word;
        this.voiceEnText=voiceEnText;
        this.voiceEnUrlText=voiceEnUrlText;
        this.voiceAmText=voiceAmText;
        this.voiceAmUrlText=voiceAmUrlText;
        this.exampleText=exampleText;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
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

    public String getExampleText() {
        return exampleText;
    }

    public void setExampleText(String exampleText) {
        this.exampleText = exampleText;
    }
}
