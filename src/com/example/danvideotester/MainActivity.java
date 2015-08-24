package com.example.danvideotester;

import com.example.danvideotester.WebViewFragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.app.DownloadManager;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends FragmentActivity {
	
	static private final String URL = "http://23.253.105.192/";
	static private final String TAG = "DanVideoTester";
	static private final String DIR = Environment.getExternalStorageDirectory() + "/Download/"+TAG;
	static private final String VideoKey = "VIDKEY";
	
	
	static private final int NUM_PAGES = 2;

    MyAdapter mAdapter;
    ViewPager mPager;
    static private File APP_DIR;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);
        
        APP_DIR = getApplicationContext().getFilesDir();
        
        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
    }
    
    
    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);            
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Fragment getItem(int position) {
        	Log.i(TAG, "POSITION "+position);
        	if(position == 0){
        		return VidViewFragment.getInstance(URL);
        	}
            return ArrayListFragment.newInstance(position);
        }
    }
    
    //THIS HANDLES THE FIRST FRAGMENT WE ENCOUNTER
    //IT DISPLAYS THE WEBVIEW AND SETS THINGS LIKE WHAT TO DO WHEN DOWNLOADING
    public static class VidViewFragment extends WebViewFragment {
    	private String mURL; 
    	
	    public static VidViewFragment getInstance(String url) { 
	    	VidViewFragment f = new VidViewFragment(); 
	        Bundle args = new Bundle(); 
	        args.putString("url", url); 
	        f.setArguments(args); 

	        return f; 
	    } 

	    @Override 
	    public void onCreate(Bundle savedInstanceState) { 
	        super.onCreate(savedInstanceState); 
	        setRetainInstance(true); 
	        mURL = getArguments().getString("url"); 
	    }
	    
	    @Override 
	    public void onActivityCreated(Bundle savedInstanceState) { 
	        super.onActivityCreated(savedInstanceState); 
	        WebView webView = getWebView();
	        if (webView != null) { 
	            if (webView.getOriginalUrl() == null) {
	        		webView.clearCache(true);
	                webView.getSettings().setJavaScriptEnabled(true); 
	        		webView.setWebViewClient(new WebViewClient());
	        		webView.setWebChromeClient(new WebChromeClient());
	        		
	        		
	        		webView.setDownloadListener(new DownloadListener() {       

	        			public void onDownloadStart(String url, String userAgent,
	        			                                    String contentDisposition, String mimetype,
	        			                                    long contentLength) {
	        			            DownloadManager.Request request = new DownloadManager.Request(
	        			                    Uri.parse(url));
	        			            
	        			            String[] urlSplit = url.split("/");
	        			            String filePath = TAG +"/"+ urlSplit[urlSplit.length-1];
	        			            
	        			            Log.i(TAG, "Creating Directory" + filePath);
	        			            File folder = new File(DIR);
	        			            boolean success = true;
	        			            if (!folder.exists()) {
	        			                success = folder.mkdir();
	        			            }
	        			            if (success) {
	        			                // Do something on success
	        				            request.allowScanningByMediaScanner();
	        				            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
	        				            //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filePath);
	        				            DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
	        				            dm.enqueue(request);
	        				            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //This is important!
	        				            intent.addCategory(Intent.CATEGORY_OPENABLE); //CATEGORY.OPENABLE
	        				            //intent.setType("* / *");//GET RID OF SPACES IN SET TYPEany application,any extension
	        				            Toast.makeText(getActivity().getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
	        				                    Toast.LENGTH_LONG).show();
	        			            } else {
	        			                // Do something else on failure 
	        			            }
	        			            


	        			        }
	        			    });
	        		
	                webView.loadUrl(mURL); 
	            } 
	        } 
	    } 
        
    }

    
    
    
    //THIS HANDLES WHAT HAPPENS WHEN YOU SWIPE LEFT
    //IT POPULATES AND DECIDES WHAT HAPPENS WHEN YOU CLICK A LIST ELEMENT
    
    public static class ArrayListFragment extends ListFragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
            ((TextView)tv).setText("Video List");
            
            Button refresh = (Button)v.findViewById(R.id.refresh);
            refresh.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	List<String> fileNames = getFileNames();
                    setListAdapter(new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, fileNames));
                }
            });
            
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            
            List<String> fileNames = getFileNames();
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, fileNames));
        }
        
        private List<String> getFileNames() {
        	List<String> fileNames = new ArrayList<String>();
        	
        	File folder = new File(DIR);
            boolean success = true;
            
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            else {
            	File f = new File(DIR);
                File[] files = f.listFiles();                
                for(int i=0; i<files.length; i++){
                	fileNames.add(files[i].getName());
                }
            }            
            return fileNames;
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            String file = (String)l.getItemAtPosition(position);
            String path = DIR + "/" + file;
            Log.i(TAG, "PATH CHOSEN: " + path);            
            View mainView = (View)((View)((View)v.getParent()).getParent()).getParent();
            
            VideoView video=(VideoView) mainView.findViewById(R.id.video1);
            MediaController mediaController = new MediaController(getActivity());
            mediaController.setAnchorView(video);
            video.setMediaController(mediaController);
            video.setKeepScreenOn(true);
            video.setVideoPath(path);
            video.start();
            video.requestFocus();
            
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
