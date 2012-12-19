package com.badlogic.androidgames.greeracer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.badlogic.androidgames.framework.FileIO;

public class Settings {
	public static boolean soundEnabled = true;
	public static boolean touchEnabled = false;
	public final static String file = ".greeracer";

	public static List<Double> shiftTimes;
	
	public static void load(FileIO files) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(files.readFile(file)));
			soundEnabled = Boolean.parseBoolean(in.readLine());
			touchEnabled = Boolean.parseBoolean(in.readLine());
			shiftTimes = new ArrayList<Double>();
			String line;
			
			line = in.readLine(); 
			if (line != null && line.length() > 0) {
				shiftTimes.add(Double.parseDouble(line));
			}
			line = in.readLine(); 
			if (line != null && line.length() > 0) {
				shiftTimes.add(Double.parseDouble(line));
			}
			line = in.readLine(); 
			if (line != null && line.length() > 0) {
				shiftTimes.add(Double.parseDouble(line));
			}
			line = in.readLine(); 
			if (line != null && line.length() > 0) {
				shiftTimes.add(Double.parseDouble(line));
			}
			line = in.readLine(); 
			if (line != null && line.length() > 0) {
				shiftTimes.add(Double.parseDouble(line));
			}
			
		} catch (IOException e) {
			// :( It's ok we have defaults
		} catch (NumberFormatException e) {
			// :/ It's ok, defaults save our day
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}

	public static void save(FileIO files) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					files.writeFile(file)));
			out.write(Boolean.toString(soundEnabled));
			out.write("\n");
			out.write(Boolean.toString(touchEnabled));
			
			if (shiftTimes != null) {
				int missingShifts = 5 - shiftTimes.size();
				for (int i = 0; i < missingShifts; i ++) {
					shiftTimes.add(Double.MAX_VALUE);
				}
				out.write("\n");
				out.write(shiftTimes.get(0).toString());
				out.write("\n");
				out.write(shiftTimes.get(1).toString());
				out.write("\n");
				out.write(shiftTimes.get(2).toString());
				out.write("\n");
				out.write(shiftTimes.get(3).toString());
				out.write("\n");
				out.write(shiftTimes.get(4).toString());
				Log.w("Settings", "wrote 5 lines of doubles");
			}
			
			
		} catch (IOException e) {
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
	}
}
