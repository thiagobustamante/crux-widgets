/*
 * Copyright 2013 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.widgets.client.dialog;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 * @see org.cruxframework.crux.smartfaces.client.progress.Progress
 *
 */
@Deprecated
public class Progress implements HasAnimation, IsWidget
{
	public static final String DEFAULT_STYLE_NAME = "crux-Progress" ;

	private boolean isProgressShowing;
	private DialogBox dialog = null;
	private HTML messageLabel = null;

	public Progress()
    {
		dialog = new DialogBox(false, true);
		dialog.setStyleName(DEFAULT_STYLE_NAME);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(0);

		FocusPanel iconPanel = createIconPanel();
		horizontalPanel.add(iconPanel);
		this.messageLabel = createMessageLabel();
		horizontalPanel.add(this.messageLabel);
					
		dialog.add(horizontalPanel);
    }
	
	/**
	 * @see com.google.gwt.user.client.ui.HasAnimation#isAnimationEnabled()
	 */
	public boolean isAnimationEnabled()
	{
		return dialog.isAnimationEnabled();
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.HasAnimation#setAnimationEnabled(boolean)
	 */
	public void setAnimationEnabled(boolean animationEnabled)
	{
		dialog.setAnimationEnabled(animationEnabled);
	}	

	/**
	 * Gets the message to be displayed to the user
	 * @return the message
	 */
	public String getMessage()
	{
		return messageLabel.getText();
	}

	/**
	 * Sets the message to be displayed to the user
	 * @param message
	 */
	public void setMessage(SafeHtml message)
	{
		messageLabel.setHTML(message);
	}
	
	/**
	 * Sets the message to be displayed to the user
	 * @param message
	 */
	public void setMessage(String message)
	{
		messageLabel.setText(message);
	}

	public void show()
	{
		Screen.blockToUser("crux-ProgressDialogScreenBlocker");
		isProgressShowing = true;
		
		//if it's a touch device, then we should wait for virtual keyboard to get closed.
		//Otherwise the dialog message will not be properly centered in screen.  
		if(Screen.getCurrentDevice().getInput().equals(DeviceAdaptive.Input.touch))
		{
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() 
			{
				@Override
				public boolean execute() 
				{
					doShow();
					return false;
				}
			}, 1000);
		}
		else 
		{
			doShow();
		}
	}
	
	/**
	 * Show message dilaog. The dialog is centered and the screen is blocked for edition
	 */
	private void doShow()
	{
		try
		{
			dialog.center();
			dialog.show();
			isProgressShowing = false;
		}
		catch (Exception e)
		{
			Crux.getErrorHandler().handleError(e);
			Screen.unblockToUser();
		}
	}
	
	/**
	 * Hides the progress dialog
	 */
	public void hide()
	{
		//This makes sure that 'hide' will not be invoked before 'show'.
		//This generaly occurs when there is a short gap between show-hide (fast requests).
		Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() 
		{
			@Override
			public boolean execute() 
			{
				if(isProgressShowing)
				{
					return true;
				}
				dialog.hide();
				Screen.unblockToUser();		
				return false;
			}
		}, 500);
	}
	
	@Override
    public Widget asWidget()
    {
	    return dialog;
    }

	/**
	 * 
	 * @param styleName
	 */
	public void setStyleName(String styleName)
	{
		dialog.setStyleName(styleName);
	}
	
	/**
	 * 
	 * @param width
	 */
	public void setWidth(String width)
	{
		dialog.setWidth(width);
	}

	/**
	 * 
	 * @param height
	 */
	public void setHeight(String height)
	{
		dialog.setHeight(height);
	}
	
	/**
	 * 
	 * @param title
	 */
	public void setTitle(SafeHtml title)
	{
		dialog.setHTML(title);
	}
	
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title)
	{
		dialog.setTitle(title);
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle()
	{
		return dialog.getTitle();
	}
	
	/**
	 * Creates a panel to display a icon for the message 
	 * @return a panel
	 */
	private FocusPanel createIconPanel()
	{
		FocusPanel iconPanel = new FocusPanel();
		iconPanel.setStyleName("icon");
		return iconPanel;
	}

	/**
	 * Creates a label to display the message 
	 * @param data
	 * @return a label
	 */
	private HTML createMessageLabel()
	{
		HTML label = new HTML();
		label.setStyleName("message");
		return label;
	}

	/**
	 * Shows a progress dialog
	 * @param message the text to be displayed in the body of the message box 
	 */
	public static Progress show(String message)
	{
		return show(message, DEFAULT_STYLE_NAME, false);
	}
	
	/**
	 * Shows a progress dialog
	 * @param message the text to be displayed in the body of the message box
	 * @param styleName the name of the CSS class to be applied in the message box element
	 * @param animationEnabled true to enable animations while showing or hiding the message box
	 */
	public static Progress show(String message, String styleName, boolean animationEnabled)
	{
		Progress progressDialog = new Progress(); 
		progressDialog.setMessage(message);
		progressDialog.setStyleName(styleName);
		progressDialog.setAnimationEnabled(animationEnabled);
		progressDialog.show();
		return progressDialog;
	}
}
