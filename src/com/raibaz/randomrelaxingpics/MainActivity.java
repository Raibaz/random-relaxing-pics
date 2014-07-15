package com.raibaz.randomrelaxingpics;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.raibaz.randomrelaxingpics.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
	
	
	private String currentUrl = "";
	
    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }
                        
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });
        
        addAdView();
                
        findViewById(R.id.dummy_button).setOnClickListener(refetchListener);
    }
    
    private void addAdView() {
    	AdView mAdView = new AdView(this);        
        mAdView.setAdUnitId(getResources().getString(R.string.ad_unit_id));
        mAdView.setAdSize(AdSize.BANNER);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.fullscreen_content_controls);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        		);
        params.addRule(RelativeLayout.BELOW, R.id.dummy_button);        
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layout.addView(mAdView, params);
        mAdView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);                                                                                 
        fetchImage();
    }
    
    private void fetchImage() {
    	try {    		    		
    		new FetcherWithLoader(this.getApplicationContext()).execute();	        
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    private class FetcherWithLoader extends ImageFetcher {
    	
    	public FetcherWithLoader(Context context) {
    		this.progress = new ProgressDialog(MainActivity.this);
    	}
    	
    	@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);    		
    		if(e != null) {
    			Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
    			return;
    		}
    		final WebView imgView = (WebView)findViewById(R.id.fullscreen_content);
    		imgView.setVisibility(View.VISIBLE);
    		imgView.loadData(result, "text/html", "UTF-8");
    		if(progress.isShowing()) {
    			progress.dismiss();
    		}
    	}
    }        	    
        
    View.OnClickListener refetchListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {			
			fetchImage();			
		}
	};    
}
