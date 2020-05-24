package com.example.guoshijie.studyword.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.guoshijie.studyword.HttpHelper.NetworkUtil;
import com.example.guoshijie.studyword.HttpHelper.OkHttpHelper;
import com.example.guoshijie.studyword.R;
import com.example.guoshijie.studyword.Word.word;
import com.example.guoshijie.studyword.Word.wordInCloud;
import com.example.guoshijie.studyword.myAdapter.wordAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class DownLoad_wordsFragment extends android.support.v4.app.Fragment {
    private View view=null;
    private EditText editText;
    private ImageButton imageButton;
    private ImageButton deletehistory;
    private ListView listView;
    private String input;
    private wordAdapter adapter;
    private ArrayList<word> words=new ArrayList();
    private List<wordInCloud> historywords=new ArrayList();
    private FrameLayout histortview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_download, container, false);
            findview();
            setView();
        }

        return view;
    }

    private void findview() {
        editText=view.findViewById(R.id.editdownloadword);
        imageButton=view.findViewById(R.id.findword);
        listView=view.findViewById(R.id.list_view);
        histortview=view.findViewById(R.id.histortview);
        deletehistory=view.findViewById(R.id.deletehistory);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }
    public void setView(){
        deletehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LitePal.deleteAll(wordInCloud.class);
                loadhistorywords();
                Message message=new Message();
                message.what=0x111;
                handler.sendMessage(message);
            }
        });
        loadhistorywords();
        TextWatcher watch=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editText.getText().toString().isEmpty()){
                    histortview.setVisibility(View.GONE);
                    words.clear();
                }
                else {
                    histortview.setVisibility(View.VISIBLE);
                    loadhistorywords();
                }
            }
        };
        editText.addTextChangedListener(watch);
        adapter=new wordAdapter(getActivity(),R.layout.worditem,words,0);
        listView.setAdapter(adapter);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    input=editText.getText().toString();
                    if(input.isEmpty())return;
                    if(NetworkUtil.isNetworkAvailable(getContext())!=0){
                        Toast.makeText(getContext(),"not internet",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(isChinese(input)){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final ArrayList arrayList =OkHttpHelper.findenIncloud(input);
//                            Toast.makeText(getActivity(),arrayList.get(0).toString(),LENGTH_SHORT).show();
//                            OkHttpHelper.findchIncloud(arrayList.get(0).toString());
                                if(arrayList.size()==0){
//                                Toast.makeText(getContext(),"无此单词",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else {
                                    words.clear();
                                    for(int i=0;i<arrayList.size();i++){
                                        final int finalI = i;
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                wordInCloud wordInCloud =OkHttpHelper.findchIncloud(arrayList.get(finalI).toString());
                                                savehistory(wordInCloud);
                                                words.add(new word("", wordInCloud.getQueryText(), wordInCloud.getMeanText()));
                                                Message message=new Message();
                                                message.what=0x111;
                                                handler.sendMessage(message);
                                            }
                                        }).start();

                                    }
                                }
                            }
                        }).start();

                    }
                    if(isEnglish(input)){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                input=toLowerCase(input);
                                wordInCloud wordInCloud =OkHttpHelper.findchIncloud(input);

                                savehistory(wordInCloud);
                                words.clear();
                                words.add(new word("", wordInCloud.getQueryText(), wordInCloud.getMeanText()));
                                Message message=new Message();
                                message.what=0x111;
                                handler.sendMessage(message);
//                            Toast.makeText(getActivity(),wordInCloud.getQueryText(),LENGTH_SHORT).show();
                            }
                        }).start();
                    }


                }catch (Exception e){

                }

            }
        });


    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0X111){
                renewlist();
            }
            super.handleMessage(msg);
        }
    };
    private void renewlist() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
    public void onResume()
    {
        super.onResume();
    }
    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }
    // 判断一个字符串是否含有中文
    public static boolean isChinese(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (!isChinese(c)) return false;// 有一个中文字符就返回
        }
        return true;
    }
    public static boolean isEnglish(String charaString) {
        String string=charaString;
        string=string.replace(" ","");
        return string.matches("^[a-zA-Z]*");
    }
    public String toLowerCase(String str) {
        char[] s=str.toCharArray();
        for(int i=0;i<s.length;i++){
            if(s[i]>='A' && s[i]<='Z')
                s[i]+=32;
        }
        str="";
        for(int j=0;j<s.length;j++){
            str+=s[j];
        }
        return str;
    }
    public void savehistory(wordInCloud wordInCloud){
        //确保不重复
        LitePal.deleteAll(wordInCloud.class,"queryText = ?",wordInCloud.getQueryText());
        wordInCloud.save();
    }
    private void loadhistorywords(){
        historywords=LitePal.findAll(wordInCloud.class);
        words.clear();
        for(int i=historywords.size()-1;i>=0;i--){
            words.add(new word("", historywords.get(i).getQueryText(), historywords.get(i).getMeanText()));
        }
    }
}
