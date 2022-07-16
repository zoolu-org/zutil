/*
 * Copyright (c) 2018 Luca Veltri, University of Parma
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.zoolu.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/** Gets the values of command-line options/parameters.
 * It parses an array of strings (command-line arguments) looking for given command-line options and/or parameters.
 * <p>
 * It can be used also for generating a "Usage" message with the list and description of all program options/parameters already parsed.
 * <p>
 * Example of command-line list:
 * <br>
 * <center>val1 -a val2 -h -v --beta val3 val4</center>
* <p>
 * Strings are of four types:
 * <ul>
 * <li> 'boolean option' with only the option tag, e.g. '-h' or '--help'
 * <li> 'value option' with both option tag and value, e.g. '--url http://127.0.0.1'
 * <li> 'tuple option' with option tag and a tuple of values, e.g. '--addrport 127.0.0.1 80'
 * <li> 'parameter' with only the parameter value, e.g. 'http://127.0.0.1'
 * </ul>
 * Parameters must be taken according to their order.
 * <p>
 * In the example above, '-a val2' and '--beta val3' are options, '-h' and '-v' are flags, while 'val1' and 'val4' are parameters. 
 */
public class Flags {

	/** Parameter */
	public static final String PARAM=null;
	/** Optional parameter */
	public static final String OPTIONAL_PARAM="optional-param";

	/** First writes parameters, then options */
	public static boolean FIRST_PARAMS_THEN_OPTIONS=true;
	/** Tab string for indenting all lines following the first "Usage" line. */
	public static String TAB1="  ";
	//public static String TAB1="";
	/** Tab string for indenting all lines following "where:" and "Options:" */
	public static String TAB2=TAB1+"  ";
	//public static String TAB2=TAB1+"\t";
	/** Tab string between the 'opt-param' part and the 'description' part */
	public static String TAB3="  ";
	//public static String TAB3="\t";

	
	/** Arguments to be parsed */
	ArrayList<String> args;

	/** Option list */
	ArrayList<Option> options=new ArrayList<Option>();

	/** Parameter list */
	ArrayList<Option> params=new ArrayList<Option>();
	
	/** Whether '--not' option is enabled */
	boolean not_option=false;
	
	
	/** Creates options.
	 * @param args array of string contains the options and/or parameters. */
	public Flags(String[] args) {
		this.args=new ArrayList<String>(Arrays.asList(args));
	}
	
	/** Parses the array of string for a given 'boolean option'.
	 * The option is removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-u' or '--help')
	 * @param description a description of this option/parameter
	 * @return <i>true</i> if the option is present */
	public boolean getBoolean(String tag, String description) {
		if (description!=null) options.add(new Option(tag,null,description));
		for (int i=0; i<args.size(); i++) {
			if (args.get(i).equals(tag)) {
				args.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/** Parses the array of string for a given 'boolean option'.
	 * The option is removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-u' or '!-u')
	 * @param default_value default value
	 * @param description a description of this option/parameter
	 * @return the boolean value */
	public Boolean getBoolean(String tag, Boolean default_value, String description) {
		not_option=true;
		if (description!=null) options.add(new Option(tag,null,description));
		for (int i=0; i<args.size(); i++) {
			if (args.get(i).equals('!'+tag)) {
				args.remove(i);
				return false;
			}
			else
			if (args.get(i).equals(tag)) {
				args.remove(i);
				if (i>0 && args.get(i-1).equals("!")) {
					args.remove(i-1);
					return false;
				}
				else return true;
			}
		}
		return default_value;
	}
	
	/** Parses the array of strings for a given value option, or parameter.
	 * The option/parameter is removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-a' or '--add'); use {@link #PARAM} or {@link #OPTIONAL_PARAM} to indicate that iti is a parameter without tag
	 * @param param parameter string used to represent the value in the help string
	 * @param description a description of this option/parameter for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the value */
	private String getValue(String tag, String param, String description) {
		if (tag==null || tag==PARAM || tag==OPTIONAL_PARAM) {
			// parameter
			if (description!=null && param!=null) params.add(new Option(tag,param,description));
			if (args.size()>0) {
				String value=args.get(0);
				args.remove(0);
				return value;				
			}
		}
		else {
			// option
			if (description!=null && param!=null) options.add(new Option(tag,param,description));
			for (int i=0; i<args.size(); i++) {
				if (args.get(i).equals(tag)) {
					args.remove(i);
					String value=args.get(i);
					args.remove(i);
					return value;
				}
			}
		}
		return null;
	}

	/** Parses the array of strings for a given tuple option.
	 * The option is removed from the list of unparsed strings.
	 * @param tag option tag; e.g. '-v' or '--values'
	 * @param len the number of components of the tuple
	 * @param param parameter string used to represent the parameter tuple in the help string
	 * @param description a description of this option for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the tuple */
	private String[] getTuple(String tag, int len, String param, String description) {
		if (tag==null || tag==PARAM || tag==OPTIONAL_PARAM) throw new RuntimeException("tuple tag is missing; tuple can't be of type 'parameter'");
		if (description!=null && param!=null) options.add(new Option(tag,param,description));
		for (int i=0; i<args.size(); i++) {
			if (args.get(i).equals(tag)) {
				args.remove(i);
				String[] tuple=new String[len];
				for (int k=0; k<len; k++) {
					tuple[k]=args.get(i);
					args.remove(i);
				}
				return tuple;
			}
		}
		return null;
	}

	/** Parses the array of strings for a given array option.
	 * The option is removed from the list of unparsed strings.
	 * @param tag option tag; e.g. '-v' or '--values'
	 * @param param parameter string used to represent the parameter tuple in the help string
	 * @param description a description of this option for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the tuple */
	private String[] getArray(String tag, String param, String description) {
		if (tag==null || tag==PARAM || tag==OPTIONAL_PARAM) throw new RuntimeException("tag is missing; array can't be of type 'parameter'");
		if (description!=null && param!=null) options.add(new Option(tag,param,description));
		for (int i=0; i<args.size(); i++) {
			if (args.get(i).equals(tag)) {
				args.remove(i);
				int len=Integer.parseInt(args.get(i));
				args.remove(i);
				String[] values=new String[len];
				for (int k=0; k<len; k++) {
					values[k]=args.get(i);
					args.remove(i);
				}
				return values;
			}
		}
		return null;
	}

	/** Parses the array of strings for all occurrences of given string option/parameter.
	 * The option/parameter occurrences are removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-a' or '--add')
	 * @param param parameter string used to represent the value in the help string;
	 * @param description a description of this option/parameter for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the string value */
	/*private ArrayList<String> getAllOccurrences(String tag, String param, String description) {
		if (tag==null || tag==PARAM || tag==OPTIONAL_PARAM) throw new RuntimeException("tag is missing; it can't be of type 'parameter'");
		if (description!=null && param!=null) options.add(new Option(tag,param,description));
		ArrayList<String> list=new ArrayList<>();
		for (int i=0; i<args.size(); i++) {
			if (args.get(i).equals(tag)) {
				args.remove(i);
				String value=args.get(i);
				args.remove(i);
				list.add(value);
			}
		}
		return list;
	}*/

	/** Parses the array of strings for a given string option/parameter.
	 * The option/parameter is removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-a' or '--add'); use {@link #PARAM} or {@link #OPTIONAL_PARAM} to indicate that it is a parameter without tag
	 * @param default_value default value
	 * @param param parameter string used to represent the value in the help string;
	 * @param description a description of this option/parameter for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the string value */
	public String getString(String tag, String default_value, String param, String description) {
		String value=getValue(tag,param,description);
		return value!=null? value : default_value;
	}

	/** Parses the array of strings for a given integer option/parameter.
	 * The option/parameter is removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-a' or '--add'); use {@link #PARAM} or {@link #OPTIONAL_PARAM} to indicate that it is a parameter without tag
	 * @param default_value default value
	 * @param param parameter string; if not <i>null</i> it is the string used to represent the parameter value
	 * @param description a description
	 * @return the integer value */
	public int getInteger(String tag, int default_value, String param, String description) {
		String value=getValue(tag,param,description);
		return value!=null? Integer.parseInt(value) : default_value;
	}

	/** Parses the array of strings for a given long option/parameter.
	 * The option/parameter is removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-a' or '--add'); use {@link #PARAM} or {@link #OPTIONAL_PARAM} to indicate that it is a parameter without tag
	 * @param default_value default value
	 * @param param parameter string used to represent the value in the help string;
	 * @param description a description of this option/parameter for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the long value */
	public long getLong(String tag, long default_value, String param, String description) {
		String value=getValue(tag,param,description);
		return value!=null? Long.parseLong(value) : default_value;
	}

	/** Parses the array of strings for a given option/parameter and returns a float value.
	 * The option/parameter is removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-a' or '--add'); use {@link #PARAM} or {@link #OPTIONAL_PARAM} to indicate that it is a parameter without tag
	 * @param default_value default value
	 * @param param parameter string used to represent the value in the help string;
	 * @param description a description of this option/parameter for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the float value */
	public float getFloat(String tag, float default_value, String param, String description) {
		String value=getValue(tag,param,description);
		return value!=null? Float.parseFloat(value) : default_value;
	}

	/** Parses the array of strings for a given double option/parameter.
	 * The option/parameter is removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-a' or '--add'); use {@link #PARAM} or {@link #OPTIONAL_PARAM} to indicate that it is a parameter without tag
	 * @param default_value default array
	 * @param param parameter string used to represent the value in the help string
	 * @param description a description of this option/parameter for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the double value */
	public double getDouble(String tag, double default_value, String param, String description) {
		String value=getValue(tag,param,description);
		return value!=null? Double.parseDouble(value) : default_value;
	}

	/** Parses the array of strings for a given tuple option.
	 * The option is removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-v' or '--values')
	 * @param len the number of components of the tuple
	 * @param default_value default value
	 * @param param parameter string used to represent the value in the help string
	 * @param description a description of this option for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the string tuple */
	public String[] getStringTuple(String tag, int len, String[] default_value, String param, String description) {
		String[] tuple=getTuple(tag,len,param,description);
		return tuple!=null? tuple : default_value;
	}

	/** Parses the array of strings for an array option, providing an array of values.
	 * The option values are formed by the length of the of the array followed by the actual array values.
	 * The option is removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-v' or '--values')
	 * @param default_value default array
	 * @param param parameter string used to represent the value in the help string
	 * @param description a description of this option for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the array of values */
	public String[] getStringArray(String tag, String[] default_value, String param, String description) {
		String[] values=getArray(tag,param,description);
		return values!=null? values : default_value;
	}

	/** Parses the array of strings for all occurrences of given string option/parameter.
	 * The option/parameter occurrences are removed from the list of unparsed strings.
	 * @param tag option tag (e.g. '-a' or '--add')
	 * @param default_value default value
	 * @param param parameter string used to represent the value in the help string;
	 * @param description a description of this option/parameter for the help string; if it is <i>null</i>, no description is added to the help string
	 * @return the string value */
	/*public ArrayList<String> getAllStrings(String tag, String default_value, String param, String description) {
		ArrayList<String> list=getAllOccurrences(tag,param,description);
		if (list.size()==0) {
			if (default_value!=null) list.add(default_value);
			else return null;
		}
		return list;
	}*/

	/** Gets the remaining strings from the array.
	 * @param optional whether these parameters are optional 
	 * @param param string used to represent the parameter values
	 * @param description a description of these parameters
	 * @return the strings that has not been parsed */
	public String[] getRemainingStrings(boolean optional, String param, String description) {
		if (description!=null && param!=null) params.add(new Option(optional?OPTIONAL_PARAM:null,param,description));
		String[] ss=args.toArray(new String[]{});
		args.clear();
		return ss;
	}

	/** Gets the number of unparsed strings.
	 * @return the number of remaining strings */
	public int size() {
		return args.size();
	}

	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		final Option nopt=new Option(" !",null,"inverts the next option");
		// compute option-param part width
		//int oplen_max=0;
		int oplen_max=nopt.getTag().length();
		for (Option o : options) {
			int len=o.getTag().length();
			if (o.getParam()!=null) len+=1+o.getParam().length();
			if (len>oplen_max) oplen_max=len;
		}
		for (Option p : params) {
			int len=p.getParam().length();
			if (len>oplen_max) oplen_max=len;
		}
		// params
		if (params.size()>0) {
			sb.append("\r\n").append(TAB1).append("where:");			
			for (Option p : params) {
				sb.append("\r\n").append(TAB2).append(p.toString(oplen_max,TAB3));
			}	
		}
		// options
		if (options.size()>0) {
			sb.append("\r\n").append(TAB1).append("options:");
			Option[] sorted_options=options.toArray(new Option[]{});
			Arrays.sort(sorted_options,new Comparator<Option>(){
				@Override
				public int compare(Option o1, Option o2) {
					return o1.getTag().compareTo(o2.getTag());
				}});
			for (Option o : sorted_options) {
				sb.append("\r\n").append(TAB2).append(o.toString(oplen_max,TAB3));
			}
			if (not_option) {
				sb.append("\r\n").append(TAB2).append(nopt.toString(oplen_max,TAB3));
			}

		}
		return sb.toString();
	}
	
	/** Gets the usage description.
	 * @param main_class the main class
	 * @return a string like "Usage: java program [options] ..." */
	public String toUsageString(Class<?> main_class) {
		return toUsageString(main_class.getName());
	}

	/** Gets the usage description.
	 * @param program the main class
	 * @return a string like "Usage: java program [options] ..." */
	public String toUsageString(String program) {
		StringBuffer sb=new StringBuffer();
		if (FIRST_PARAMS_THEN_OPTIONS || options.size()==0) sb.append("Usage: java ").append(program);
		else sb.append("Usage: java ").append(program).append(" [options]");
		for (Option p : params) {
			if (p.getTag()==OPTIONAL_PARAM) sb.append(" [").append(p.getParam()).append("]");
			else sb.append(" ").append(p.getParam());
		}
		if (FIRST_PARAMS_THEN_OPTIONS && options.size()>0) sb.append(" [options]");
		sb.append(toString());
		return sb.toString();
	}
	
	
	// ************************ INNER CLASSES ************************
	
	/** Option.
	 */
	static class Option {
		
		/** Option tag */
		String tag;
		
		/** Parameter string */
		String param;
		
		/** Description */
		String description;
		
		/** Creates an option.
		 * @param tag option tag
		 * @param param a string representing the option parameter
		 * @param description a description */
		public Option(String tag, String param, String description) {
			this.tag=tag;
			this.param=param;
			this.description=description;
		}
		
		/** Gets the option tag. */
		public String getTag() {
			return tag;
		}
		
		/** Gets the string representing the option parameter. */
		public String getParam() {
			return param;
		}
		
		/** Gets the description. */
		public String getDescription() {
			return description;	
		}
		
		/** Gets a string representation of this option, with a fixed width of the 'opt-param' part.
		 * @param oplen minimum number of character of the 'opt-param' part
		 * @param sep separator between the 'opt-param' part and the 'description' part */
		public String toString(int oplen, String sep) {
			StringBuffer sb=new StringBuffer();
			if (tag!=null && tag!=PARAM && tag!=OPTIONAL_PARAM) {
				sb.append(tag);
				if (param!=null) sb.append(" ");
			}
			if (param!=null) sb.append(param);
			if (oplen>0) {
				int len=sb.length();
				for (int k=oplen-len; k>0; k--) sb.append(" ");
			}
			if (sep!=null) sb.append(sep);
			if (description!=null) sb.append(description);
			return sb.toString();
		}
		
		@Override
		public String toString() {
			return toString(0," : ");
		}
	}

}
