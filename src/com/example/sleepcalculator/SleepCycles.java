package com.example.sleepcalculator;

import java.sql.Time;
import java.util.Calendar;
import java.util.EventListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class SleepCycles extends Fragment {

	OnTimeChangedListener otc;
	TimePicker tp;
	
	public static String TYPE = "tabnumber";

	private int type;

	LinearLayout col1;
	LinearLayout col2;
	LinearLayout col3;

	AlarmButton alarmButtons[] = new AlarmButton[7];
	
	public interface onTimeChangedListener {
		public void myonTimeChanged(Long time);
	}

	@Override
    public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			otc= (OnTimeChangedListener) activity;
		}catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.full_cycles, container, false);


		type = getArguments().getInt(TYPE);

		col1 = (LinearLayout) rootView.findViewById(R.id.col1);
		col2 = (LinearLayout) rootView.findViewById(R.id.col2);
		col3 = (LinearLayout) rootView.findViewById(R.id.col3);

		populateButtons();

		TextView tv = (TextView) rootView.findViewById(R.id.textView1);
		tv.setText("If I go to bed at:");
		
		tp = (TimePicker) rootView.findViewById(R.id.timePicker1);
//		tp.setBackgroundColor(Color.WHITE);
		tp.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hour, int minute) {
				otc.onTimeChanged(view, hour, minute);
				setTimes();

			}
		});

		TextView tv2 = (TextView) rootView.findViewById(R.id.textView2);
		tv2.setText("Set the alarm for:");

		setTimePicker(MainActivity.hrs, MainActivity.min);

		setTimes();

		return rootView;

	}
	
	

	// initialize buttons for alarm times
	public void populateButtons() {
		int counter = 0;

		for (int i = 0; i < alarmButtons.length; i++) {
			alarmButtons[i] = new AlarmButton(super.getActivity());
			alarmButtons[i].setOnClickListener(new AlarmButtonListener());
			alarmButtons[i].setVisibility(View.GONE);

			counter++;
			if (counter == 1) {
				col1.addView(alarmButtons[i]);
			} else if (counter == 2) {
				col2.addView(alarmButtons[i]);
			} else {
				col3.addView(alarmButtons[i]);
				counter = 0;
			}
		}
	}

	public void setTimes() {
		if (type == 1)
			// increment by 1.5 hr intervals
			incrementTime(1, 30, 7);
		else
			// increment by 30 min intervals
			incrementTime(0, 30, 3);
	}

	protected void incrementTime(int hours, int minutes, int repetitions) {
		Calendar c = Calendar.getInstance();
		// set calendar to time picker
		c.set(c.YEAR, c.MONTH, c.DAY_OF_MONTH, tp.getCurrentHour(),
				tp.getCurrentMinute(), c.SECOND);
		Long time = c.getTimeInMillis();

		// assume user will fall asleep in 10 mintues
		time += 10 * 60 * 1000;
		

		String period = "";

		for (int i = 0; i < repetitions; i++) {
			// increment the time in milliseconds
			int incrementValue = hours * 60 * 60 * 1000 + minutes * 60 * 1000;
			time += Long.valueOf(incrementValue);
			alarmButtons[i].time=time;

			// set new time in milliseconds, and parse back hours+seconds,
			// correcting for periodicity and single digit minutes
			Time t = new Time(time);
			int hrs = t.getHours();
			
			if(hrs<12){
				if (hrs == 0)
					hrs = 12;
				period = "am";
			}
			else{
				if (hrs > 12)
					hrs -= 12;
				period = "pm";
			} 

			String min = t.getMinutes() < 10 ? ("0" + t.getMinutes()) : ("" + t
					.getMinutes());

			// times is label for button
			String times = hrs + ":" + min + period;
			alarmButtons[i].setVisibility(View.VISIBLE);
			alarmButtons[i].setText(times);
		}
	}
	
	//get hours from epoc mili
	public int getHours(Long mili){
		return new Time(mili).getHours();
	}
	//get minutes from epoc mili
	public int getMinutes(Long mili){
		return new Time(mili).getMinutes();
	}
	public void setTimePicker(int hrs, int min){
		tp.setCurrentHour(hrs);
		tp.setCurrentMinute(min);
	}

	private class AlarmButton extends Button {
		Long time;
		public AlarmButton(Context context) {
			super(context);
		}
	}
	private class AlarmButtonListener implements OnClickListener {
	//	public AlarmButtonListener() {}
		Long time;
		@Override
		public void onClick(View v) {
			time = ((AlarmButton) v).time;
			// set alarm for 'alarmTime'
			Toast.makeText(getActivity(), ((AlarmButton)v).getText().toString(),
					Toast.LENGTH_SHORT).show();
			Intent i = new Intent(AlarmClock.ACTION_SET_ALARM); 
			i.putExtra(AlarmClock.EXTRA_MESSAGE, "New Alarm"); 
			//i.putExtra(AlarmClock.ACTION_SET_ALARM, time);
			i.putExtra(AlarmClock.EXTRA_HOUR, getHours(time)); 
			i.putExtra(AlarmClock.EXTRA_MINUTES, getMinutes(time)); 
			startActivity(i); 
		}
	}
}

// on switch, update the 2nd time picker
// disable am/pm switcher