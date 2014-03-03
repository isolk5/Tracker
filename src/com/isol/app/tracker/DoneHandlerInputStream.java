package com.isol.app.tracker;

import java.io.*;

public class DoneHandlerInputStream extends FilterInputStream
{
	private boolean done;
 
	public DoneHandlerInputStream(InputStream stream)
	{
		super(stream);
	}
 
	@Override
	public int read(byte[] bytes, int offset, int count) throws IOException
	{
		if (!done)
		{
			int result = super.read(bytes, offset, count);
			if (result != -1)
			{
				return result;
			}
		}
		done = true;
		return -1;
	}
}
