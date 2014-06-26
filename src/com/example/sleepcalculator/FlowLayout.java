package com.example.sleepcalculator;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;

public class FlowLayout extends RelativeLayout {
	int maxWidth;
	int maxRowHeight=0;
	
	int currWidth=0;
	
	public FlowLayout(Context context) {
		super(context);
		maxWidth = this.getWidth();
	}
	
	public void addThing(View child){
		if(currWidth + child.getHeight() + 10 < maxWidth){
			//add in horizontal
			super.addView(child);
			if(child.getHeight() > maxRowHeight){
				maxRowHeight = child.getHeight();
			}
		}
		else{
			//add in another row
			super.addView(child);
		}
	}

}
