package com.example.customcirclemenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomMenuDemo extends View{
	private Paint mPaint;
	private Paint mPbtn ;
	private Paint mPmenu,mPsele;
	//圆心，半径
	private float x,y,radius;
	private String[] menutext = {"名称1","名称2","名称3","名称4","名称5","名称6"};
	private int[] images = {R.drawable.home_btn_hot,R.drawable.home_btn_hot,R.drawable.home_btn_hot
			,R.drawable.home_btn_hot,R.drawable.home_btn_hot,R.drawable.home_btn_hot,};
	//每两个点间隔的角度   
	private int mDegreeDelta;
	//box列表   
	private BigBox[] mBox = new BigBox[menutext.length];;

	private Bitmap image;
	private int menu=R.drawable.btn_menu_normal;
	private int selectItem = 3;
	private boolean drawAdd = true,drawX = true;
	private float sX,sY;
	//幅度
	private float extent = 0.5f;
	public CustomMenuDemo(Context context){
		super(context);
	} 
	public CustomMenuDemo(Context context,AttributeSet attr) { 
		super(context,attr);
		// TODO Auto-generated constructor stub
	}
	public CustomMenuDemo(Context context,AttributeSet attr,int defStyle){
		super(context, attr, defStyle);
	}

	private void initPaint(){
		mPaint = new Paint();  
		mPaint.setAntiAlias(true); //消除锯齿  
		mPaint.setColor(Color.RED);
		mPaint.setStrokeWidth(45);
		mPaint.setStyle(Style.STROKE);

		mPbtn = new Paint();
		mPbtn.setAntiAlias(true);

		mPmenu = new Paint();
		mPmenu.setAntiAlias(true); //消除锯齿  
		mPmenu.setColor(Color.WHITE);
		mPmenu.setTextSize(10);

		mPsele = new Paint();
		mPsele.setStyle(Paint.Style.STROKE);
		mPsele.setAntiAlias(true);
		mPsele.setStrokeWidth(45);
		mPsele.setColor(0x880000FF);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		x = getWidth()/2;
		y = getHeight();

		if(mPaint==null||mPbtn==null||mPmenu==null||mPsele==null){
			initPaint();
		}
		System.out.println("-----------------------"+radius);
		//圆
		canvas.drawCircle(x, y, radius, mPaint);
		drawSelectImage(canvas,selectItem);
		if(mBox[0]!=null){
			setupBoxs();   
			computeCoordinates();   
			for(int index=0; index<menutext.length; index++) {   
				if(!mBox[index].isVisible) continue;   
				drawInCenter(canvas,mBox[index].angle, mBox[index].text,mBox[index].bitmap, mBox[index].x, mBox[index].y);   
			}
		}
		if(menu!=0){
			drawItemMenu(canvas);
		}
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		sX = event.getX();
		sY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//			System.out.println("按下");
			//圆
			if (sX<(getWidth()/2-image.getWidth()/2)||sX>(getWidth()/2+image.getWidth()/2)||sY<(getHeight()-image.getHeight())&&sY>getHeight()-radius) {
				if(radius>0){
					selectItem = itemCount(sX, sY);
					menu=R.drawable.btn_menu_selected_focused;
					invalidate();
				}
			}
			//菜单
			if(sX>(getWidth()/2-image.getWidth()/2)&&sX<(getWidth()/2+image.getWidth()/2)&&sY>(getHeight()-image.getHeight())){
				if(radius==0){
					menu=R.drawable.btn_menu_focused;
				}
				invalidate();
			}
		case MotionEvent.ACTION_UP:
			//圆形菜单
			if (sX<(getWidth()/2-image.getWidth()/2)||sX>(getWidth()/2+image.getWidth()/2)||sY<(getHeight()-image.getHeight())) {
				if(radius==0){
					
				}if(radius>0){
					menu=R.drawable.btn_menu_normal;
					new Thread(new LoadImage()).start();
					new Thread(new DrawImageX()).start();
					//					invalidate();
				}
			}
			//点
			if(sX>(getWidth()/2-image.getWidth()/2)&&sX<(getWidth()/2+image.getWidth()/2)&&sY>(getHeight()-image.getHeight())){
				if(radius>0){
					menu=R.drawable.btn_menu_normal;
					new Thread(new LoadImage()).start();
					new Thread(new DrawImageX()).start();
				}
				if(radius==0){
					menu=R.drawable.btn_menu_selected_normal;
					new Thread(new LoadImage()).start();
					new Thread(new DrawImageAdd()).start();
				}
			}
			break;
		}
		if(radius>0){
			onCharChange(selectItem);
		}
		return super.onTouchEvent(event);
	}
	class LoadImage implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			setupBoxs();   
			computeCoordinates();   
		}
	}
	class DrawImageAdd implements Runnable{

		@Override
		public void run() {

			while(drawAdd){
				drawMenuAdd();
			}
			drawAdd=true;
		}

	}
	class DrawImageX implements Runnable{

		@Override
		public void run() {

			while(drawX){
				drawMenuX();
			}
			drawX=true;
		}

	}

	private void setupBoxs() {   
		BigBox stone;   
		int angle = 12;   
		mDegreeDelta = 180/menutext.length;   

		for(int index=0; index<menutext.length; index++) {   
			stone = new BigBox();   
			stone.angle = angle;  
			stone.text = menutext[index];
			stone.bitmap = BitmapFactory.decodeResource(getResources(), images[index]);               
			angle += mDegreeDelta;   

			mBox[index] = stone;   
		}   
	}   
	/**  
	 * 计算每个点的坐标   
	 */  
	private void computeCoordinates() {   
		BigBox box;   
		for(int index=0; index<menutext.length; index++) {   
			box = mBox[index];   
			box.x = x+ (float)(radius * Math.cos(box.angle*Math.PI/180));   
			box.y = y- (float)(radius * Math.sin(box.angle*Math.PI/180));   
		}   
	}   

	class BigBox {   
		String text;
		//图片   
		Bitmap bitmap;   
		//角度   
		int angle;   
		//x坐标   
		float x;   
		//y坐标   
		float y;   
		//是否可见   
		boolean isVisible = true;   
	}  
	//点击阴影
	private void drawSelectImage(Canvas canvas,int item){
		//		Paint p2 = new Paint();
		//		mPaint = new Paint();

		RectF r1=new RectF(getWidth()/2-radius,getHeight()-radius,getWidth()/2+radius,getHeight()+radius);
		canvas.drawArc(r1, 330-30*item,30, false,mPsele);
		//			System.out.println("---------"+item);
	}

	//圆形菜单
	private void drawInCenter(Canvas canvas, int angle,String text,Bitmap bitmap, float left, float top) { 
		//		Paint p = new Paint();
		canvas.save();
		canvas.rotate(90-angle,left, top);
		canvas.drawText(text, left-bitmap.getWidth()/2 ,top+bitmap.getHeight()/2 ,mPmenu);
		canvas.drawBitmap(bitmap, left-bitmap.getWidth()*3/4, top-bitmap.getHeight()*3/4, mPmenu);   
		canvas.restore();
	}   
	//算出点击哪个图片
	private int itemCount(float xx,float yy){
		if(xx!=0&&yy!=0){
			Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.home_btn_hot);
			int w = image.getWidth();
			int h = image.getHeight();
			if(mBox!=null){
				for(int i = 0; i < mBox.length; i++ ){
					BigBox bb = mBox[i];
					if(xx>bb.x-w&&xx<bb.x+w&&yy>bb.y-h&&yy<bb.y+h){
						return i;
					}
				}
			}
		}
		return selectItem;
	}
	//点击键
	private void drawItemMenu(Canvas canvas){
		image = BitmapFactory.decodeResource(getResources(), menu);
		canvas.drawBitmap(image,getWidth()/2-image.getWidth()/2,getHeight()-image.getHeight(),mPbtn);
	}
	private void drawMenuAdd(){
	         	if(radius<(getWidth()/8*3)){
			radius+=extent;
		}
		//重新唤醒ondraw（）方法
		if(radius>0&&radius<=getWidth()/8*3){
			//			invalidate();
			postInvalidate();
		}
		if(radius>=getWidth()/8*3){
			drawAdd = false;
		}
	}
	private void drawMenuX(){
		if(radius!=0){
			radius-=extent;
		}
		//重新唤醒ondraw（）方法
		if(radius>=0&&radius<getWidth()/8*3){
			//			invalidate();
			postInvalidate();
		}
		if(radius<=0){
			drawX = false;
		}
	}
	
	protected void onCharChange(int c) {
		if (mOnCharChangeListener != null)
			mOnCharChangeListener.onCharChange(c);
	}

	private OnCharChangeListener mOnCharChangeListener;

	public void setOnCharChangeListener(OnCharChangeListener listener) {
		mOnCharChangeListener = listener;
	}
 
	public interface OnCharChangeListener {
		void onCharChange(int c);
	}
}
