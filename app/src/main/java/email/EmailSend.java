/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package email;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * EmailSend and it's commensurate php file hosted on an outside server were developed by Huy on 4/8/2015.
 */
public class EmailSend {
    private static final String EMAIL_LINK = "http://cssgate.insttech.washington.edu/~eprokhor/emailreset.php";
    private String email;
    private String password;

    /**
     * No arg constructor for an EmailSend object.
     * Avoids accidental instantiation.
     */
    public EmailSend() {
        //do nothing constructor to avoid instantiation
    }


    /**
     * Calling code passes in 2 parameters to send off an email.
     *
     * @param theEmail    is the users current email to which the new password will be sent
     * @param newPassword is the newly generated hash string created in the calling code
     */
    public void sendEmail(final String theEmail, final String newPassword) {
        email = theEmail;
        password = newPassword;
        new sendEmailInBackground().execute();
    }

    /**
     * Private inner class that runs a separate thread to generate email messages
     * containing the new password for the user.
     *
     * @author Huy Ngo 4/8/2015
     */
    private class sendEmailInBackground extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... bleh) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(EMAIL_LINK);

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("email", email));
            nameValuePair.add(new BasicNameValuePair("password", password));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                e.printStackTrace();
            }

            String result = "failed";
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("Email service", result);
            return null;
        }
    }
}
