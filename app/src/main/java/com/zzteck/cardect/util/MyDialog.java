package com.zzteck.cardect.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzteck.cardect.R;

public class MyDialog extends Dialog {
	private Button positiveButton, negativeButton;
	private TextView title, content;
	private EditText input;
	private Context mcontext;
	
	private LinearLayout mLLContent ;


	public MyDialog(Context context) {
		super(context, R.style.mydialog);
		this.mcontext=context;
		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.mydialoglayout, null); // 通过LayoutInflater获取布局
		mLLContent = (LinearLayout)view.findViewById(R.id.ll_content) ;
		title = (TextView) view.findViewById(R.id.title);
		content = (TextView) view.findViewById(R.id.content);
		input = (EditText) view.findViewById(R.id.input);
		positiveButton = (Button) view.findViewById(R.id.acceptbtn);
		negativeButton = (Button) view.findViewById(R.id.refusebtn);
		setContentView(view); // 设置view
	}
	
	public void setTitleVisable(){
		content.setVisibility(View.GONE);
		input.setVisibility(View.VISIBLE);
	}

	// 设置内容
	public void setTitle(String titleString) {
		title.setText(titleString);
	}

	// 设置内容
	public void setContent(String contentString) {
		content.setText(contentString);
	}

	public String getEdText() {
		return input.getEditableText().toString();
	}

	// 确定按钮监听
	public void setOnPositiveListener(View.OnClickListener listener) {
//		if (StringUtil.isNil(getEdText())&&(content.getVisibility()!=View.VISIBLE)) {
//			Toast.makeText(mcontext, "标记内容为空", 0).show();
//			return ;
//		}
		positiveButton.setOnClickListener(listener);
	}

	// 否定按钮监听
	public void setOnNegativeListener(View.OnClickListener listener) {
		negativeButton.setOnClickListener(listener);
	}
}
