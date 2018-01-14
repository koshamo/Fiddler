/*
 * Copyright [2017] [Dr. Jochen Raﬂler]
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
package com.github.koshamo.fiddler.jfx;

import javafx.application.Application;

/**
 * @author jochen
 *
 */
public class FiddlerFxAppRunner implements Runnable {

	private Class<? extends FiddlerFxApp> fiddlerFxApp;
	private String[] args;
	
	public FiddlerFxAppRunner(Class<? extends FiddlerFxApp> fiddlerFxApp, String[] args) {
		this.fiddlerFxApp = fiddlerFxApp;
		this.args = args;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Application.launch(fiddlerFxApp, args);
	}

}
