package net.sourceforge.squirrel_sql.fw.id;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.rmi.server.UID;

public class UidIdentifier implements IIdentifier {
	public interface IPropertyNames {
		String STRING = "string";
	}

	private String _id;

	public UidIdentifier() {
		super();
		_id = new UID().toString();
	}

	public boolean equals(Object rhs) {
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass())) {
			rc = ((UidIdentifier)rhs).toString().equals(toString());
		}
		return rc;
	}

	public synchronized int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		return _id;
	}

	// Only for restoring from XML etc.
	public void setString(String value) {
		_id = value;
	}
}