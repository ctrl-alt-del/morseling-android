package com.morseling;

import java.util.LinkedList;

import android.app.Activity;

public class MorseCodeMessage {

	Activity activity;
	LinkedList<MorseCodeEntry> parsedMorseCode;
	
	
	public MorseCodeMessage(Activity activity) {
		this.activity = activity;
		this.resetMessage();
	}
	
	public void resetMessage() {
		
		if (parsedMorseCode != null) {
			this.parsedMorseCode = null;
		}
		
		this.parsedMorseCode = new LinkedList<MorseCodeEntry>();
	}
	
	public void parseRawMorseCodeToMeesage() {
		
	}
	
	public void appendMessage() {}

}
