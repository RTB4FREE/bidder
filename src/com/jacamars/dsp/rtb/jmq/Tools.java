package com.jacamars.dsp.rtb.jmq;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Tools {



    public static String serialize2(ObjectMapper mapper, Object o) throws Exception {
        String type = o.getClass().getSimpleName();
        String str = null;
        switch(type) {
            case "Integer":
            case "Long":
           case "String":
            case "Boolean":
            case "Double":
            case "AtomicLong":
                str = RedisProto.Encode(
                        new String[]{ o.toString(),"AtomicLong"}
                );
                break;
            default:
                str = RedisProto.Encode(
                        new String[]{ mapper.writeValueAsString(o),type}
                );
        }
        return str;
    }

    public static Object[] deSerialize2(ObjectMapper mapper, String o) throws Exception {
        Object obj = null;
        String name = null;
        String[] tuple = RedisProto.Decode(o);
        switch(tuple[1]) {
            case "Integer":
                obj = new Integer(tuple[0]);
                break;
            case "Long":
                obj = new Long(tuple[0]);
                break;
            case "String":
                obj = tuple[0];
                break;
            case "Boolean":
                obj = new Boolean(tuple[0]);
                break;
            case "Double":
                obj = new Double(tuple[0]);
                break;
            case "AtomicLong":
                obj = new AtomicLong(Long.parseLong(tuple[0]));
                break;
        }

        Object [] pair = new Object[2];
        pair[0] = name;
        pair[1] = obj;
        return pair;

    }

    public static String serialize(ObjectMapper mapper, Object o) {
		String contents = null;
		StringBuilder sb = new StringBuilder();
		try {
			if (o instanceof String) {
				sb.append("\"");
				sb.append(o);
				sb.append("\"");
			}
			else {
				contents = mapper.writeValueAsString(o);
				sb.append(contents);
			}
			sb.setLength(sb.length() - 1);
			sb.append(",\"");
			sb.append("serialClass");
			sb.append("\":\"");
			sb.append(o.getClass().getName());
			sb.append("\"}");
			contents = sb.toString();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return contents;
	}

	public static Object[] deSerialize(ObjectMapper mapper, String o) throws Exception  {
		Object obj = null;
		String name = null;

		if (o.indexOf("\"serialClass") == -1) {
			Object [] pair = new Object[2];
			pair[0] = "String";
			pair[1] = o;
			return pair;
			//System.out.println("ERROR: CAN'T SERIALIZE: " + o);
			//throw new Exception("Can't deserialize: " + o);
		}
		if (o.charAt(0) == '{') {
			int i = o.indexOf("\"serialClass");
			StringBuilder sb = new StringBuilder(o);
			name = sb.substring(i + 15, sb.length() - 2);
			sb.setLength(i - 1);
			sb.append("}");
			String contents = sb.toString();
			try {
				obj = mapper.readValue(contents, Class.forName(name));
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else 
		if (o.charAt(0) == '"') {
			int i = o.indexOf("\"serialClass");
			StringBuilder sb = new StringBuilder(o);
			name = sb.substring(i + 15, sb.length() - 2);
			obj = sb.substring(2, i - 1);
		} else {
			int i = o.indexOf("\"serialClass");
			StringBuilder sb = new StringBuilder(o);
			name = sb.substring(i + 15, sb.length() - 2);
			String contents = sb.substring(0, i - 1);
			try {
				if (name.contains("Double") && contents.endsWith(".")) {
					sb = new StringBuilder(contents);
					sb.append("0");
					contents = sb.toString();
				}
				obj = mapper.readValue(contents, Class.forName(name));
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Object [] pair = new Object[2];
		pair[0] = name;
		pair[1] = obj;
		return pair;
	}
}
