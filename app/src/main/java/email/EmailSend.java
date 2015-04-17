package email;

import android.os.AsyncTask;

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
 * Created by Huy on 4/8/2015.
 */
public class EmailSend {
    private static final String EMAIL_LINK = "http://androidclass.uphero.com/email.php";
    private String email;
    private String password;

    public EmailSend() {
    }


    public void sendEmail(final String theEmail, final String newPassword) {
        email = theEmail;
        password = newPassword;
        new sendEmailInBackground().execute();
    }

    class sendEmailInBackground extends AsyncTask<Void, Void, Void> {
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
            return null;
        }
    }
}
