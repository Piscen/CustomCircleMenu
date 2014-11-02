package com.example.customcirclemenu;

import com.example.customcirclemenu.CustomMenuDemo.OnCharChangeListener;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

public class MainActivity extends Activity {
	private CustomMenuDemo mCustomMenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCustomMenu = (CustomMenuDemo) findViewById(R.id.main_custommenu);
		if(mCustomMenu!=null){
			mCustomMenu.setOnCharChangeListener(listener);
		}else{
			Toast.makeText(getApplicationContext(), "mCustomMenuÎª¿Õ", Toast.LENGTH_LONG).show();
		}
	}
	
	
	private OnCharChangeListener listener = new OnCharChangeListener() {
		@Override
		public void onCharChange(int c) {
			Toast.makeText(getApplicationContext(), ""+c, Toast.LENGTH_SHORT).show();
		}
	};

}
