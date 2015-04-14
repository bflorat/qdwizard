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
 */
package org.qdwizard;

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * Various internal utilities
 */
class Utils {
	
	/** List of reserved data types */
	enum RESERVED_DATA{
		/** Request for GUI refresh, maps a boolean*/
		UPDATE_GUI,
		/** Request for a programmatical next screen action */
		FORCED_NEXT_SCREEN,
		/** Request for a programmatical previous screen action */
		FORCED_PREV_SCREEN,
		/** Request for a programmatical cancel action */
		FORCED_CANCEL,
		/** Request for a programmatical finish action */
		FORCED_FINISH,
		
	}
	
  
  /**
   * Image resizes
   */
  static ImageIcon getResizedImage(Image img, int iNewWidth, int iNewHeight) {
    if (img == null) {
      return null;
    }
    ImageIcon iiNew = new ImageIcon();
    Image scaleImg = img.getScaledInstance(iNewWidth, iNewHeight, Image.SCALE_AREA_AVERAGING);
    iiNew.setImage(scaleImg);
    return iiNew;
  }
}
