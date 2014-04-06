/*
 *  QDWizard
 *  Copyright (C) Bertrand Florat and others
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  
 */
package org.qdwizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

/**
 * A Wizard dialog displaying one or more screens
 * <ul>
 * <li>Create a class that extends Wizard. You have to implement
 * getPreviousScreen(), getNextScreen() and finish() abstract methods</li>
 * <li>Displaying the wizard:</li>
 * 
 * <pre>
 * {
 * 	@{code
 * 	MyWizard wizard = new Wizard(new Wizard.Builder(&quot;wizard name&quot;, ActionSelectionPanel.class,
 * 			window).hSize(600).vSize(500).locale(LocaleManager.getLocale()).icon(anIcon));
 * 	wizard.show();
 * }
 * </pre>
 * 
 * <li>{@code finish()} method implements actions to be done at the end of the
 * wizard</li>
 * <li>{@code getPreviousScreen()} and {@code getNextScreen()} have to return
 * previous or next screen class. Example:</li>
 * 
 * <pre>
 * {@code
 * public Class getNextScreen(Class screen) {
 *   if (ActionSelectionPanel.class.equals(getCurrentScreen())) {
 *     String sAction = (String) data.get(KEY_ACTION);
 *     if (ActionSelectionPanel.ACTION_CREATION.equals(sAction)) {
 *       return TypeSelectionPanel.class;
 *     } 
 *     else if (ActionSelectionPanel.ACTION_DELETE.equals(sAction)) {
 *       return RemovePanel.class;
 *     }
 *   }
 * }
 * </pre>
 * 
 * <li>Get and set wizard data using the 'data' map available from wizard and screen classes. This is a HashMap<Object,Object> so you can use anything as a key. <br/>
 * A good practice is to create an enum in the Wizard class and use to enum entry as key for the data map :
<pre>
{@code
public Class MyWizard extends Wizard {
	enum Variable {VARIABLE_1,VARIABLE_2}
	...
	void someMethod(){
		data.put(Variable.VARIABLE1,"a fine example String");
	}
}

public Class MyScreen extends Screen {
	void someMethod(){
		String var1 = data.get(Variable.VARIABLE_1);
	}
}
}
</pre>
 * </li>
 * 
 * </ul>
 */
public abstract class Wizard extends WindowAdapter implements ActionListener {
	private final String name;
	private Screen current;
	private final Class<? extends Screen> initial;
	/** Wizard data. */
	protected final Map<Object, Object> data = new HashMap<Object, Object>(10);
	/** Wizard header. */
	private Header header;
	/** Wizard action Panel. */
	private ActionsPanel actions;
	/** Wizard dialog. */
	private JDialog dialog;
	private final Image leftSideImage;
	private final Frame parentWindow;
	private final int horizontalSize;
	private final int verticalSize;
	private final Logger logger = Logger.getAnonymousLogger();
	/** Screens instance repository. */
	private final Map<Class<? extends Screen>, Screen> hmClassScreens = new HashMap<Class<? extends Screen>, Screen>(
			10);
	/** Default Wizard size. */
	protected static final int DEFAULT_H_SIZE = 700;
	/** The Constant DEFAULT_V_SIZE. */
	protected static final int DEFAULT_V_SIZE = 500;
	/** Default horizontal padding. */
	protected static final int DEFAULT_H_LAYOUT_PADDING = 5;
	/** Default vertical padding. */
	protected static final int DEFAULT_V_LAYOUT_PADDING = 5;
	/** Was the Wizard Canceled?. */
	private boolean bCancelled;
	private final int layoutHPadding;
	private final int layoutVPadding;

	/**
	 * Fluent-API style Wizard Builder
	 */
	public static class Builder {
		// Mandatory fields
		private final String name;
		/** Initial screen class */
		private final Class<? extends Screen> initial;
		/** Parent window. */
		private final Frame parentWindow;
		// Optional fields
		private ImageIcon icon;
		private Image headerBackgroundImage;
		private Image leftSideImage;
		private int horizontalSize = -1;
		private int verticalSize = -1;
		private int layoutHPadding = -1;
		private int layoutVPadding = -1;
		private Locale locale;

		/**
		 * 
		 * @param name
		 *            Wizard name displayed in the frame title
		 * @param initial
		 *            initial screen to display
		 * @param parentWindow
		 *            wizard parent window
		 */
		public Builder(String name, Class<? extends Screen> initial, Frame parentWindow) {
			this.name = name;
			this.initial = initial;
			this.parentWindow = parentWindow;
		}

		/**
		 * Set the header left-side icon
		 * 
		 * @param icon
		 *            header left-side icon
		 * @return the wizard builder
		 */
		public Builder icon(ImageIcon icon) {
			this.icon = icon;
			return this;
		}

		/**
		 * Set the background image
		 * 
		 * @param image
		 *            image displayed in the header
		 * @return the wizard builder
		 */
		public Builder headerBackgroundImage(Image image) {
			this.headerBackgroundImage = image;
			return this;
		}

		/**
		 * Set the left-side image
		 * 
		 * @param image
		 *            -side image displayed in the wizard body
		 * @return the wizard builder
		 */
		public Builder leftSideImage(Image image) {
			this.leftSideImage = image;
			return this;
		}

		/**
		 * Set the locale. If provided locale is not supported, English is used.
		 * 
		 * @param locale
		 *            locale (language) of the wizard
		 * @return the wizard builder
		 */
		public Builder locale(Locale locale) {
			this.locale = locale;
			return this;
		}

		/**
		 * Set the vertical size
		 * 
		 * @param verticalSize
		 *            vertical size in pixel of the wizard
		 * @return the wizard builder
		 */
		public Builder vSize(int verticalSize) {
			this.verticalSize = verticalSize;
			return this;
		}

		/**
		 * Set the horizontal size. Default is 700.
		 * 
		 * @param horizontalSize
		 *            horizontal size in pixel of the wizard
		 * @return the wizard builder
		 */
		public Builder hSize(int horizontalSize) {
			this.horizontalSize = horizontalSize;
			return this;
		}

		/**
		 * Set the vertical padding. Default is 500.
		 * 
		 * @param layoutVPadding
		 *            vertical padding in pixel between header and body
		 * @return the wizard builder
		 */
		public Builder vPadding(int layoutVPadding) {
			this.layoutVPadding = layoutVPadding;
			return this;
		}

		/**
		 * Set the horizontal padding
		 * 
		 * @param layoutHPadding
		 *            horizontal padding in pixel between left side image and
		 *            body
		 * @return the wizard builder
		 */
		public Builder hPadding(int layoutHPadding) {
			this.layoutHPadding = layoutHPadding;
			return this;
		}
	}

	/**
	 * Wizard constructor.
	 * 
	 * @param builder
	 *            Wizard builder
	 */
	public Wizard(Builder builder) {
		bCancelled = false;
		this.name = builder.name;
		this.parentWindow = builder.parentWindow;
		Langpack.setLocale((builder.locale == null) ? Locale.getDefault() : builder.locale);
		this.header = new Header();
		header.setIcon(builder.icon);
		header.setBackgroundImage(builder.headerBackgroundImage);
		this.layoutHPadding = builder.layoutHPadding >= 0 ? builder.layoutHPadding
				: DEFAULT_H_LAYOUT_PADDING;
		this.layoutVPadding = builder.layoutVPadding >= 0 ? builder.layoutVPadding
				: DEFAULT_V_LAYOUT_PADDING;
		this.initial = builder.initial;
		this.leftSideImage = builder.leftSideImage;
		this.horizontalSize = builder.horizontalSize >= 0 ? builder.horizontalSize : DEFAULT_H_SIZE;
		this.verticalSize = builder.verticalSize >= 0 ? builder.verticalSize : DEFAULT_V_SIZE;
	}

	/**
	 * Show the wizard *
	 */
	public void show() {
		createDialog();
		setScreen(initial);
		current.onEnter();
		dialog.setVisible(true);
	}

	/**
	 * access to the JDialog of the wizard, in case we need it (for instance to
	 * set a glass pane when waiting).
	 * 
	 * @return the wizard dialog
	 */
	public JDialog getDialog() {
		return dialog;
	}

	/**
	 * UI manager.
	 */
	private void createDialog() {
		dialog = new JDialog(parentWindow, true);// modal
		// Set default size
		dialog.setSize(this.horizontalSize == 0 ? DEFAULT_H_SIZE : horizontalSize,
				this.verticalSize == 0 ? DEFAULT_V_SIZE : verticalSize);
		dialog.setTitle(name);
		actions = new ActionsPanel(this);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(this);
		dialog.setLocationRelativeTo(parentWindow);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try {
			// Previous required. Note that the previous button is enabled only
			// if the user can go previous
			if ("Prev".equals(ae.getActionCommand())) {
				setScreen(getPreviousScreen(current.getClass()));
			} else if ("Next".equals(ae.getActionCommand())) {
				current.onNext();
				setScreen(getNextScreen(current.getClass()));
				current.onEnter();
			} else if ("Cancel".equals(ae.getActionCommand())) {
				current.onCancelled();
				data.clear();
				bCancelled = true;
				onCancel();
				dialog.dispose();
			} else if ("Finish".equals(ae.getActionCommand())) {
				current.onFinished();
				finish();
				dialog.dispose();
			}
		} finally {
			dialog.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Set the screen to display a a class.
	 * 
	 * @param screenClass
	 *            screen class to set. Note that this class must be public or
	 *            you'll get an IllegalArgumentException.
	 * @throw {@link IllegalArgumentException} if the screen class doesn't exist
	 *        or is not accessible
	 */
	private void setScreen(Class<? extends Screen> screenClass) throws IllegalArgumentException {
		Screen screen = null;
		// If the class is an clear point, we clean up all previous screens
		if (screenClass.isAnnotationPresent(ClearPoint.class)) {
			resetScreens();
		}
		// Try to get a screen from buffer or create it if needed
		if (hmClassScreens.containsKey(screenClass)) {
			screen = hmClassScreens.get(screenClass);
		} else {
			try {
				screen = screenClass.newInstance();
			} catch (InstantiationException e) {
				logger.log(Level.SEVERE, "Cannot instanciate the screen", e);
				throw new IllegalArgumentException("Cannot instanciate the screen", e);
			} catch (IllegalAccessException e) {
				logger.log(Level.SEVERE, "Cannot instanciate the screen", e);
				throw new IllegalArgumentException("Cannot instanciate the screen", e);
			}
			screen.setWizard(this);
			screen.initUI();
			hmClassScreens.put(screenClass, screen);
		}
		current = screen;
		current.setCanGoPrevious(getPreviousScreen(screenClass) != null);
		current.setCanGoNext(getNextScreen(screenClass) != null);
		String sDesc = screen.getDescription();
		if (sDesc != null) {
			header.setTitle(screen.getName());
			header.setSubtitle(sDesc);
		} else {
			header.setTitle(screen.getName());
			header.setSubtitle("");
		}
		refreshGUI();
	}

	/**
	 * Called at each screen refresh.
	 */
	private void refreshGUI() {
		((JPanel) dialog.getContentPane()).removeAll();
		dialog.setLayout(new BorderLayout(layoutHPadding, layoutVPadding));
		if (leftSideImage != null) {
			final JLabel jlIcon = new JLabel(new ImageIcon(leftSideImage));
			dialog.add(jlIcon, BorderLayout.WEST);
			// Add a listener to resize left side image if wizard window is
			// resized
			jlIcon.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					jlIcon.setIcon(Utils.getResizedImage(leftSideImage, jlIcon.getWidth(),
							jlIcon.getHeight()));
					jlIcon.setVisible(true);
				}
			});
		}
		dialog.add(actions, BorderLayout.SOUTH);
		JScrollPane jsp = new JScrollPane(header);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		dialog.add(jsp, BorderLayout.NORTH);
		if (current != null) {
			dialog.add(current, BorderLayout.CENTER);
		}
		actions.setNextAsDefaultButtonInPanel(dialog.getRootPane());
		((JPanel) dialog.getContentPane()).revalidate();
		dialog.getContentPane().repaint();
	}

	/**
	 * Set the header image.
	 * 
	 * @param img
	 *            image to set as header
	 */
	public void setHeaderImage(Image img) {
		header.setBackgroundImage(img);
	}

	/**
	 * Set the header icon.
	 * 
	 * @param icon
	 *            icon to set as header
	 */
	public void setHeaderIcon(ImageIcon icon) {
		header.setIcon(icon);
	}

	/**
	 * Set the background color of the ActionPanel.
	 * 
	 * @param color
	 *            background color to set
	 */
	public void setActionsBackgroundColor(Color color) {
		actions.setBackgroundColor(color);
	}

	/**
	 * Set the background color of the ActionPanel's Problem notification area.
	 * 
	 * @param color
	 *            problems panel background color to set
	 */
	public void setProblemBackgroundColor(Color color) {
		actions.setProblemBackgroundColor(color);
	}

	/**
	 * Gets the previous screen class.
	 * 
	 * @param screen
	 *            reference screen for which we want the previous screen class.
	 * 
	 * @return previous screen class relatively to <code>screen</code> or null
	 *         if there is no screen before <code>screen</code>.
	 */
	abstract public Class<? extends Screen> getPreviousScreen(Class<? extends Screen> screen);

	/**
	 * Clear screens history, all screens are dropped along with their data and
	 * will be recreated in future use.
	 */
	public final void resetScreens() {
		hmClassScreens.clear();
	}

	/**
	 * Gets the next screen class.
	 * 
	 * @param screen
	 *            reference screen for which we want the next screen class.
	 * 
	 * @return next screen class relatively to <code>screen</code> or null if
	 *         there is no screen after <code>screen</code>.
	 */
	abstract public Class<? extends Screen> getNextScreen(Class<? extends Screen> screen);

	/**
	 * Get current screen.
	 * 
	 * @return current screen class
	 */
	public Class<? extends Screen> getCurrentScreen() {
		return this.current.getClass();
	}

	/**
	 * Refresh buttons and problems. Called asynchronously by the screens or by
	 * the wizard itself.
	 */
	public void updateGUIState() {
		boolean bPrevious = current.canGoPrevious();
		boolean bNext = current.canGoNext();
		boolean bFinish = current.canFinish();
		boolean bCancel = current.canCancel();
		actions.setState(bPrevious, bNext, bFinish, bCancel);
		actions.setProblem(current.getProblem());
	}

	/**
	 * Finish action. Called when user clicks on "finish"
	 */
	abstract public void finish();

	/**
	 * Called when user clicks on "cancel". Override it if you want to do
	 * something in cancel such as display a confirmation dialog.
	 * <p>
	 * 
	 * @return return true if the Wizard should continue to close return false
	 *         if the Wizard should abort the cancellation
	 */
	public boolean onCancel() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent windowEvent) {
		// if cancel is disabled, then don't call the onCancel function and
		// don't dispose
		if (current.canCancel() && onCancel()) {
			bCancelled = true;
			dialog.dispose();
		}
	}

	/**
	 * Was canceled.
	 * 
	 * 
	 * @return true if...
	 */
	public boolean wasCancelled() {
		return bCancelled;
	}
}
