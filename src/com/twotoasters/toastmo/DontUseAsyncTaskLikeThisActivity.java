package com.twotoasters.toastmo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class DontUseAsyncTaskLikeThisActivity extends Activity {

	private ProgressBar _progressBar = null;
	private static AsyncTask<Integer, Integer, Void> _myTask;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		_progressBar = null;
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		
		Button button = (Button)findViewById(R.id.switch_button);
		button.setText(R.string.switch_to_good);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DontUseAsyncTaskLikeThisActivity.this, ListenerAwareAsyncTaskDemoActivity.class);
				startActivity(i);
				finish();
			}
		});
		
		if(_myTask == null) {
			_myTask = new AsyncTask<Integer, Integer, Void>() {
	
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
	
				/* (non-Javadoc)
				 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
				 */
				@Override
				protected void onPostExecute(Void result) {
					Toast.makeText(DontUseAsyncTaskLikeThisActivity.this, "Done!", Toast.LENGTH_LONG).show();
				}
	
				/* (non-Javadoc)
				 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
				 */
				@Override
				protected void onProgressUpdate(Integer... progress) {
					// oh dear
					_progressBar.setProgress(progress[0]);
				}
			};
		
			_myTask.execute(120);
		}
		
	}

}
