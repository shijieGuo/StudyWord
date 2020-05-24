package com.example.guoshijie.studyword.Word;

public class word {
    private String partchapter;
    private String word;
    private String translation;
    public word(){

    }
    public word(String partchapter, String word, String translation){
        this.partchapter=partchapter;
        this.word=word;
        this.translation=translation;
    }
    public String getPartchapter() {
        return partchapter;
    }

    public void setPartchapter(String partchapter) {
        this.partchapter = partchapter;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
