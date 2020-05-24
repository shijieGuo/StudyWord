package com.example.guoshijie.studyword.Json;

import java.util.ArrayList;
import java.util.List;

public class wordjson {
    /**
     * word_id : 2143763
     * word_name : 好
     * symbols : [{"symbol_id":"2144972","word_id":"2143763","word_symbol":"hǎo","symbol_mp3":"http://res.iciba.com/hanyu/zi/19a73d32bf61c88bc4ed86c40f26bc9c.mp3","parts":[{"part_name":"形","means":[{"mean_id":"2465987","part_id":"2148468","word_mean":"good","has_mean":"1","split":1},{"mean_id":"2465988","part_id":"2148468","word_mean":"fine","has_mean":"1","split":1},{"mean_id":"2465989","part_id":"2148468","word_mean":"nice","has_mean":"1","split":0}]}],"ph_am_mp3":"","ph_en_mp3":"","ph_tts_mp3":"","ph_other":""},{"symbol_id":"2144973","word_id":"2143763","word_symbol":"hào","symbol_mp3":"","parts":[{"part_name":"动","means":[{"mean_id":"2465990","part_id":"2148469","word_mean":"like","has_mean":"1","split":1},{"mean_id":"2465991","part_id":"2148469","word_mean":"love","has_mean":"1","split":1},{"mean_id":"2465992","part_id":"2148469","word_mean":"be fond of","has_mean":"1","split":0}]},{"part_name":"名","means":[{"mean_id":"2465993","part_id":"2148470","word_mean":"a surname","has_mean":"0","split":0}]}]}]
     */

    private List<SymbolsBean> symbols;

    public List<SymbolsBean> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<SymbolsBean> symbols) {
        this.symbols = symbols;
    }

    public static class SymbolsBean {
        /**
         * symbol_id : 2144972
         * word_id : 2143763
         * word_symbol : hǎo
         * symbol_mp3 : http://res.iciba.com/hanyu/zi/19a73d32bf61c88bc4ed86c40f26bc9c.mp3
         * parts : [{"part_name":"形","means":[{"mean_id":"2465987","part_id":"2148468","word_mean":"good","has_mean":"1","split":1},{"mean_id":"2465988","part_id":"2148468","word_mean":"fine","has_mean":"1","split":1},{"mean_id":"2465989","part_id":"2148468","word_mean":"nice","has_mean":"1","split":0}]}]
         * ph_am_mp3 :
         * ph_en_mp3 :
         * ph_tts_mp3 :
         * ph_other :
         */

        private List<PartsBean> parts;

        public List<PartsBean> getParts() {
            return parts;
        }

        public void setParts(List<PartsBean> parts) {
            this.parts = parts;
        }

        public static class PartsBean {
            /**
             * part_name : 形
             * means : [{"mean_id":"2465987","part_id":"2148468","word_mean":"good","has_mean":"1","split":1},{"mean_id":"2465988","part_id":"2148468","word_mean":"fine","has_mean":"1","split":1},{"mean_id":"2465989","part_id":"2148468","word_mean":"nice","has_mean":"1","split":0}]
             */

            private List<MeansBean> means;

            public List<MeansBean> getMeans() {
                return means;
            }

            public void setMeans(List<MeansBean> means) {
                this.means = means;
            }

            public static class MeansBean {
                /**
                 * mean_id : 2465987
                 * part_id : 2148468
                 * word_mean : good
                 * has_mean : 1
                 * split : 1
                 */

                private String word_mean;

                public String getWord_mean() {
                    return word_mean;
                }

                public void setWord_mean(String word_mean) {
                    this.word_mean = word_mean;
                }
            }
        }
    }
    public ArrayList<String> getword_mean(){
        ArrayList<String> arrayList=new ArrayList();

        List<SymbolsBean> symbols=getSymbols();
        for(int i=0;i<symbols.size();i++){
            List<SymbolsBean.PartsBean> partsBean=symbols.get(i).getParts();
            for(int j=0;j<partsBean.size();j++){
                List<SymbolsBean.PartsBean.MeansBean> meansBeans=partsBean.get(j).getMeans();
                for(int k=0;k<meansBeans.size();k++){
                    arrayList.add(meansBeans.get(k).getWord_mean());
                }
            }

        }
        return arrayList;
    }
}
