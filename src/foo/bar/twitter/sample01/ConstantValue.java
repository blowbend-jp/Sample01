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

/**
 * define constant value
 * @author Hidehisa YOKOYAMA(Blowbend.jp)
 * @version 1.0.0
 * @since 1.0.0
 */
final class ConstantValue {
	private ConstantValue(){/* Don't create instance. */}
	
	//for Twitter
	static String CONSUMER_KEY = "";    // <-- set your consumer key.
	static String CONSUMER_SECRET = ""; // <-- set your consumer secret.
	static String PREFERENCE_NAME = "PrefSample01";
	static final String PREF_KEY_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TOKEN = "oauth_token";
	static final String CALLBACK_URL = "myapp://oauth";
	static final String IEXTRA_AUTH_URL = "auth_url";
	static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	static String HASH_TAG = "#BlowbendSample01";  //<-- change the word that you like.
}
