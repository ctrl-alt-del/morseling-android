package com.morseling;

public class MorseCodeEntry {

	private boolean press;
	private int duration;
	
	public MorseCodeEntry() {
		this.press = false;
		this.duration = 0;
	}

	/**
	 * @return the press
	 */
	public boolean isPress() {
		return this.press;
	}

	/**
	 * @param press the press to set
	 */
	public MorseCodeEntry setPress(boolean press) {
		this.press = press;
		return this;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return this.duration;
	}

	/**
	 * @param duration the duration to set in millisecond
	 */
	public MorseCodeEntry setDuration(int duration) {
		this.duration = duration;
		return this;
	}
	
	

}
