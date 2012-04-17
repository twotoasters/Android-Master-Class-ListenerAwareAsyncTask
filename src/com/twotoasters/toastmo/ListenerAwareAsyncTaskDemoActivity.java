package com.twotoasters.toastmo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.twotoasters.toastmo.ListenerAwareAsyncTask.OnCompleteListener;

public class ListenerAwareAsyncTaskDemoActivity extends Activity {

	static ListenerAwareAsyncTask<Integer, Integer, Void> _myTask = null;
	private ProgressBar _progressBar = null;
	
	private OnCompleteListener<Integer, Void> _myTaskListener = new OnCompleteListener<Integer, Void>() {

		@Override
		public void onComplete(Void result) {
			Toast.makeText(ListenerAwareAsyncTaskDemoActivity.this, "Done!", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProgress(Integer... progress) {
			_progressBar.setProgress(progress[0]);
		}
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		_progressBar = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_progressBar = (ProgressBar)findViewById(R.id.progressBar1);

		Button button = (Button)findViewById(R.id.switch_button);
		button.setText(R.string.switch_to_bad);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ListenerAwareAsyncTaskDemoActivity.this, DontUseAsyncTaskLikeThisActivity.class);
				startActivity(i);
				finish();
			}
		});
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		if(_myTask != null) {
			_myTask.unregister();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		if(_myTask == null) {
			_myTask = new ListenerAwareAsyncTask<Integer, Integer, Void>(this, _myTaskListener) {

				@Override
				protected Void doInBackground(Integer... params) {
					int totalTime = params[0];
					int count = 0;
					
					while(count <= totalTime && !isCancelled()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// don't care!
						}
						
						this.publishProgress((int)(((float)count++ / (float)totalTime) * 100f));
					}
					return null;
				}
			};
			_myTask.execute(120);
		} else {
			_myTask.register(this, _myTaskListener);
		}
	}

}