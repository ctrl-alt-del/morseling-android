package com.morseling;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;

public class MorseCodeConverter {

	ToneGenerator tg;
	Activity activity;

	public MorseCodeConverter(Activity activity) {
		this.activity = activity;
		this.tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
	}

	public void beep(int type) throws InterruptedException {
		switch (type) {
		case 0: // "."
			tg.startTone(ToneGenerator.TONE_PROP_BEEP);
			java.lang.Thread.sleep(25);
			break;
		case 1: // " "
			java.lang.Thread.sleep(25);
			break;
		case 2: // "-"
			tg.startTone(ToneGenerator.TONE_PROP_BEEP);
			java.lang.Thread.sleep(50);
			break;
		case 4: // "/"
			java.lang.Thread.sleep(100);
			break;
		default:
			break;
		}
	}

	public void beepString(String input) throws InterruptedException {
		input = input.replace(".","0").replace(" ","1").replace("-","2").replace("/","3");
		
		for (int i= 0; i < input.length(); i++) {
			int type = Integer.valueOf((input.charAt(i)+""));
			beep(type);
		}
	}

}
