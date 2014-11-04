package com.fic.foxsportsclips;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class VideoPlayer extends Activity {

	public final static String VIDEO_URL = "VIDEO_URL";
	private VideoView videoPlayerView;
	 private MediaController mController;
	 ProgressDialog pDialog;
	private ProgressBar progressBar;
	private static ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		 requestWindowFeature(Window.FEATURE_NO_TITLE);
//		    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//		                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_view);
		progressDialog = ProgressDialog.show(this, "", "Loading...", true);
		
		initalizeComponents();
	}
	private void initalizeComponents() {
		// TODO Auto-generated method stub
		this.videoPlayerView = (VideoView) findViewById(R.id.videoView);
		 mController = new MediaController(this);
		 this.videoPlayerView .setMediaController(mController);
		 this.progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		 this.progressBar.setVisibility(View.VISIBLE);
		
	        
	        Bundle extras = getIntent().getExtras();
//	        if(extras !=null) {
	            String value = extras.getString(VIDEO_URL);
//	        }
//	        
	        Uri video = Uri.parse(value);
           
	        this.videoPlayerView.setVideoURI(video);
	        
	        this.videoPlayerView.requestFocus();
	        this.videoPlayerView.setOnPreparedListener(new OnPreparedListener() {
	            // Close the progress bar and play the video
	    

				@Override
				public void onPrepared(MediaPlayer mp) {
					 progressBar.setVisibility(View.GONE);
					 
					mp.start();
					
					 mp.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {

		                    @Override
		                    public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
		                        // TODO Auto-generated method stub
		                        
		                        progressDialog.dismiss();
		                        mp.start();
		                    }
		                });
					
				}
	        });
	}

}
