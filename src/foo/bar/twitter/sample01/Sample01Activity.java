/*
 * Copyright 2012 Hidehisa YOKOYAMA(Blowbend.jp)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package foo.bar.twitter.sample01;

import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * main screen
 * @author Hidehisa YOKOYAMA(Blowbend.jp)
 * @version 1.0.0
 * @since 1.0.0
 */
public class Sample01Activity extends Activity {
    private Button bt_start;
    private Button bt_cancel;
    private boolean isAuthorized;
    private Twitter twitter = null;
    private RequestToken requestToken = null;
    private TwitterStream twitterStream;
    private String token;
    private String tokenSecret;
    
    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //buttons
        bt_start = (Button)findViewById(R.id.stratButton);
        bt_cancel = (Button)findViewById(R.id.cancelButton);
    	bt_start.setEnabled(true);   //start button enabled
    	bt_cancel.setEnabled(false); //cancel button disabled
    	
    	//get OAuth token
    	SharedPreferences pref = getSharedPreferences(ConstantValue.PREFERENCE_NAME, MODE_PRIVATE);
    	token = pref.getString(ConstantValue.PREF_KEY_TOKEN, "");
    	tokenSecret = pref.getString(ConstantValue.PREF_KEY_SECRET, "");
    	
    	isAuthorized = (token.length() > 0 && tokenSecret.length() > 0);
    	twitterStream = new TwitterStreamFactory().getInstance();
    }
       	
    /**
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
    	//stop streaming API
    	if(isAuthorized){
			Log.d("DEBUG", "twitter stream cleanuped");
    		twitterStream.cleanUp();
    	}
    		
    	super.onBackPressed();
    }
    	
    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    		
    	menu.add(Menu.NONE, 0, 0, "Authorize");
    	return true;
    }
    	
    /**
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	super.onMenuItemSelected(featureId, item);
    		
    	switch(item.getItemId()){
    	case 0:
    		if(isAuthorized){
    			//authorized
    			new AlertDialog.Builder(this)
    			.setTitle("Logoff")
    			.setMessage("Do you want logoff twitter?")
    			.setPositiveButton("Logoff", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					SharedPreferences pref = getSharedPreferences(ConstantValue.PREFERENCE_NAME, MODE_PRIVATE);
    					SharedPreferences.Editor editor = pref.edit();
    					editor.remove(ConstantValue.PREF_KEY_TOKEN);
    					editor.remove(ConstantValue.PREF_KEY_SECRET);
    					token = "";
    					tokenSecret = "";
    					isAuthorized = false;
    				}
    			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					;//no process
    				}
    			}).create().show();
    		}else{
    			//not authorized
    			connectTwitter();
    		}
    		break;
   		}
    	return true;
    }
    	
    /**
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    		
        if (resultCode == RESULT_OK) {
        	super.onActivityResult(requestCode, resultCode, intent);
        	AccessToken accessToken = null;
        	try {
        		accessToken = twitter.getOAuthAccessToken(requestToken, intent.getExtras().getString(ConstantValue.IEXTRA_OAUTH_VERIFIER));
        		SharedPreferences pref = getSharedPreferences(ConstantValue.PREFERENCE_NAME, MODE_PRIVATE);
        		SharedPreferences.Editor editor = pref.edit();
        		token = accessToken.getToken();
        		tokenSecret = accessToken.getTokenSecret();
        		editor.putString(ConstantValue.PREF_KEY_TOKEN, token);
        		editor.putString(ConstantValue.PREF_KEY_SECRET, tokenSecret);
        		editor.commit();
        		Log.d("DEBUG", "authorized!!");
        		isAuthorized = true;
        	} catch (TwitterException e) {
        		e.printStackTrace();
        	}
        }
    }
    	
    /**
     * connect twitter
     */
    private void connectTwitter() {
      	ConfigurationBuilder confbuilder = new ConfigurationBuilder();
      	confbuilder.setOAuthConsumerKey(ConstantValue.CONSUMER_KEY);
       	confbuilder.setOAuthConsumerSecret(ConstantValue.CONSUMER_SECRET);
       	Configuration conf = confbuilder.build();

       	twitter = new TwitterFactory(conf).getInstance();
       	twitter.setOAuthAccessToken(null);

        try {
        	requestToken = twitter.getOAuthRequestToken(ConstantValue.CALLBACK_URL);
        	Intent intent = new Intent(this, TwitterLoginActivity.class);
        	intent.putExtra(ConstantValue.IEXTRA_AUTH_URL, requestToken.getAuthorizationURL());
        	this.startActivityForResult(intent, 0);
        } catch (TwitterException e) {
        	Toast.makeText(this, "Twitter Exception!!\n" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
        
    /**
     * start button clicked
     * @param v
     */
    public void onClickStartButton(View v) {
    	Log.d("DEBUG", "Start button clicked!");
    	
       	if(!isAuthorized){
       		Log.d("DEBUG", "not authorized");
       		return;
       	}
       	
    	bt_start.setEnabled(false); //start button disabled
    	bt_cancel.setEnabled(true); //cancel button enabled
    		
       	if(isAuthorized){
       		twitterStream = new TwitterStreamFactory().getInstance();
       		twitterStream.setOAuthConsumer(ConstantValue.CONSUMER_KEY, ConstantValue.CONSUMER_SECRET);
       		twitterStream.setOAuthAccessToken(new AccessToken(token, tokenSecret));
       		StatusListener listener = new StatusListener(){
    			@Override
    			public void onException(Exception arg0) {
    				;//no process
    			}

    			@Override
    			public void onDeletionNotice(StatusDeletionNotice arg0) {
    				;//no process
    			}

    			@Override
    			public void onScrubGeo(long arg0, long arg1) {
    				;//no process
    			}

    			@Override
    			public void onStatus(Status status) {
    				String tweet = status.getText();
    				Log.d("DEBUG", "tweet: "+tweet);
    			}

    			@Override
    			public void onTrackLimitationNotice(int arg0) {
    				;//no process
    			}
       		};
       		twitterStream.addListener(listener);
       		String[] trackArray = {ConstantValue.HASH_TAG};
       		twitterStream.filter(new FilterQuery(0, null, trackArray));
       	}
    }
    	
  	/**
  	 * cancel button pressed
   	 * @param v
   	 */
   	public void onClickCancelButton(View v) {
    	Log.d("DEBUG", "Cancel button clicked!");
    	
   		bt_start.setEnabled(true);   //start button enable
   		bt_cancel.setEnabled(false); //cancel button disable
    		
   		//stop streaming API
   		if(isAuthorized){
			Log.d("DEBUG", "twitter stream cleanuped");
   			twitterStream.cleanUp();
   		}
   	}
    	
}