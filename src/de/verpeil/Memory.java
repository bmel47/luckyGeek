/**********************************
 * Memory.java
 * Part of the project "luckyGeek" from
 * ctvoigt (Christian Voigt), chripo2701  2011.
 *
 * 
 * Email: luckygeek@verpeil.de
 * 
 *
 * 
 **********************************
 * 
 * Class remembering the last download. Uses property-file.
 **********************************
 * 
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 */

package de.verpeil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

class Memory {
	private static final Logger LOG = Logger.getLogger(Memory.class
			.getCanonicalName());
	private static final File MEMORY_FILE = new File("./conf/memory.properties");
	private final Properties memory = new Properties();

	Memory() {
		InputStream ins = null;
		try {
			ins = new FileInputStream(MEMORY_FILE);
			memory.load(ins);
		} catch (Exception e) {
			LOG.severe("Can not load memory. Message: " + e.getMessage());
		} finally {
			IOUtils.closeQuietly(ins);
		}
	}

	String getUrl() {
		return memory.getProperty("url", "");
	}

	void setUrl(String url) {
		memory.setProperty("url", url);
	}
	
	void save() {
		OutputStream out = null;
		try {
			out = new FileOutputStream(MEMORY_FILE);
			memory.store(out, "");
		} catch (Exception e) {
			LOG.severe("Can not save memory. Message: " + e.getMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
