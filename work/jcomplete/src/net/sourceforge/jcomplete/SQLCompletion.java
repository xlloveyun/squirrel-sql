/*
 * Copyright (C) 2002 Christian Sell
 * csell@users.sourceforge.net
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
 *
 * created by cse, 24.09.2002 12:00:04
 */
package net.sourceforge.jcomplete;

import net.sourceforge.jcomplete.Completion;
import net.sourceforge.jcomplete.completions.SQLStatement;

/**
 * abstract superclass for completion items
 */
public abstract class SQLCompletion implements Completion
{
    public static String[] EMPTY_RESULT = new String[0];

    protected int startPosition=NO_POSITION, endPosition=NO_POSITION;
    protected SQLStatement parent;

    protected SQLCompletion(int startPosition)
    {
        this.startPosition = startPosition;
    }

    protected SQLCompletion() {}

    /**
     * Find a completion item for the given text position. This method can be overridden by
     * subclasses which implement the composite pattern. The default implementation returns
     * the current object if <em>position</em> is within its position limits (inclusive).
     * @param position the caret position at which the completion is requested
     * @return an appropriate completion object - this one, a subelement or <em>null</em>
     */
    public SQLCompletion getCompletion(int position)
    {
        return position >= startPosition && position <= endPosition ? this : null;
    }

    public void setEndPosition(int offset)
    {
        this.endPosition = offset;
    }

    /**
     * @return the completion text
     * @throws UnsupportedOperationException
     */
    public String getText(int position)
    {
        throw new UnsupportedOperationException("completion not available");
    }

    /**
     * @return the completion text, provided it is available
     * @throws UnsupportedOperationException
     */
    public String getText(int position, String options)
    {
        throw new UnsupportedOperationException("completion not available");
    }

    public boolean hasTextPosition()
    {
        return startPosition != NO_POSITION && endPosition != NO_POSITION;
    }

    public boolean isRepeatable()
    {
        return false;
    }

    public int getLength()
    {
        return endPosition - startPosition + 1;
    }

    public int getStart()
    {
        return startPosition;
    }

    public boolean mustReplace(int position)
    {
        return false;
    }

    public void updateWith(Completion completion)
    {
    }

    public void setParent(SQLStatement sqlStatement)
    {
        parent = sqlStatement;
    }

    public SQLStatement getParent()
    {
        return parent;
    }
}
