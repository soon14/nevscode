package com.nevs.car.tools.view.safecode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;

import java.util.ArrayList;

/**
 * Dialog 支付密码键盘
 * 
 * @author LanYan
 * 
 */
@SuppressLint("InflateParams")
public class PayPasswordView implements OnClickListener {

	private RelativeLayout del;
	private ImageView point;

	private LinearLayout zero;
	private LinearLayout one;
	private LinearLayout two;
	private LinearLayout three;
	private LinearLayout four;
	private LinearLayout five;
	private LinearLayout sex;
	private LinearLayout seven;
	private LinearLayout eight;
	private LinearLayout nine;

	private TextView cancel;
	private TextView sure;
	private ImageView box1;
	private ImageView box2;
	private ImageView box3;
	private ImageView box4;
	private ImageView box5;
	private ImageView box6;
	private TextView content;
	private TextView finishText;
	private TextView payTitle;
	private RelativeLayout pin;
	private RelativeLayout pin3;


	private ArrayList<String> mList = new ArrayList<String>();
	private View mView;
	private OnPayListener listener;
	@SuppressWarnings("unused")
	private Context mContext;

	public PayPasswordView(int isback,String monney, Context mContext,
						   OnPayListener listener) {
		getDecorView(isback,monney, mContext, listener);
	}

	public static PayPasswordView getInstance(int isback,String monney, Context mContext,
											  OnPayListener listener) {
		return new PayPasswordView(isback,monney, mContext, listener);
	}

	public void getDecorView(int isback,String monney, Context mContext,
							 OnPayListener listener) {
		this.listener = listener;
		this.mContext = mContext;
		mView = LayoutInflater.from(mContext).inflate(
				R.layout.item_paypassword, null);
		findViewByid();
		setLintenter();
		//content.setText("消费金额：" + monney + "元");
		payTitle.setText(monney);
		if(isback==0){//0表示隐藏取消按钮
			finishText.setVisibility(View.GONE);
			pin.setVisibility(View.VISIBLE);
			pin3.setVisibility(View.GONE);
		}else {
			finishText.setVisibility(View.VISIBLE);
			pin.setVisibility(View.GONE);
			pin3.setVisibility(View.VISIBLE);
		}
	}

	private void findViewByid() {

		del = (RelativeLayout) mView.findViewById(R.id.pay_keyboard_del);// 删除键
		point = (ImageView) mView.findViewById(R.id.pay_keyboard_point);// 计算小数点(未做处理)
		pin= (RelativeLayout) mView.findViewById(R.id.pin);
		pin3= (RelativeLayout) mView.findViewById(R.id.pin3);


		// 键盘1-9
		zero = (LinearLayout) mView.findViewById(R.id.pay_keyboard_zero);
		one = (LinearLayout) mView.findViewById(R.id.pay_keyboard_one);
		two = (LinearLayout) mView.findViewById(R.id.pay_keyboard_two);
		three = (LinearLayout) mView.findViewById(R.id.pay_keyboard_three);
		four = (LinearLayout) mView.findViewById(R.id.pay_keyboard_four);
		five = (LinearLayout) mView.findViewById(R.id.pay_keyboard_five);
		sex = (LinearLayout) mView.findViewById(R.id.pay_keyboard_sex);
		seven = (LinearLayout) mView.findViewById(R.id.pay_keyboard_seven);
		eight = (LinearLayout) mView.findViewById(R.id.pay_keyboard_eight);
		nine = (LinearLayout) mView.findViewById(R.id.pay_keyboard_nine);

		// 输入框 TextView
		box1 = (ImageView) mView.findViewById(R.id.pay_box1);
		box2 = (ImageView) mView.findViewById(R.id.pay_box2);
		box3 = (ImageView) mView.findViewById(R.id.pay_box3);
		box4 = (ImageView) mView.findViewById(R.id.pay_box4);
		box5 = (ImageView) mView.findViewById(R.id.pay_box5);
		box6 = (ImageView) mView.findViewById(R.id.pay_box6);

		cancel = (TextView) mView.findViewById(R.id.pay_cancel);// 取消
		sure = (TextView) mView.findViewById(R.id.pay_sure);// 确定
		finishText = (TextView) mView.findViewById(R.id.finish_text);// quxiao
		payTitle = (TextView) mView.findViewById(R.id.pay_title);// quxiao
		//content = (TextView) mView.findViewById(R.id.pay_content);// 金额

	}

	private void setLintenter() {
		point.setOnClickListener(this);
		del.setOnClickListener(this);
		zero.setOnClickListener(this);
		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
		four.setOnClickListener(this);
		five.setOnClickListener(this);
		sex.setOnClickListener(this);
		seven.setOnClickListener(this);
		eight.setOnClickListener(this);
		nine.setOnClickListener(this);
		cancel.setOnClickListener(this);
		sure.setOnClickListener(this);
		finishText.setOnClickListener(this);
		del.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				parseActionType(KeyboardEnum.longdel);
				return false;
			}
		});
	}

	private void parseActionType(KeyboardEnum type) {
		// TODO Auto-generated method stub
		if (type.getType() == KeyboardEnum.ActionEnum.add) {
			if (mList.size() < 6) {
				mList.add(type.getValue());
				updateUi();
				if (mList.size() == 6) {
					String payValue = "";
					for (int i = 0; i < mList.size(); i++) {
						payValue += mList.get(i);
					}
					listener.onSurePay(payValue);
				}
			}

		} else if (type.getType() == KeyboardEnum.ActionEnum.delete) {
			if (mList.size() > 0) {
				mList.remove(mList.get(mList.size() - 1));
				updateUi();
			}
		}

		else if (type.getType() == KeyboardEnum.ActionEnum.longClick) {
			mList.clear();
			updateUi();
		}

		// else if (type.getType() == ActionEnum.cancel) {//取消按钮
		// listener.onCancelPay();
		// } else if (type.getType() == ActionEnum.sure) {//确定按钮
		// if (mList.size() < 6) {
		// Toast.makeText(mContext, "支付密码必须6位", Toast.LENGTH_SHORT).show();
		// } else {
		// String payValue = "";
		// for (int i = 0; i < mList.size(); i++) {
		// payValue += mList.get(i);
		// }
		// listener.onSurePay(payValue);
		// }
		// }

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == zero) {
			parseActionType(KeyboardEnum.zero);
		} else if (v == one) {
			parseActionType(KeyboardEnum.one);
		} else if (v == two) {
			parseActionType(KeyboardEnum.two);
		} else if (v == three) {
			parseActionType(KeyboardEnum.three);
		} else if (v == four) {
			parseActionType(KeyboardEnum.four);
		} else if (v == five) {
			parseActionType(KeyboardEnum.five);
		} else if (v == sex) {
			parseActionType(KeyboardEnum.sex);
		} else if (v == seven) {
			parseActionType(KeyboardEnum.seven);
		} else if (v == eight) {
			parseActionType(KeyboardEnum.eight);
		} else if (v == nine) {
			parseActionType(KeyboardEnum.nine);

		} else if (v == cancel) {
			parseActionType(KeyboardEnum.cancel);
		} else if (v == sure) {
			parseActionType(KeyboardEnum.sure);
		} else if (v == del) {
			parseActionType(KeyboardEnum.del);
		}else if (v == finishText) {
			listener.onCancelPay();
		}
	}

	/**
	 * 刷新UI
	 */
	private void updateUi() {
		// TODO Auto-generated method stub
		box1.setVisibility(4);
		box2.setVisibility(4);
		box3.setVisibility(4);
		box4.setVisibility(4);
		box5.setVisibility(4);
		box6.setVisibility(4);
		if (mList.size() == 0) {
		} else if (mList.size() == 1) {
			box1.setVisibility(0);
		} else if (mList.size() == 2) {
			box1.setVisibility(0);
			box2.setVisibility(0);
		} else if (mList.size() == 3) {
			box1.setVisibility(0);
			box2.setVisibility(0);
			box3.setVisibility(0);
		} else if (mList.size() == 4) {
			box1.setVisibility(0);
			box2.setVisibility(0);
			box3.setVisibility(0);
			box4.setVisibility(0);
		} else if (mList.size() == 5) {
			box1.setVisibility(0);
			box2.setVisibility(0);
			box3.setVisibility(0);
			box4.setVisibility(0);
			box5.setVisibility(0);
		} else if (mList.size() == 6) {
			box1.setVisibility(0);
			box2.setVisibility(0);
			box3.setVisibility(0);
			box4.setVisibility(0);
			box5.setVisibility(0);
			box6.setVisibility(0);

		}
	}

	public interface OnPayListener {
		void onCancelPay();

		void onSurePay(String password);
	}

	public View getView() {
		return mView;
	}

	public enum KeyboardEnum {
		one(ActionEnum.add, "1"), two(ActionEnum.add, "2"), three(
				ActionEnum.add, "3"), four(ActionEnum.add, "4"), five(
				ActionEnum.add, "5"), sex(ActionEnum.add, "6"), seven(
				ActionEnum.add, "7"), eight(ActionEnum.add, "8"), nine(
				ActionEnum.add, "9"), zero(ActionEnum.add, "0"), del(
				ActionEnum.delete, "del"), longdel(ActionEnum.longClick,
				"longclick"), cancel(ActionEnum.cancel, "cancel"), sure(
				ActionEnum.sure, "sure"), point(ActionEnum.add, ".");
		public enum ActionEnum {
			add, delete, longClick, cancel, sure
		}

		private ActionEnum type;
		private String value;

		private KeyboardEnum(ActionEnum type, String value) {
			this.type = type;
			this.value = value;
		}

		public ActionEnum getType() {
			return type;
		}

		public void setType(ActionEnum type) {
			this.type = type;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
