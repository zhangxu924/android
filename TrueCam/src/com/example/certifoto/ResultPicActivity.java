package com.example.certifoto;

import com.example.certifoto.R;
import com.example.certifoto.utils.BitMapUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultPicActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.resultpic_layout);
		String picFileName = getIntent().getStringExtra(Constants.EXTRA_PIC_FILE);
        String responseTxt = getIntent().getStringExtra(Constants.EXTRA_RESPONSE);
		ImageView picView = (ImageView) findViewById(R.id.pic_result_view);
		TextView resultTxtView= (TextView) findViewById(R.id.result_text_view);
//		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();  
//		bmpFactoryOptions.inSampleSize = 2;  
		//Bitmap bmp = BitmapFactory.decodeFile(picFileName, bmpFactoryOptions);  
		Bitmap bmp=BitMapUtils.loadBitmap(picFileName, true);
		picView.setImageBitmap(bmp);  
		// ivCameraSwitcher = (ImageView) findViewById(R.id.mc_facing_switcher);
		resultTxtView.setText(responseTxt);
		Button doneBtn=(Button)findViewById(R.id.done_btn);
		doneBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
		        case R.id.done_btn: {
		        	finish();
		        }
		        }
			}
		});
    }

	
	
	
}
