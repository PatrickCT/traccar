 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.LoggerFactory;

/**
 *
 * @author USER
 */
public class GeneralUtils {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(GeneralUtils.class);

    public static Date dateToUTC(Date date) {
        return new Date(date.getTime() - Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    } 

    public static String genericPOST(String url, String json, Map<String, Object> headers, int timeoutInSeconds) throws IOException {
        try {
            // Configure request timeout
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(Timeout.ofSeconds(timeoutInSeconds))
                    .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutInSeconds))
                    .build();

            CloseableHttpClient client = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();

            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            if (headers.isEmpty()) {
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
            } else {
                headers.forEach(httpPost::addHeader);
            }

            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");
            LOG.info("Generic post to " + url + " response");
            LOG.info(responseString);
            client.close();

            return responseString;
        } catch (org.apache.hc.core5.http.ParseException ex) {
            // Handle exception
            System.out.println("error generic post");
            System.out.println(ex.getMessage());
            System.out.println(ex);
            ex.printStackTrace();
        }
        return "";
    }

}
