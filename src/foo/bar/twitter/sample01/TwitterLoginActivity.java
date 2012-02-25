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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * twitter OAuth screen
 * @author Hidehisa YOKOYAMA(Blowbend.jp)
 * @version 1.0.0
 * @since 1.0.0
 */
public class TwitterLoginActivity extends Activity {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_login);
		
		WebView webView = (WebView) findViewById(R.id.twitterlogin);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				
				if (url != null && url.startsWith(ConstantValue.CALLBACK_URL)) {
					String[] urlParameters = url.split("\\?")[1].split("&");
					String oauthVerifier = "";

					if (urlParameters[0].startsWith("oauth_verifier")) {
						oauthVerifier = urlParameters[0].split("=")[1];
					} else if (urlParameters.length > 1 && urlParameters[1].startsWith("oauth_verifier")) {
						oauthVerifier = urlParameters[1].split("=")[1];
					}else{
						;//no process
					}

					Intent intent = getIntent();
					intent.putExtra(ConstantValue.IEXTRA_OAUTH_VERIFIER, oauthVerifier);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			}
		});
		webView.loadUrl(this.getIntent().getExtras().getString("auth_url"));
	}

}
