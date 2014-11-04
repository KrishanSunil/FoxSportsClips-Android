package com.fic.foxsportsclips;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;




import com.fic.models.ImageLoader;
import com.fic.models.Item;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends Activity implements OnItemClickListener {
	
	private AbsListView itemsListView;
	private ProgressBar progressBar;
	private ArrayList<Item> itemList;
	public final static String VIDEO_URL = "VIDEO_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (getResources().getString(R.string.screen_type_phone).equalsIgnoreCase("YES")) {
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
        
        initializeComponents();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    private void downloadData(){
    	URL feedUrl = null;;
		try {
			this.progressBar.setVisibility(View.VISIBLE);
			feedUrl = new URL("http://feed.theplatform.com/f/IgChJC/TestClips");
			
			if (!isNetworkAvailable()) {
				
				
				new AlertDialog.Builder(this)
			    .setTitle("Error")
			    .setMessage("Network unreachable , Please check your internet connectivity")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            
			        	return;
			        }
			     })
			     .show();
				
				return;
			}
			this.itemList = new ArrayList<Item>();
			new DownloadFeeds().execute(feedUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    private void initializeComponents() {
		
    	this.itemsListView = (AbsListView) findViewById(R.id.testClipListView);
    	this.itemsListView.setOnItemClickListener(MainActivity.this);
    	
    	this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
    	
    	
//    	downloadData();
    	
		
	}
    
    
    @Override
    protected void onResume() {
    	
    	super.onResume();
    	downloadData();
    }
    
    // ListView / GridView adapter
    
    private class MyArrayAdapter extends ArrayAdapter<Item>{

    	 public ImageLoader imageLoader;
    	 int devicewidth;
    	 int deviceHeight;
        public MyArrayAdapter(Context context, int resource,
               ArrayList<Item> itemLists) {
            super(context, resource, itemLists);   
            imageLoader=new ImageLoader(context);
            Display display = getWindowManager().getDefaultDisplay(); 
            devicewidth = display.getWidth();
            deviceHeight = display.getHeight();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.row, parent, false);
            
            Item displayItem = getItem(position);
            TextView textView = (TextView) view.findViewById(R.id.textView1);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
            
            if (getResources().getString(R.string.screen_type_phone).equalsIgnoreCase("NO")) {
            	 imageView.getLayoutParams().width = devicewidth/3;
                 imageView.getLayoutParams().height = deviceHeight/3;
			}else{
				 imageView.getLayoutParams().width = devicewidth;
                 imageView.getLayoutParams().height = deviceHeight/3;
			}
           
            textView.setText(displayItem.getTitle());
            imageLoader.DisplayImage(displayItem.getDefaultThumbnailUrl(), imageView);
            return view;
        }
    }
    
    
    // Download iTems from the feed
    
    private class DownloadFeeds extends AsyncTask<URL, Integer, Void> {

		protected Void doInBackground(URL... params) {
			XmlPullParserFactory pullParserFactory;
			try {
				pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				
				HttpURLConnection conn = (HttpURLConnection) params[0].openConnection();
//			    conn.setReadTimeout(10000 /* milliseconds */);
//			    conn.setConnectTimeout(15000 /* milliseconds */);
//			    conn.setRequestMethod("GET");
			    conn.setDoInput(true);
			    // Starts the query
			    conn.connect();
			   conn.getInputStream();

				    InputStream in_s =conn.getInputStream();;
			        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		            parser.setInput(in_s, null);

		            parseXML(parser);

			} catch (XmlPullParserException e) {

				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onProgressUpdate(Integer... progress) {
	         System.out.println(progress[0]);
	     }

	     protected void onPostExecute(Void v) {
	    	 System.out.println("Success");
	    	 
	    	 MyArrayAdapter adapter = new MyArrayAdapter(MainActivity.this, R.layout.row,itemList);
	    	 System.out.print(itemsListView);
	    	 itemsListView.setAdapter(adapter);
	    	 adapter.notifyDataSetChanged();
	    	 progressBar.setVisibility(View.GONE);
	     }
    	
    }
    
	private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		ArrayList<Item> Items = null;
        int eventType = parser.getEventType();
        Item currentItem = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                	Items = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item")){
                    	currentItem = new Item();
                    } else if (currentItem != null){
                        if (name.equalsIgnoreCase("title")){
                        	currentItem.setTitle(parser.nextText()); 
                        } else if (name.equalsIgnoreCase("description")){
                        	currentItem.setDescription(parser.nextText());
                        } else if (name.equalsIgnoreCase("plmedia:defaultThumbnailUrl")){
                        	currentItem.setDefaultThumbnailUrl(parser.nextText());
                        }  else if(name.equalsIgnoreCase("media:content")){
                        	
                        	currentItem.getMediaList().add(parser.getAttributeValue(null, "url"));
                        	
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && currentItem != null){
                    	this.itemList.add(currentItem);
                    } 
            }
            eventType = parser.next();
        }

        
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Item clickedItem = this.itemList.get(position); 
		
		if (clickedItem==null) {
			return;
		}
		
		if (clickedItem.getMediaList().isEmpty()) {
			return;
		}
		
		new ProcessSMILFile().execute(clickedItem.getMediaList().get(0) + "&manifest=m3u");
		
		
		
	}
	
	
	// SMIL File Processing AsyncTask
	
	private class ProcessSMILFile extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			
//			Document doc = null;
		
//				doc = (Document) Jsoup.connect(params[0]).get();
			
	                URL u = null;
	                try {
	                    u = new URL(params[0]);
	                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
	                    c.setRequestMethod("GET");
	                    c.connect();
	                    InputStream in = c.getInputStream();
	                    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
	                    byte[] buffer = new byte[1024];
	                    in.read(buffer); // Read from Buffer.
	                    bo.write(buffer); // Write Into Buffer.
	                    
	                    InputStream isFromFirstData = new ByteArrayInputStream(bo.toByteArray()); 
	                    try{
	                    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    				DocumentBuilder builder = factory.newDocumentBuilder();
		    				Document doc =builder.parse(isFromFirstData);
		    				Element root = doc.getDocumentElement();
		    				
		    				NodeList videos = root.getElementsByTagName("video");
		    				Log.i("MainActivityTAG", "videos item : " + videos.getLength());
		    				
		    				int nVideo = videos.getLength();
		    				if (nVideo > 0) {
		    					
		    					
		    					
		    					for (int i = 0; i < nVideo; i++) {
		    						Element vnode = (Element) videos.item(i);
		    						String vlink = vnode.getAttribute("src");
		    						return vlink;
		    						
		    						
		    					}
		    				
	                    }
	                    }catch(Exception e){
	                    	e.printStackTrace();
	                    }
	    				

	                    

	                } catch (MalformedURLException e) {
	                    e.printStackTrace();
	                } catch (ProtocolException e) {
	                    e.printStackTrace();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                
	                finally{
	                	
	                }
		

				

			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			super.onPostExecute(result);
			
			if (result!=null) {
				
				Intent videoPlayerIntent = new Intent(MainActivity.this, VideoPlayer.class);
				videoPlayerIntent.putExtra(VIDEO_URL, result);
				startActivity(videoPlayerIntent);
			}
		}
		
	}
}
