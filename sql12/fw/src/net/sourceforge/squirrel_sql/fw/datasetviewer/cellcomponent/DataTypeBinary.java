package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.event.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
//??import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;

/**
 * @author gwg
 *
 * This class provides the display components for handling Binary data types,
 * specifically SQL types BINARY, VARBINARY, and LONGVARBINARY.
 * The display components are for:
 * <UL>
 * <LI> read-only display within a table cell
 * <LI> editing within a table cell
 * <LI> read-only or editing display within a separate window
 * </UL>
 * The class also contains 
 * <UL>
 * <LI> a function to compare two display values
 * to see if they are equal.  This is needed because the display format
 * may not be the same as the internal format, and all internal object
 * types may not provide an appropriate equals() function.
 * <LI> a function to return a printable text form of the cell contents,
 * which is used in the text version of the table.
 * </UL>
 * <P>
 * The components returned from this class extend RestorableJTextField
 * and RestorableJTextArea for use in editing table cells that
 * contain values of this data type.  It provides the special behavior for null
 * handling and resetting the cell to the original value.
 */

public class DataTypeBinary
 	implements IDataTypeComponent
{
	/* the whole column definition */
	private ColumnDisplayDefinition _colDef;

	/* whether nulls are allowed or not */
	private boolean _isNullable;

	/* the number of decimal digits allowed in the number */
	private int _scale;

	/* table of which we are part (needed for creating popup dialog) */
	private JTable _table;
	
	/* The JTextComponent that is being used for editing */
	private IRestorableTextComponent _textComponent;


	/**
	 * Constructor - save the data needed by this data type.
	 */
	public DataTypeBinary(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();
		_scale = colDef.getScale();
	}
	
	/**
	 * Return the name of the java class used to hold this data type.
	 */
	public String getClassName() {
		return "[Ljava.lang.Byte";
	}

	/**
	 * Determine if two objects of this data type contain the same value.
	 * Neither of the objects is null
	 */
	public boolean areEqual(Object obj1, Object obj2) {
		Byte[] b1 = (Byte[])obj1;
		Byte[] b2 = (Byte[])obj2;
		for (int i=0; i<b1.length; i++)
			if (b1[i] != b2[i])
				return false;
				
		return true;
	}

	/*
	 * First we have the methods for in-cell and Text-table operations
	 */
	 
	/**
	 * Render a value into text for this DataType.
	 */
	public String renderObject(Object value) {
		// use the default settings for the conversion
		return BinaryDisplayConverter.convertToString((Byte[])value,
			BinaryDisplayConverter.HEX, false);
	}
	
	/**
	 * This Data Type can be edited in a table cell.
	 */
	public boolean isEditableInCell() {
		return true;	
	}
	
	/**
	 * Return a JTextField usable in a CellEditor.
	 */
	public JTextField getJTextField() {
		_textComponent = new RestorableJTextField();
		
		// special handling of operations while editing this data type
		((RestorableJTextField)_textComponent).addKeyListener(new KeyTextHandler());
				
		//
		// handle mouse events for double-click creation of popup dialog.
		// This happens only in the JTextField, not the JTextArea, so we can
		// make this an inner class within this method rather than a separate
		// inner class as is done with the KeyTextHandler class.
		//
		((RestorableJTextField)_textComponent).addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					MouseEvent tableEvt = SwingUtilities.convertMouseEvent(
						(RestorableJTextField)DataTypeBinary.this._textComponent,
						evt, DataTypeBinary.this._table);
					CellDataPopup.showDialog(DataTypeBinary.this._table,
						DataTypeBinary.this._colDef, tableEvt);
				}
			}
		});	// end of mouse listener

		return (JTextField)_textComponent;
	}


	/**
	 * Implement the interface for validating and converting to internal object.
	 * Null is a valid successful return, so errors are indicated only by
	 * existance or not of a message in the messageBuffer.
	 */
	public Object validateAndConvert(String value, StringBuffer messageBuffer) {
		// handle null, which is shown as the special string "<null>"
		if (value == null || value.equals("<null>") || value.equals(""))
			return null;

		// Do the conversion into the object in a safe manner
		try {
			Object obj = BinaryDisplayConverter.convertToBytes(value,
				BinaryDisplayConverter.HEX, false);
			return obj;
		}
		catch (Exception e) {
			messageBuffer.append(e.toString()+"\n");
			//?? do we need the message also, or is it automatically part of the toString()?
			//messageBuffer.append(e.getMessage());
			return null;
		}
	}

	/*
	 * Now the functions for the Popup-related operations.
	 */
	
	/**
	 * Returns true if data type may be edited in the popup,
	 * false if not.
	 */
	public boolean isEditableInPopup() {
		return true;
	}

	/*
	 * Return a JTextArea usable in the CellPopupDialog
	 * and fill in the value.
	 */
	 public JTextArea getJTextArea(Object value) {
		_textComponent = new RestorableJTextArea();
		
		// value is a simple string representation of the data,
		// the same one used in Text and in-cell operations.
		((RestorableJTextArea)_textComponent).setText(renderObject(value));
		
		// special handling of operations while editing this data type
		((RestorableJTextArea)_textComponent).addKeyListener(new KeyTextHandler());
		
		return (RestorableJTextArea)_textComponent;
	 }

	/**
	 * Validating and converting in Popup is identical to cell-related operation.
	 */
	public Object validateAndConvertInPopup(String value, StringBuffer messageBuffer) {
		return validateAndConvert(value, messageBuffer);
	}

	/*
	 * The following is used in both cell and popup operations.
	 */	
	
	/*
	 * Internal class for handling key events during editing
	 * of both JTextField and JTextArea.
	 */
	 private class KeyTextHandler extends KeyAdapter {
	 	public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				
				// as a coding convenience, create a reference to the text component
				// that is typecast to JTextComponent.  this is not essential, as we
				// could typecast every reference, but this makes the code cleaner
				JTextComponent _theComponent = (JTextComponent)DataTypeBinary.this._textComponent;
				String text = _theComponent.getText();
												
				// tabs and newlines get put into the text before this check,
				// so remove them
				// This only applies to Popup editing since these chars are
				// not passed to this level by the in-cell editor.
				if (c == KeyEvent.VK_TAB || c == KeyEvent.VK_ENTER) {
					int cIndex = text.indexOf(c);
					String newText = null;
					if (cIndex == 0)
						newText = text.substring(1);
					else if (cIndex == text.length()-1)
						newText = text.substring(0, text.length()-1);
					else
						newText = text.substring(0, cIndex) + text.substring(cIndex+1);

					((IRestorableTextComponent)_theComponent).updateText(newText);
					_theComponent.getToolkit().beep();
					e.consume();
				}

				// handle cases of null
				// The processing is different when nulls are allowed and when they are not.
				//

				if ( DataTypeBinary.this._isNullable) {

					// user enters something when field is null
					if (text.equals("<null>")) {
						if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							// delete when null => original value
							DataTypeBinary.this._textComponent.restoreText();
							e.consume();
						}
						else {
							// non-delete when null => clear field and add text
							DataTypeBinary.this._textComponent.updateText("");
							// fall through to normal processing of this key stroke
						}
					}
					else {
						// check for user deletes last thing in field
						if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							if (text.length() <= 1 ) {
								// about to delete last thing in field, so replace with null
								DataTypeBinary.this._textComponent.updateText("<null>");
								e.consume();
							}
						}
					}
				}
				else {
					// field is not nullable
					//
					// if the field is not allowed to have nulls, we need to let the
					// user erase the entire contents of the field so that they can enter
					// a brand-new value from scratch.  While the empty field is not a legal
					// value, we cannot avoid allowing it.  This is the normal editing behavior,
					// so we do not need to add anything special here except for the cyclic
					// re-entering of the original data if user hits delete when field is empty
					if (text.length() == 0 &&
						(c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
						// delete when null => original value
						DataTypeBinary.this._textComponent.restoreText();
						e.consume();
					}
				}
			}
		}


	
	
	/*
	 * DataBase-related functions
	 */
	 
	 /**
	  * On input from the DB, read the data from the ResultSet into the appropriate
	  * type of object to be stored in the table cell.
	  */
	public Object readResultSet(ResultSet rs, int index,
		LargeResultSetObjectInfo largeObjInfo)
		throws java.sql.SQLException {
		
		byte[] data = rs.getBytes(index);
		if (rs.wasNull())
			return null;
		else {
			Byte[] internal = new Byte[data.length];
			for (int i=0; i<data.length; i++)
				internal[i] = new Byte(data[i]);
			return internal;
		}
	}


	/**
	 * When updating the database, generate a string form of this object value
	 * that can be used in the WHERE clause to match the value in the database.
	 * A return value of null means that this column cannot be used in the WHERE
	 * clause, while a return of "null" (or "is null", etc) means that the column
	 * can be used in the WHERE clause and the value is actually a null value.
	 * This function must also include the column label so that its output
	 * is of the form:
	 * 	"columnName = value"
	 * or
	 * 	"columnName is null"
	 * or whatever is appropriate for this column in the database.
	 */
	public String getWhereClauseValue(Object value) {
		if (value == null || value.toString() == null || value.toString().length() == 0)
			return _colDef.getLabel() + " IS NULL";
		else
			//?? There does not seem to be any standard way to represent
			//?? binary data in a WHERE clause...
			return null;	// tell caller we cannot use this in Where clause
	}
	
	
	/**
	 * When updating the database, insert the appropriate datatype into the
	 * prepared statment at variable position 1.
	 */
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value)
		throws java.sql.SQLException {
		if (value == null) {
			pstmt.setNull(1, _colDef.getSqlType());
		}
		else {
			Byte[] internal = (Byte[])value;
			byte[] dbValue = new byte[internal.length];
			for (int i=0; i<internal.length; i++)
				dbValue[i] = internal[i].byteValue();
			pstmt.setBytes(1, dbValue);
		}
	}
	
	
	/*
	 * File IO related functions
	 */
	 
	 
	 /**
	  * Say whether or not object can be exported to and imported from
	  * a file.  We put both export and import together in one test
	  * on the assumption that all conversions can be done both ways.
	  */
	 public boolean canDoFileIO() {
	 	return true;
	 }
	 
	 /**
	  * Read a file and construct a valid object from its contents.
	  * Errors are returned by throwing an IOException containing the
	  * cause of the problem as its message.
	  * <P>
	  * DataType is responsible for validating that the imported
	  * data can be converted to an object, and then must return
	  * a text string that can be used in the Popup window text area.
	  * This object-to-text conversion is the same as is done by
	  * the DataType object internally in the getJTextArea() method.
	  */
	public String importObject(FileInputStream inStream)
	 	throws IOException {
	 	

	 	int fileSize = inStream.available();
	 	
	 	byte[] buf = new byte[fileSize];
	 	
	 	int count = inStream.read(buf);
	 	
	 	if (count != fileSize)
	 		throw new IOException(
	 			"Could read only "+ count +
	 			" bytes from a total file size of " + fileSize +
	 			". Import failed.");
	 	
	 	// Convert bytes to Bytes
	 	Byte[] bBytes = new Byte[count];
	 	for (int i=0; i<count; i++)
	 		bBytes[i] = new Byte(buf[i]);
	 	
	 	// return the text converted from the file 
	 	return BinaryDisplayConverter.convertToString(bBytes,
	 		BinaryDisplayConverter.HEX, false);
	}

	 	 
	 /**
	  * Construct an appropriate external representation of the object
	  * and write it to a file.
	  * Errors are returned by throwing an IOException containing the
	  * cause of the problem as its message.
	  * <P>
	  * DataType is responsible for validating that the given text
	  * text from a Popup JTextArea can be converted to an object.
	  * This text-to-object conversion is the same as validateAndConvertInPopup,
	  * which may be used internally by the object to do the validation.
	  * <P>
	  * The DataType object must flush and close the output stream before returning.
	  * Typically it will create another object (e.g. an OutputWriter), and
	  * that is the object that must be flushed and closed.
	  */
	 public void exportObject(FileOutputStream outStream, String text)
	 	throws IOException {
	 	
		Byte[] bBytes = BinaryDisplayConverter.convertToBytes(text,
			BinaryDisplayConverter.HEX, false);
	 	
	 	// check that the text is a valid representation
	 	StringBuffer messageBuffer = new StringBuffer();
	 	validateAndConvertInPopup(text, messageBuffer);
	 	if (messageBuffer.length() > 0) {
	 		// there was an error in the conversion
	 		throw new IOException(new String(messageBuffer));
	 	}
	 	
	 	// Convert Bytes to bytes
	 	byte[] bytes = new byte[bBytes.length];
	 	for (int i=0; i<bytes.length; i++)
	 		bytes[i] = bBytes[i].byteValue();
	 	
	 	// just send the text to the output file
		outStream.write(bytes);
		outStream.flush();
		outStream.close();
	 }
}
