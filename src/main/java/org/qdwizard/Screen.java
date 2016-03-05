/*
 *  QDWizard
 *  Copyright (C) Bertrand Florat and others
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *  
 */
package org.qdwizard;

import java.util.Map;

import javax.swing.JPanel;

/**
 * A wizard screen
 * <ul>
 * <li>For each wizard page, create a public Screen class. You have to implement
 * initUI(), getDescription() and getName() abstract mandatory methods.</li>
 * <li>{@code getName()} method should return the step name and
 * {@code getDescription()} the step description (can return null if no
 * description is required).</li>
 * <li>{@code initUI()} method contains graphical code for your screen. This
 * method is automatically called from screen constructor, you shouldn't call it
 * directly.</li>
 * </ul>
 */
public abstract class Screen extends JPanel {
	/** Generated serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/** The state of the screen */
	private final ScreenState state;
	/** Data is shared with wizard scope */
	public Map<Object, Object> data;

	/**
	 * Construct a screen.
	 */
	public Screen() {
		state = new ScreenState(true, true, true, false, null);
	}

	/**
	 * Set the data map from the wizard
	 * 
	 * @param data
	 *            the data map
	 */
	void setData(Map<Object, Object> data) {
		this.data = data;
	}

	/**
	 * Give here the step name.
	 * 
	 * @return screen name
	 */
	@Override
	abstract public String getName();

	/**
	 * Give here the screen description (can be null)
	 * 
	 * @return screen description
	 */
	abstract public String getDescription();

	/**
	 * Can finish.
	 * 
	 * 
	 * @return true if this is the last screen.
	 */
	boolean canFinish() {
		// Can finish only if none problem
		return state.getCanFinish() && state.getProblem() == null;
	}

	/**
	 * Set whether the finish button should be enabled.
	 * 
	 * @param b
	 *            set whether we can finish from this screen
	 */
	protected void setCanFinish(boolean b) {
		state.setCanFinish(b);
		notifyGUI();
	}

	/**
	 * Can go next.
	 * 
	 * 
	 * @return true if we can go next from this screen
	 */
	boolean canGoNext() {
		// if screen is last one, cannot go further
		return state.getCanGoNext() && !state.getCanFinish() && state.getProblem() == null;
	}

	/**
	 * Can cancel.
	 * 
	 * 
	 * @return true if we can cancel from this screen
	 */
	boolean canCancel() {
		return state.getCanCancel();
	}

	/**
	 * Can go previous.
	 * 
	 * 
	 * @return true if we can go to the previous screen from this screen
	 */
	boolean canGoPrevious() {
		return state.getCanGoPrevious();
	}

	/**
	 * Set whether the next button should be enabled.
	 * 
	 * @param b
	 *            set whether we can go to the next screen from this screen
	 */
	protected void setCanGoNext(boolean b) {
		state.setCanGoNext(b);
		notifyGUI();
	}

	/**
	 * Set whether the previous button should be enabled.
	 * 
	 * @param b
	 *            set whether we can go to the previous screen from this screen
	 */
	protected void setCanGoPrevious(boolean b) {
		state.setCanGoPrevious(b);
		notifyGUI();
	}

	/**
	 * Set whether the cancel (or System menu close) button should be enabled.
	 * 
	 * @param b
	 *            set whether we can cancel from this screen
	 */
	protected void setCanCancel(boolean b) {
		state.setCanCancel(b);
		notifyGUI();
	}

	/**
	 * Set a problem (set to null if problem is fixed).
	 * 
	 * @param sProblem
	 *            Problem string or null if no more problem
	 */
	public void setProblem(String sProblem) {
		state.setProblem(sProblem);
		notifyGUI();
	}

	/**
	 * Get current problem if any (or null if none problem)
	 * 
	 * @return the current problem
	 */
	public String getProblem() {
		return state.getProblem();
	}

	/**
	 * UI creation.
	 */
	abstract public void initUI();

	/**
	 * Called by wizard before the screen is displayed. This happens only in
	 * forward mode, which means {@code onEnter()} won't be called when you
	 * return to a screen via the previous button.
	 */
	public void onEnter() {
		// Do nothing by default
	}

	/**
	 * Programmatical switch to the next screen.
	 * 
	 */
	public void forceNextScreen() {
		synchronized (data) {
			this.data.put(Utils.RESERVED_DATA.FORCED_NEXT_SCREEN, true);
		}
	}

	/**
	 * Programmatical switch to the previous screen.
	 * 
	 */
	public void forcePreviousScreen() {
		synchronized (data) {
			this.data.put(Utils.RESERVED_DATA.FORCED_PREV_SCREEN, true);
		}
	}

	/**
	 * Programmatical cancel.
	 * 
	 */
	public void forceCancel() {
		synchronized (data) {
			this.data.put(Utils.RESERVED_DATA.FORCED_CANCEL, true);
		}
	}

	/**
	 * Programmatical finish.
	 * 
	 */
	public void forceFinish() {
		synchronized (data) {
			this.data.put(Utils.RESERVED_DATA.FORCED_FINISH, true);
		}
	}

	/**
	 * Called by wizard before the screen is left. This happens only in forward
	 * mode, which means {@code onLeave()} won't be called when you leave the
	 * screen via the previous button.
	 * 
	 */
	public void onLeave() {
		// Does nothing by default
	}

	/**
	 * Ask for a GUI refresh
	 * 
	 */
	private void notifyGUI() {
		synchronized (data) {
			this.data.put(Utils.RESERVED_DATA.UPDATE_GUI, true);
		}
	}
}
