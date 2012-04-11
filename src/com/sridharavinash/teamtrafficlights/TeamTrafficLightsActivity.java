package com.sridharavinash.teamtrafficlights;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;



public class TeamTrafficLightsActivity extends Activity {
	private final String RESTPROJECTSPATH="/httpAuth/app/rest/projects";
	private final String RESTBUILDTYPEPATH=RESTPROJECTSPATH+"/id:";
	private final String RESTBUILDSPATH = "/httpAuth/app/rest/buildTypes/id:";
	EditText teamCityUrl;
	EditText teamCityUser;
	EditText teamCityPass;
	
	// Button Listener
	private OnClickListener connectListener = new OnClickListener(){
		public void onClick(View v){
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			//Get user input data
			teamCityUrl = (EditText)findViewById(R.id.serverUrl);
			teamCityUser = (EditText)findViewById(R.id.user);
			teamCityPass = (EditText)findViewById(R.id.pass);
			
			getResponse(teamCityUrl.getText().toString().trim(),
						teamCityUser.getText().toString().trim(),
						teamCityPass.getText().toString(),
						RESTPROJECTSPATH);
		}
	};
	
	/** Convenience function to convert inputstream(like HttpResponse) to a string */
	private StringBuilder inputStreamToString(InputStream is) {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    try {
			while ((line = rd.readLine()) != null) { 
			    total.append(line); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    // Return full string
	    return total;
	}

	/**Make a call to the server with credentials and get a response */
	private void getResponse(String url, String user, String pass,String restPath){
		HttpHost targetHost = new HttpHost(url, 80, "http");
		
		HttpClient httpClient = new DefaultHttpClient();
		
		((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
				new AuthScope(targetHost.getHostName(),targetHost.getPort()), 
				new UsernamePasswordCredentials(user,pass));
		
		
		HttpGet httpGet = new HttpGet("http://"+url+ restPath);
		HttpResponse httpResponse;
		try{
			httpResponse = httpClient.execute(httpGet);
			StringBuilder resp = inputStreamToString(httpResponse.getEntity().getContent());
			
			
			if(restPath.contains(RESTBUILDTYPEPATH)){
				ArrayList<TeamCityBuilds> respXml = (ArrayList<TeamCityBuilds>) ParseXMLResponse(resp, new BuildsDataHandler());
				showBuildNameList(respXml);
			}
			else if(restPath.contains(RESTPROJECTSPATH)){
				ArrayList<TeamCityProject> respXml = (ArrayList<TeamCityProject>) ParseXMLResponse(resp, new TCDataHandler());
				showProjectList(respXml);
			}else{
				//gets you build name and status information
				ArrayList<TeamCityBuilds> respXml = (ArrayList<TeamCityBuilds>) ParseXMLResponse(resp, new BuildsDataHandler());
				showBuildStatusList(respXml);
			}
				
			
		}catch (ClientProtocolException e){
			e.printStackTrace();
			showAlertDialog("Oh Oh! We are having trouble connecting to the server with those credentials! Please recheck your url, username and password.");		   
		} catch (IOException e) {
			e.printStackTrace();
			showAlertDialog("Oh Oh! We are having trouble connecting to the server with those credentials! Please recheck your url, username and password.");
		}
	}
	
	/** Show a list of build statuses for project */
	private void showBuildStatusList(final ArrayList<TeamCityBuilds> projectList){
		ArrayList<String> myList = new ArrayList<String>();
		Iterator<TeamCityBuilds> iterator = projectList.iterator();
		while(iterator.hasNext()){
			try {
				SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.US);
				Date dateString = ISO8601DATEFORMAT.parse(iterator.next().startDate);

				myList.add(iterator.next().status + " - " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(dateString));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
		if(myList.isEmpty())
			myList.add("No Status for build!");
		
        ListView lv = new ListView(this);
        lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myList));
        setContentView(lv);
	}
	
	/** Show a list of build names for project */
	private void showBuildNameList(final ArrayList<TeamCityBuilds> projectList){
		ArrayList<String> myList = new ArrayList<String>();
		Iterator<TeamCityBuilds> iterator = projectList.iterator();
		while(iterator.hasNext()){
			myList.add(iterator.next().buildName);
		}
		if(myList.isEmpty())
			myList.add("No Builds for Project!");
		
        ListView lv = new ListView(this);
        lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myList));
        setContentView(lv);
        lv.setOnItemClickListener(new OnItemClickListener(){
        	@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				getResponse(teamCityUrl.getText().toString().trim(),
						teamCityUser.getText().toString().trim(),
						teamCityPass.getText().toString(),
						RESTBUILDSPATH+projectList.get(pos).buildId+"/builds");
			}
        	
        });
	}
	
	/** Show a list of projects from server */
	private void showProjectList(final ArrayList<TeamCityProject> projectList){
		ArrayList<String> myList = new ArrayList<String>();
		Iterator<TeamCityProject> iterator = projectList.iterator();
		while(iterator.hasNext()){
			myList.add(iterator.next().projectName);
		}
		if(myList.isEmpty())
			myList.add("No Projects!");
        ListView lv = new ListView(this);
        lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myList));
        setContentView(lv);
        lv.setOnItemClickListener(new OnItemClickListener(){
        	@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				Log.i("TeamCityActivity", projectList.get(pos).projectId);
				getResponse(teamCityUrl.getText().toString().trim(),
						teamCityUser.getText().toString().trim(),
						teamCityPass.getText().toString(),
						RESTBUILDTYPEPATH+projectList.get(pos).projectId);
				
			}
        	
        });
	}

	/** Parse response to get a list of available projects from the server
	 * @return */
	private ArrayList<?> ParseXMLResponse(StringBuilder response, IDataInterface myHandler){
		 SAXParserFactory spf = SAXParserFactory.newInstance();
		 try {
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			
			xr.setContentHandler((ContentHandler) myHandler);
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(response.toString()));
			xr.parse(is);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return myHandler.getData();
	}
	
	private void showAlertDialog(String message){
		 AlertDialog alertDialog = new AlertDialog.Builder(TeamTrafficLightsActivity.this).create();
		 alertDialog.setTitle("Erorr");
		 alertDialog.setMessage(message);
		 alertDialog.show();
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button connect = (Button)findViewById(R.id.Connect);
        connect.setOnClickListener(connectListener);
    }
}