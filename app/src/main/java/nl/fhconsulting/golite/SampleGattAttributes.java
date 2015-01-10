package nl.fhconsulting.golite;



import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String FIRMWARE = "00002a26-0000-1000-8000-00805f9b34fb";

    public static String WW_SERVICE = "af237777-879d-6186-1f49-deca0e85d9c1";
    public static String WW0 = "af230000-879d-6186-1f49-deca0e85d9c1";
    public static String WW1 = "af230001-879d-6186-1f49-deca0e85d9c1";
    public static String WW2 = "af230002-879d-6186-1f49-deca0e85d9c1";
    public static String WW3 = "af230003-879d-6186-1f49-deca0e85d9c1";
    public static String WW4 = "af230004-879d-6186-1f49-deca0e85d9c1";
    public static String WW5 = "af230005-879d-6186-1f49-deca0e85d9c1";
    public static String WW6 = "af230006-879d-6186-1f49-deca0e85d9c1";

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    //public static UUID CLIENT_CHARACTERISTIC_CONFIG = new UUID(45088566677504L, -9223371485494954757L); //from GO apk

    static {
        // Sample Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic attribute");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information");
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");

        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");


        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        attributes.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Privacy Flag");
        attributes.put("00002a03-0000-1000-8000-00805f9b34fb", "Reconnection Address");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters");
        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID" );
        attributes.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String");
        attributes.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String");
        attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
        attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
        attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
        attributes.put("00002a2a-0000-1000-8000-00805f9b34fb", "IEEE 11073-20601 Regulatory");

        attributes.put("af237777-879d-6186-1f49-deca0e85d9c1", "WW_SERVICE");
        attributes.put("af230000-879d-6186-1f49-deca0e85d9c1", "WW0");
        attributes.put("af230001-879d-6186-1f49-deca0e85d9c1", "WW1");
        attributes.put("af230002-879d-6186-1f49-deca0e85d9c1", "WW2");
        attributes.put("af230003-879d-6186-1f49-deca0e85d9c1", "WW3");
        attributes.put("af230004-879d-6186-1f49-deca0e85d9c1", "WW4");
        attributes.put("af230005-879d-6186-1f49-deca0e85d9c1", "WW5");
        attributes.put("af230006-879d-6186-1f49-deca0e85d9c1", "WW6");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
