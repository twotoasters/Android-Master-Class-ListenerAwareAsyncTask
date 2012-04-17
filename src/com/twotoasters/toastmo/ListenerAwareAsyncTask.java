package com.twotoasters.toastmo;

import android.os.AsyncTask;

public abstract class ListenerAwareAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	/**
	 * Our completion and progress listener. This is the thing that we'll call when things get
	 * completed.
	 * 
	 * @author bjdupuis
	 *
	 * @param <Progress>
	 * @param <Result>
	 */
	public interface OnCompleteListener<Progress, Result> {
		public void onComplete(Result result);
		
		public void onProgress(Progress... progress);
	}
	
	/**
	 * Constructor that registers a listener.
	 * 
	 * @param listener the listener to register.
	 */
	public ListenerAwareAsyncTask(OnCompleteListener<Progress, Result> listener) {
		this();
		
		register(listener);
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	final protected void onPostExecute(Result result) {
		if(_listener != null) {
			_listener.onComplete(result);
		} else {
			// save the result so we can defer calling the completion
			// listener when (and if) it re-registers
			_result = result;
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	final protected void onProgressUpdate(Progress... values) {
		if(_listener != null) {
			_listener.onProgress(values);
		}
	}

	/**
	 * Register a listener to be notified when the task completes and updates progress.
	 * Note that this must be called from the UI thread since it's possible the listener
	 * will be called right now.
	 * 
	 * @param listener the listener to call
	 */
	public void register(OnCompleteListener<Progress, Result> listener) {
		_listener = listener;
		
		// see if we had a deferred result available
		if(_result != null) {
			_listener.onComplete(_result);
			_result = null;
		}
	}
	
	/**
	 * Unregister the registered listener. If it's desirable for more than one listener 
	 * to be notified, it's trivial to have a list of listeners.
	 */
	public void unregister() {
		_listener = null;
	}
	
	//------------------------------------------------------------------------
	// END of public interface
	//------------------------------------------------------------------------
	// the completion listener we'll call.
	private OnCompleteListener<Progress, Result> _listener = null;
	
	// a temporary storage for the result.
	private Result _result = null;

	private ListenerAwareAsyncTask() {
	}
	
}
