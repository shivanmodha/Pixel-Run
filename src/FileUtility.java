import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class FileUtility
{
	public static String readAsString(String _in, boolean sepLines) throws Exception
	{
		String _return = "";
		try
		{
			BufferedReader buffer = new BufferedReader(new FileReader(_in));
			String line = "";
			int lines = 0;
			while ((line = buffer.readLine()) != null)
			{
				if (lines > 0 && sepLines)
				{
					_return += "\n";
				}
				_return += line;
				lines++;
			}
			buffer.close();
		}
		catch (Exception e)
		{
			throw e;
		}
		return _return;
	}
	public static String[] readAsStringArray(String _in) throws Exception
	{
		ArrayList<String> arr = new ArrayList<String>();
		try
		{
			BufferedReader buffer = new BufferedReader(new FileReader(_in));
			String line = "";
			while ((line = buffer.readLine()) != null)
			{
                line = line.replaceAll("﻿", ""); // UTF-8 BOM Element << Doesn't read the file with the correct encoding, but this is a simple hack that simply gets rid of the junk as text
				arr.add(line);
			}
			buffer.close();
		}
		catch (Exception e)
		{
			throw e;
		}
		return (String[])arr.toArray(new String[arr.size()]);
	}
}
