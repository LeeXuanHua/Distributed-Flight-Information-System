package com.example.demo.utils;

import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

import com.example.demo.server.servant.models.Time;

@Slf4j
public class MarshallUtil {
    public static final char KV_PAIR = ':';
    public static final char DELIMITER = '|';
    public static final int BOOL_SIZE = 1;
    public static final int INT_SIZE = 4;
    public static final int LONG_SIZE = 8;
    public static final int FLOAT_SIZE = 4;
    public static final int DOUBLE_SIZE = 8;
    public static final boolean hostIsBigEndian = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);

    public static byte[] marshall(Object obj) {
       List<Byte> message = new ArrayList<Byte>();

       // Parsing the object and appending the message
       marshallParsing(message, obj);

       // Convert the list of Bytes to 1 array of Bytes
       return MarshallUtil.byteUnboxing(message);
    }

    public static byte[] marshall(Optional obj) {
        List<Byte> message = new ArrayList<Byte>();

        // Check if the object is present
        // If present - append the Optional class name and 0 to the message, and process it as per normal
        // If not present - append the Optional class name and 0 to the message, and terminate
        if (obj.isPresent()) {
            // Append the classname and number of objects to the message
            appendMessage(message, obj.getClass().getTypeName());
            appendMessage(message, 1);

            // Parsing the object and appending the message
            marshallParsing(message, obj.get());
        } else {
            // Append the classname and number of objects to the message
            appendMessage(message, obj.getClass().getTypeName());
            appendMessage(message, 0);
        }

        // Convert the list of Bytes to 1 array of Bytes
        return MarshallUtil.byteUnboxing(message);
    }

    private static void marshall(Optional obj, List<Byte> message) {
        // Check if the object is present
        // If present - append the Optional class name and 0 to the message, and process it as per normal
        // If not present - append the Optional class name and 0 to the message, and terminate
        if (obj.isPresent()) {
            // Append the classname and number of objects to the message
            appendMessage(message, obj.getClass().getTypeName());
            appendMessage(message, 1);

            // Parsing the object and appending the message
            marshallParsing(message, obj.get());
        } else {
            // Append the classname and number of objects to the message
            appendMessage(message, obj.getClass().getTypeName());
            appendMessage(message, 0);
        }
    }

    public static byte[] marshall(List<Object> objList) {
        List<Byte> message = new ArrayList<Byte>();

        // Append the classname and number of objects to the message
        appendMessage(message, objList.getClass().getTypeName());
        appendMessage(message, objList.size());

        // Parsing the object and appending the message
        for (Object obj : objList)
            marshallParsing(message, obj);

        // Convert the list of Bytes to 1 array of Bytes
        return MarshallUtil.byteUnboxing(message);
    }

    private static void marshall(List<Object> objList, List<Byte> message) {
        // Append the classname and number of objects to the message
        appendMessage(message, objList.getClass().getTypeName());
        appendMessage(message, objList.size());

        // Parsing the object and appending the message
        for (Object obj : objList)
            marshallParsing(message, obj);
    }

    private static void marshallParsing(List<Byte> message, Object obj) {
        String className = obj.getClass().getTypeName();

        // If the class is Optional (e.g. nested Optionals)
        if (className.equals(Optional.class.getTypeName())) {
            marshall((Optional) obj, message);
            return;
            // If the class is ArrayList (e.g. nested ArrayLists)
        } else if (className.equals(ArrayList.class.getTypeName())) {
            marshall((List<Object>) obj, message);
            return;
        } else {
            // Append the class name to the message
            appendMessage(message, className);
        }

        // Extract all fields (type and name) from the object and marshall them based on their types
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object o = field.get(obj);  // Returns the value of the field in the initialised object
                String type = field.getGenericType().getTypeName().split("[<>]")[0];    // Returns the type of the field

                // Based on the field type, call the appropriate method
                // Size is appended to message and its field value is marshalled
                switch (type) {
                    case "java.lang.Boolean", "boolean" -> appendMessage(message, (boolean) o);
                    case "java.lang.String", "String" -> appendMessage(message, (String) o);
                    case "java.lang.Integer", "int" -> appendMessage(message, (int) o);
                    case "java.lang.Long", "long" -> appendMessage(message, (long) o);
                    case "java.lang.Float", "float" -> appendMessage(message, (float) o);
                    case "java.lang.Double", "double" -> appendMessage(message, (double) o);
                    case "java.time.LocalDateTime", "LocalDateTime" -> {
                        // Convert LocalDateTime to our custom Time class (as required by project)
                        Time t = Time.fromDateTime((LocalDateTime) o);
                        marshallParsing(message, t);
                    }
                    default -> {
                        // Check if the object is Optional
                        if (o.getClass().getTypeName() == "java.util.Optional" || o.getClass().getTypeName() == "Optional") {
                            marshall((Optional) o, message);

                        // Check if the object is Optional
                        } else if (o.getClass().getTypeName() == "java.util.ArrayList" || o.getClass().getTypeName() == "ArrayList") {
                            marshall((List<Object>) o, message);

                        // If not, assume it is a nested class
                        } else {
                            marshallParsing(message, o);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object unmarshall(byte[] b) {
        int ptr = 0;    // Pointer to the current position in the byte array

        // Parsing the object and populating object fields
        Map<Object, Integer> objectAndPtr = unmarshallParsing(b, ptr);

        // Extract the object and the latest pointer from the HashMap
        Object obj = objectAndPtr.keySet().iterator().next();

        return obj;
    }

    private static Map<Object, Integer> unmarshallParsing(byte[] b, int ptr) {
        // Create a HashMap to store the object and the latest pointer
        Map<Object, Integer> objectAndPtr = new HashMap<>();

        // Extract the object type name (string) from the byte array
        int classNameLength = unmarshallInt(b, ptr);
        ptr += INT_SIZE;
        String className = unmarshallString(b, ptr, ptr+classNameLength);
        ptr += classNameLength;

        // Create an instance of the object based on the class name
        Object obj = null;
        try {
            // If the class is Optional
            if (className.equals(Optional.class.getTypeName())) {
                // Optional classes cannot be instantiated using the default constructor (therefore, we need to handle this specially)
                // For optional classes, we need to check if the values are present
                // If so, we can safely create an Optional object amd recursively call unmarshallParsing
                // Otherwise, we can just return an empty Optional object

                // Recall the format: <classname length> <classname> <INT_SIZE> <isPresent()>
                int nextByteToRead = unmarshallInt(b, ptr); // exhaust the byte denoting <INT_SIZE>
                ptr += INT_SIZE;
                int available = unmarshallInt(b, ptr); // this is the byte that tells us if the optional object is present or not
                ptr += INT_SIZE;

                if (available == 0) {   // if the optional object is not present
                    obj = Optional.empty();

                } else {                // if the optional object is present
                    Map<Object, Integer> optionalObjAndPtr = unmarshallParsing(b, ptr);
                    obj = Optional.of(optionalObjAndPtr.keySet().iterator().next());
                    ptr = optionalObjAndPtr.values().iterator().next();
                }

                objectAndPtr.put(obj, ptr);
                return objectAndPtr;

            // If the class is ArrayList
            } else if (className.equals(ArrayList.class.getTypeName())) {
                // ArrayList classes cannot be instantiated using the default constructor
                // For ArrayList, we create an empty ArrayList and recursively call unmarshallParsing, adding the objects to the ArrayList

                // Recall the format: <classname length> <classname> <INT_SIZE> <# of elements>
                int nextByteToRead = unmarshallInt(b, ptr); // exhaust the byte denoting <INT_SIZE>
                ptr += INT_SIZE;
                int elemCount = unmarshallInt(b, ptr); // this is the byte that tells us if the ArrayList object is present or not
                ptr += INT_SIZE;

                // Create an empty ArrayList
                List<Object> arrList = new ArrayList<>();

                for (int i = 0; i < elemCount; i++) {
                    Map<Object, Integer> arrayListObjAndPtr = unmarshallParsing(b, ptr);
                    obj = arrayListObjAndPtr.keySet().iterator().next();
                    ptr = arrayListObjAndPtr.values().iterator().next();

                    arrList.add(obj);
                }

                objectAndPtr.put(arrList, ptr);
                return objectAndPtr;

            // For all other classes (as of now, assuming they are custom classes)
            } else {
                obj = Class.forName(className).getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }

        // Extract all fields (type and name) from the object and unmarshall them based on their types
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String type = field.getGenericType().getTypeName().split("[<>]")[0];    // Returns the type of the field

            // Size (int) is extracted from the byte array and its field value is unmarshalled (Useful for Strings as size is variable)
            int sourceLength = unmarshallInt(b, ptr);
            ptr += INT_SIZE;

            switch (type) {
                case "java.lang.Boolean", "boolean" -> {
                    boolean booleanValue = unmarshallBoolean(b, ptr);
                    ptr += sourceLength;
                    set(obj, field.getName(), booleanValue);
                }
                case "java.lang.String", "String" -> {
                    String stringValue = unmarshallString(b, ptr, ptr + sourceLength);
                    ptr += sourceLength;
                    set(obj, field.getName(), stringValue);
                }
                case "java.lang.Integer", "int" -> {
                    int intValue = unmarshallInt(b, ptr);
                    ptr += sourceLength;
                    set(obj, field.getName(), intValue);
                }
                case "java.lang.Long", "long" -> {
                    long longValue = unmarshallLong(b, ptr);
                    ptr += sourceLength;
                    set(obj, field.getName(), longValue);
                }
                case "java.lang.Float", "float" -> {
                    float floatValue = unmarshallFloat(b, ptr);
                    ptr += sourceLength;
                    set(obj, field.getName(), floatValue);
                }
                case "java.lang.Double", "double" -> {
                    double doubleValue = unmarshallDouble(b, ptr);
                    ptr += sourceLength;
                    set(obj, field.getName(), doubleValue);
                }
                case "java.time.LocalDateTime", "LocalDateTime" -> {    // Used LocalDateTime here instead of Time because we are inspecting the original class
                    // Unmarshall the Time object and convert it to LocalDateTime
                    Map<Object, Integer> nestedObjAndPtr = unmarshallParsing(b, ptr-4);
                    Object nestedObj = nestedObjAndPtr.keySet().iterator().next();
                    ptr = nestedObjAndPtr.get(nestedObj);
                    LocalDateTime localDateTime = ((Time) nestedObj).toDateTime();
                    set(obj, field.getName(), localDateTime);
                }
                default -> {
                    // Backtrack the ptr 4 bytes since we extracted the sourceLength earlier
                    Map<Object, Integer> nestedObjAndPtr = unmarshallParsing(b, ptr-4);
                    Object nestedObj = nestedObjAndPtr.keySet().iterator().next();
                    ptr = nestedObjAndPtr.get(nestedObj);
                    set(obj, field.getName(), nestedObj);
                }
            }
        }

        // Add the object and the latest pointer to the HashMap
        objectAndPtr.put(obj, ptr);

        return objectAndPtr;
    }

    private static void set(Object object, String fieldName, Object fieldValue) {
        // Sets a field of an object to a value and makes it accessible if it is not, using reflection
        Class<?> refClass = object.getClass();
        while (refClass != null) {
            try {
                Field field = refClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                refClass = null;
            } catch (NoSuchFieldException e) {
                refClass = refClass.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static byte[] marshall(boolean value) {
        return new byte[]{(byte) (value ? 1 : 0)};
    }

    private static boolean unmarshallBoolean(byte[] b, int start) {
        return b[start] == 1;
    }

    private static byte[] marshall(int value) {
        return new byte[]{
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }
    private static int unmarshallInt(byte[] b, int start) {
        return (b[start] & 0xFF) << 24 |
                (b[start + 1] & 0xFF) << 16 |
                (b[start + 2] & 0xFF) << 8 |
                (b[start + 3] & 0xFF);
    }

    private static byte[] marshall(long value) {
        byte[] bytes = new byte[]{
                (byte) ((value >> 56) & 0xFF),
                (byte) ((value >> 48) & 0xFF),
                (byte) ((value >> 40) & 0xFF),
                (byte) ((value >> 32) & 0xFF),
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
        return bytes;
    }
    private static long unmarshallLong(byte[] b, int start) {
        return (b[start] & 0xFFL) << 56 |
                (b[start + 1] & 0xFFL) << 48 |
                (b[start + 2] & 0xFFL) << 40 |
                (b[start + 3] & 0xFFL) << 32 |
                (b[start + 4] & 0xFFL) << 24 |
                (b[start + 5] & 0xFFL) << 16 |
                (b[start + 6] & 0xFFL) << 8 |
                (b[start + 7] & 0xFFL);
    }

    private static byte[] marshall(float value) {
        return marshall(Float.floatToIntBits(value));    // floatToIntBits() is in accordance to IEEE 754
    }

    private static float unmarshallFloat(byte[] b, int start) {
        return Float.intBitsToFloat(unmarshallInt(b, start));   // intBitsToFloat() is in accordance to IEEE 754
    }

    private static byte[] marshall(double value) {
        return marshall(Double.doubleToLongBits(value));    // doubleToLongBits() is in accordance to IEEE 754
    }

    private static double unmarshallDouble(byte[] b, int start) {
        return Double.longBitsToDouble(unmarshallLong(b, start));   // longBitsToDouble() is in accordance to IEEE 754
    }

    private static byte[] marshall(String s) {
        byte[] res = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            res[i] = (byte) s.charAt(i);
        }
        return res;
    }

    private static String unmarshallString(byte[] b, int start, int end) {
        char[] c = new char[end - start];
        for (int i = start; i < end; i++) {
            c[i - start] = (char) (b[i]);
        }
        return new String(c);
    }

    private static Byte[] byteBoxing(byte[] b) {
        // Box the byte array into a Byte array to gain access to wrapper classes methods
        // In actual, this is used because of our implementation - where we store the byte array in a List<Byte>
        Byte[] res = new Byte[b.length];
        for (int i = 0; i < b.length; i++)
            res[i] = Byte.valueOf(b[i]);
        return res;
    }

    private static byte[] byteUnboxing(Byte[] b) {
        // Unbox the Byte array into a byte array to gain access to primitive methods
        // In actual, this is used because of our implementation - where we store the byte array in a List<Byte>
        byte[] res = new byte[b.length];
        for (int i = 0; i < b.length; i++)
            res[i] = b[i].byteValue();
        return res;
    }

    private static byte[] byteUnboxing(List list) {
        // Unbox the Byte array into a byte array to gain access to primitive methods
        // Called right before marshaller finish marshalling to ensure data sent is in byte array form
        return MarshallUtil.byteUnboxing((Byte[]) list.toArray(new Byte[list.size()]));
    }

    private static void appendMessage(List<Byte> list, boolean x) {
        // Append the size of the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(BOOL_SIZE))));
        // Append the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(x))));
    }

    private static void appendMessage(List<Byte> list, int x) {
        // Append the size of the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(INT_SIZE))));
        // Append the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(x))));
    }

    private static void appendMessage(List<Byte> list, long x) {
        // Append the size of the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(LONG_SIZE))));
        // Append the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(x))));
    }

    private static void appendMessage(List<Byte> list, float f) {
        // Append the size of the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(FLOAT_SIZE))));
        // Append the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(f))));
    }

    private static void appendMessage(List<Byte> list, double f) {
        // Append the size of the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(DOUBLE_SIZE))));
        // Append the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(f))));
    }

    private static void appendMessage(List<Byte> list, String s) {
        // Append the size of the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(s.length()))));
        // Append the message
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(s))));
    }

    private static void append(List<Byte> list, int x) {
        // Append the message without size
        list.addAll(Arrays.asList(MarshallUtil.byteBoxing(MarshallUtil.marshall(x))));
    }

}
