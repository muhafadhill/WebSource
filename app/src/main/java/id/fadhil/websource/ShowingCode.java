package id.fadhil.websource;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ASUS on 20/10/2017.
 */

public class ShowingCode extends AsyncTaskLoader<String> {
	Context context;
	String url;
	private ProgressDialog dialog;

	public ShowingCode(Context context, String url) {
		super(context);
		this.url = url;
	}

	@Override
	protected void onStartLoading() {
		forceLoad();
	}

	@Override
	public String loadInBackground() {
		InputStream inputStream;

		try {
			URL url1 = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
			connection.setReadTimeout(3000);
			connection.setConnectTimeout(3000);
			connection.setRequestMethod("GET");
			connection.connect();

			inputStream = connection.getInputStream();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuffer stringBuffer = new StringBuffer();
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line + "\n");
			}

			bufferedReader.close();
			inputStream.close();

			//dialog.dismiss();

			return stringBuffer.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "URL tidak valid";
	}
}
