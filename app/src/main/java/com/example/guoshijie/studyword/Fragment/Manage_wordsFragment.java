package com.example.guoshijie.studyword.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.guoshijie.studyword.Dialog.PdfUtils;
import com.example.guoshijie.studyword.Dialog.WordDialog;
import com.example.guoshijie.studyword.R;
import com.example.guoshijie.studyword.Word.word;
import com.example.guoshijie.studyword.Word.wordrecord;
import com.example.guoshijie.studyword.example.example;
import com.example.guoshijie.studyword.getwordsFromMysql.getnotewordsFromMysql;
import com.example.guoshijie.studyword.getwordsFromMysql.getpartchapterFromMysql;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordrecordFromMysql;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordsFromMysql;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Manage_wordsFragment extends android.support.v4.app.Fragment {
    private View view=null;
    private ListView notelist;
    private ArrayList<String> note=new ArrayList<String>();
    private getnotewordsFromMysql getnotewordsFromMysql;
    private Button turnpdf;
    private ProgressDialog myDialog; // 保存进度框
    private static final int PDF_SAVE_START = 1;// 保存PDF文件的开始意图
    private static final int PDF_SAVE_RESULT = 2;// 保存PDF文件的结果开始意图
    private Socket socket = null;
    private String Code_Adress = "193.112.144.243";
    private Button clean;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_managewords, container, false);
            findview();
            setView();
        }
        return view;
    }
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PDF_SAVE_START:
                    if (!myDialog.isShowing())
                        myDialog.show();
                    break;

                case PDF_SAVE_RESULT:
                    if (myDialog.isShowing())
                        myDialog.dismiss();
                    Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
            return false;
        }
    });
    private void findview() {
        clean=view.findViewById(R.id.clean);
        notelist=view.findViewById(R.id.notelist);
        turnpdf=view.findViewById(R.id.turnpdf);
        getnotewordsFromMysql=new getnotewordsFromMysql(getContext());
        note=getnotewordsFromMysql.getnotewords();
    }

    private void setView(){
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getnotewordsFromMysql.cleanwords();
                refresh();
                Toast.makeText(getContext(),"列表已清空",Toast.LENGTH_SHORT).show();
            }
        });
        initProgress();
        ArrayAdapter<String> adapter1=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,note);
        notelist.setAdapter(adapter1);
        notelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s=note.get(i);
                Log.d("Avalible", "Process:"+s);
                wordrecord wordrecord=new getwordrecordFromMysql(getContext()).getwordrecord(s);
                word word1=new getwordsFromMysql(getContext()).getword(s);
                WordDialog wordDialog=new WordDialog(getContext(),word1,wordrecord,0.6,0.75,1);
                wordDialog.setCancelable(true);
                wordDialog.show();
            }
        });
        notelist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                builder.setPositiveButton("查看更多", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final byte[] bytes = new byte[10000];
                                    socket = new Socket(Code_Adress,8888);
                                    OutputStream outputStream = socket.getOutputStream();
                                    InputStream inputStream=socket.getInputStream();
                                    outputStream.write(note.get(i).getBytes());
                                    inputStream.read(bytes);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent =new  Intent(getActivity(),example.class);
                                            intent.putExtra("word",note.get(i));
                                            intent.putExtra("dataSend",new String (bytes).trim());
                                            startActivity(intent);
//                                            Toast.makeText(getContext(),"收到服务器返回："+new String (bytes).trim(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Log.d("receve", new String (bytes).trim());
                                    outputStream.close();
                                    inputStream.close();
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                builder.setNeutralButton("移除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getnotewordsFromMysql.deleteword(note.get(i));
                        refresh();
                        Toast.makeText(getContext(),"移除成功",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                return true;
            }
        });
        turnpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToPdf();
            }
        });

    }
    private void initProgress() {
        myDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_HOLO_LIGHT);
        myDialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.progress_ocr));
        myDialog.setMessage("正在保存PDF文件...");
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
    private void turnToPdf() {
        File file = new File(PdfUtils.ADDRESS);
        if (!file.exists())
            file.mkdirs();
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String pdf_address = PdfUtils.ADDRESS + File.separator + "My Note" + ".pdf";
        handler.sendEmptyMessage(PDF_SAVE_START);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = new Document();// 创建一个document对象
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(pdf_address); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    //
                    for(int i=0;i<note.size();i++){
                        wordrecord wordrecord=new getwordrecordFromMysql(getContext()).getwordrecord(note.get(i));
                        word word=new getwordsFromMysql(getContext()).getword(note.get(i));
                        String PDF="单词："+note.get(i)+"\n"+
                                "词义："+word.getTranslation()+"\n"+
                                "例句："+wordrecord.getExampleText()+"\n"+
                        "\n\n";

                        doc.add(new Paragraph(PDF,
                                setChineseFont())); // result为保存的字符串
                    }
                    //
                    // ,setChineseFont()为pdf字体
                    // 一定要记得关闭document对象
                    doc.close();
                    fos.flush();
                    fos.close();
                    handler.sendEmptyMessage(PDF_SAVE_RESULT);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 设置PDF字体(较为耗时)
     */
    public Font setChineseFont() {
        BaseFont bf = null;
        Font fontChinese = null;
        try {
            // STSong-Light : Adobe的字体
            // UniGB-UCS2-H : pdf 字体
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                    BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, 12, Font.NORMAL);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontChinese;
    }
    public void refresh(){
        note=getnotewordsFromMysql.getnotewords();
        ArrayAdapter<String> adapter1=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,note);
        notelist.setAdapter(adapter1);
    }
}