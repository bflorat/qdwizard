/*
 *  Jajuk
 *  Copyright (C) 2003 "Bertrand Florat <bertrand@florat.net>"
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
 *  $Revision$
 */

package org.qdwizard;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Test;

public class LangpackTest {

	@Test
	public void testAddLocaleOK() throws Exception {
		Langpack.addLocale(new Locale("is"), Arrays.asList("a", "b", "c", "d"));
		Langpack.setLocale(new Locale("is"));
		assertEquals("c", Langpack.getMessage("Previous"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddLocaleNullLocale() throws Exception {
		Langpack.addLocale(null, Arrays.asList("a", "b", "c", "d"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddLocaleNullLabels() throws Exception {
		Langpack.addLocale(new Locale("is"), null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddLocaleWrongLabelSize() throws Exception {
		Langpack.addLocale(new Locale("is"), Arrays.asList("a", "b", "c"));
	}

}
