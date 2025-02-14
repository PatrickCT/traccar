package org.traccar.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.TimeZone;

public class SitrackAuthGenerator {
    public static String createAuthorization(String name, String secret){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long unixTimestamp = timestamp.getTime()/1000;
        String header = null;
        try {
            byte[] data = String.format("%s%s%s", name, secret, unixTimestamp).getBytes(StandardCharsets.UTF_8);
            MessageDigest digester = MessageDigest.getInstance("MD5");
            digester.update(data,0,data.length);
            byte[] firstEncode = digester.digest();
            String hashed = Base64.getEncoder().encodeToString(firstEncode);
            header = String.format("SWSAuth application=\"%s\",signature=\"%s\",timestamp=\"%s\"", name, hashed, unixTimestamp);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  header;
    }
}
