/*
 * Copyright [2017] [Dr. Jochen Raßler]
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
 * This class helps you to launch your <code>FiddlerFxApp</code> subclass.
 * 
 * See the description in <code>FiddlerFxApp</code> for usage. 
 * @see com.github.koshamo.fiddler.jfx.FiddlerFxApp
 * 
 * @author Dr. Jochen Raßler
 *
 */
public class FiddlerFxAppRunner implements Runnable {

	private Class<? extends FiddlerFxApp> fiddlerFxApp;
	private String[] args;
	
	/**
	 * Configure the runner to start the Gui
	 * @param fiddlerFxApp	the Gui class to be launched 
	 * @param args			normally these are the arguments provided by
	 * the main method
	 */
	public FiddlerFxAppRunner(Class<? extends FiddlerFxApp> fiddlerFxApp, String[] args) {
		this.fiddlerFxApp = fiddlerFxApp;
		this.args = args;
	}
	
	/* (non-Javadoc)
	 * starts the Gui class
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Application.launch(fiddlerFxApp, args);
	}

}
