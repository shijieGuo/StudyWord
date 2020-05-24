package com.example.guoshijie.studyword.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guoshijie.studyword.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF文件浏览中心
 * @author can
 *@since 2016-10-28
 */
public class PdfCenterDialog extends DialogFragment implements OnClickListener {
	
	private Button btn_close; //关闭识别结果框的按钮
	private ListView lv; //显示pdf文件集合控件
	private String[] strings; //pdf文件数组
	private List<VideoModel> list;//pdf文件集合
	
	public PdfCenterDialog() {
		super();
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		strings = PdfUtils.getString();
		list = new ArrayList<VideoModel>();
		if (strings != null)
			for (int i = 0; i < strings.length; i++) {
				VideoModel model = new VideoModel();
				String url = strings[i];
				model.setName(url.substring(url.lastIndexOf('/') + 1));
				model.setPath(url);
				list.add(model);
			}
	}

	@SuppressWarnings("deprecation")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View views = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_pdf_center, null);
		lv = (ListView) views.findViewById(R.id.lv_pdf_center);
		lv.setAdapter(new MyPdfAdapter(getActivity(), list));
		btn_close = (Button) views.findViewById(R.id.btn_close_pdf_center);
		btn_close.setOnClickListener(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		AlertDialog show = builder.show();
		show.getWindow().getAttributes().windowAnimations = R.style.dialog_ocr_result; //设置动画
		show.getWindow().setContentView(views);// 自定义布局
		show.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				getActivity().getWindowManager().getDefaultDisplay().getHeight()*2/3);// 宽高
		show.getWindow().setGravity(Gravity.BOTTOM);// 位置 展示位置
		show.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		return show;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_close_pdf_center: //关闭
			this.dismiss();
			break;
		}
	}
	
	/**
	 * PDF文件适配器
	 * @author can
	 */
	class MyPdfAdapter extends BaseAdapter {

		private Context ct ;
		private List<VideoModel> listpdf;
		
		public MyPdfAdapter(Context c, List<VideoModel> mList) {
			super();
			this.ct = c;
			this.listpdf = mList;
		}

		@Override
		public int getCount() {
			return listpdf.size();
		}

		@Override
		public Object getItem(int position) {
			return listpdf.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			VH vh ;
			if(convertView==null){
				convertView = LayoutInflater.from(ct).inflate(R.layout.item_pdf_list, null);
				vh = new VH();
				vh.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name_pdf_list);
				vh.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time_pdf_list);
				vh.iv = (ImageView) convertView
						.findViewById(R.id.iv_pdf_list);
				vh.tv_length = (TextView) convertView
						.findViewById(R.id.tv_length_pdf_list);
				vh.look=(LinearLayout)convertView.findViewById(R.id.look);
				vh.share=(Button)convertView.findViewById(R.id.pdfshare);
				vh.delete=(Button)convertView.findViewById(R.id.pdfdelete);
				convertView.setTag(vh);
			}else{
				vh = (VH) convertView.getTag();
			}
			vh.tv_name.setText(listpdf.get(position).getName());
			String path = listpdf.get(position).getPath();
			vh.tv_time.setText(BitmapUtil.getFileCreatedTime(path));
			vh.tv_length.setText(BitmapUtil.getVideoLength(path));
			vh.look.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					String path1 = PdfUtils.getString()[position];
					File file = new File(path1);
					Intent intent = new Intent("android.intent.action.VIEW");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Uri uri= FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".fileprovider",file );
					intent.setDataAndType(uri, "application/pdf");
					startActivity(intent);
				}
			});
			vh.share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					String path1 = PdfUtils.getString()[position];
					File file = new File(path1);
					Intent intent=new Intent(Intent.ACTION_SEND);
					Uri uri= FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".fileprovider",file );//Uri.fromFile(file);
					//intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
					intent.putExtra(Intent.EXTRA_STREAM,uri);
					intent.setType("application/octet-stream");
					//intent.setType("image/*");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(Intent.createChooser(intent,"share"));
				}
			});
			vh.delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					String path1 = PdfUtils.getString()[position];
					File file = new File(path1);
					file.delete();
					initData();
					listpdf=list;
					notifyDataSetChanged();
				}
			});
			return convertView;
		}
		
		class VH {
			ImageView iv;
			TextView tv_name,tv_time,tv_length;
			LinearLayout look;
			Button share,delete;
		}
		
	}
	
}
