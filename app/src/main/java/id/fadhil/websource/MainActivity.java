package id.fadhil.websource;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

	public TextView tvSourceCode;
	public Button btnRefresh, btnShowCode, btnReset;
	TextView tvUrl;
	private EditText edtUrl;
	public Spinner spinner;
	ProgressDialog dialog;
	Runnable progress;
	Handler handler;


	String[] url = new String[]{"pilih protocol","http://","https://"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();

//		final Spinner spinner = new Spinner(this);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,url);
		spinner.setAdapter(spinnerAdapter);

		dialog = new ProgressDialog(this);
		dialog.setMessage("Loading ...");
		dialog.setCancelable(false);

		//event handling progress bar dengan mengatur waktu
		handler = new Handler();
		progress = new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
				tvSourceCode.setText("Periksa Internet anda dan coba lagi");
				tvSourceCode.setGravity(View.TEXT_ALIGNMENT_CENTER);
				btnRefresh.setVisibility(View.VISIBLE);
				btnShowCode.setEnabled(false);
			}
		};

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
				Log.e("Error" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
			}
		});

		if (getSupportLoaderManager().getLoader(0) != null) {
			getSupportLoaderManager().initLoader(0, null, this);
		}

		//event ketika button tampilkan diklik
		btnShowCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doAction();
			}
		});

		//event ketika button refresh diklik (muncul ketika tidak ada koneksi)
		btnRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvSourceCode.setText(null);
				btnRefresh.setVisibility(View.GONE);
				btnShowCode.setEnabled(true);

				doAction();
			}
		});

		//event ketika edittext diklik (menampilkan drop down spinner)
//		edtUrl.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				spinner.performClick();
//			}
//		});

		//event ketika klik enter pada virtual keyboard di edittext
		edtUrl.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction()==KeyEvent.ACTION_DOWN){
					switch (keyCode){
						case KeyEvent.KEYCODE_DPAD_CENTER:
						case KeyEvent.KEYCODE_ENTER:
							doAction();
							return true;
						default:
							break;
					}
				}
				return false;
			}
		});

		//event ketika item pada spinner dipilih
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(spinner.getSelectedItemPosition()!=0){
					tvUrl.setText(spinner.getSelectedItem().toString());

					//mengarahkan kursor ke huruf akhir edittext
					//edtUrl.setSelection(edtUrl.getText().length());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		//event ketika button reset diklik
		btnReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvUrl.setText(null);
				edtUrl.setText(null);
				tvSourceCode.setText(null);
			}
		});
	}

	//method proses/aksi ketika tombol diklik
	private void doAction() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		//validasi editText kosong atau tidak
		if (!validasiEditText()) {
			return;
		}

		dialog.show();

		//validasi koneksi
		if (!checkConnection()) {
			handler.postDelayed(progress, 5000);
			return;
		}

		Bundle bundle = new Bundle();
		bundle.putString("url", tvUrl.getText().toString()+""+edtUrl.getText().toString());
		getSupportLoaderManager().restartLoader(0, bundle, MainActivity.this);
	}

	@Override
	public Loader<String> onCreateLoader(int id, Bundle args) {
		return new ShowingCode(this, args.getString("url"));
	}

	@Override
	public void onLoadFinished(Loader<String> loader, String data) {
		tvSourceCode.setVisibility(View.VISIBLE);
		if(data.length()==0){
			tvSourceCode.setText("ERROR");
		}else{
			tvSourceCode.setText(data);
		}
		dialog.dismiss();
	}

	@Override
	public void onLoaderReset(Loader<String> loader) {

	}

	//method cek koneksi
	public boolean checkConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	//method validasi ketika editText kosong
	public boolean validasiEditText() {
		if (edtUrl.getText().toString().isEmpty()) {
			edtUrl.requestFocus();
			edtUrl.setError("Masukkan url");
			return false;
		}
		return true;
	}

	//method findviewbyme
	private void initView() {
		edtUrl = (EditText) findViewById(R.id.edt_url);
		btnShowCode = (Button) findViewById(R.id.btn_showCode);
		tvSourceCode = (TextView) findViewById(R.id.tv_sourceCode);
		btnRefresh = (Button) findViewById(R.id.btn_refresh);
		btnReset = (Button) findViewById(R.id.btn_reset);
		spinner = (Spinner) findViewById(R.id.spinner);
		tvUrl = (TextView) findViewById(R.id.tv_url);
	}
}
