package br.com.mobiwork.captacao.impBluetooth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import br.com.mobiwork.captacao.R;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;
import br.com.mobiwork.captacao.dao.DaoMotorista;
import br.com.mobiwork.captacao.info.AsyncResponse;
import br.com.mobiwork.captacao.model.Motorista;
import br.com.mobiwork.captacao.sinc.EnvImp;
import br.com.mobiwork.captacao.sinc.SincDown;
import br.com.mobiwork.captacao.util.ArquivoTxt;
import br.com.mobiwork.captacao.util.FolderConfig;
import br.com.mobiwork.captacao.util.SharedPreferencesUtil;

public class Bluetooth_main extends Activity  implements AsyncResponse {
	private TextView mStatusTv;
	private Button mActivateBtn;
	private Button mPairedBtn;
	private Button mScanBtn;
	private Button btn_pag_teste;
	private SQLiteDatabase dbC;
	private DaoCreateDBC daoDBC;
	private Motorista config;
	private CheckBox ckHabBlue;
	SharedPreferencesUtil sdu;
	
	private ProgressDialog mProgressDlg;
	
	private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
	
	private BluetoothAdapter mBluetoothAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bluetooth_main);

		daoDBC = new DaoCreateDBC(this);
		dbC=daoDBC.getWritableDatabase();
		config= new DaoMotorista(this).consultar(this.dbC);
		 sdu = new SharedPreferencesUtil(getBaseContext());

		
		mStatusTv 			= (TextView) findViewById(R.id.tv_status);
		mActivateBtn 		= (Button) findViewById(R.id.btn_enable);
		mPairedBtn 			= (Button) findViewById(R.id.btn_view_paired);
		mScanBtn 			= (Button) findViewById(R.id.btn_scan);
		ckHabBlue 			= (CheckBox) findViewById(R.id.ckHabBlue);
		ckHabBlue.setChecked(sdu.getImpBT());
		btn_pag_teste=(Button)findViewById(R.id.btn_pag_teste);
		
		mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();
		
		mProgressDlg 		= new ProgressDialog(this);
		
		mProgressDlg.setMessage("Scanning...");
		mProgressDlg.setCancelable(false);
		mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        
		        mBluetoothAdapter.cancelDiscovery();
		    }
		});
		
		if (mBluetoothAdapter == null) {
			showUnsupported();
		} else {
			mPairedBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
					
					if (pairedDevices == null || pairedDevices.size() == 0) { 
						showToast("Não foi encontrado dispositivos pareados");
					} else {
						ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
						
						list.addAll(pairedDevices);
						
						Intent intent = new Intent(Bluetooth_main.this, DeviceListActivity.class);
						
						intent.putParcelableArrayListExtra("device.list", list);
						
						startActivity(intent);						
					}
				}
			});
			
			mScanBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mBluetoothAdapter.startDiscovery();
				}
			});
			
			mActivateBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mBluetoothAdapter.isEnabled()) {
						mBluetoothAdapter.disable();
						
						showDisabled();
					} else {
						Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						
					    startActivityForResult(intent, 1000);
					}
				}
			});
			
			if (mBluetoothAdapter.isEnabled()) {
				showEnabled();
			} else {
				showDisabled();
			}
		}
		
		IntentFilter filter = new IntentFilter();
		
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		
		registerReceiver(mReceiver, filter);

		ckHabBlue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						sdu.setImpBT(ckHabBlue.isChecked());
					}
		}
		);
	}
	
	@Override
	public void onPause() {
		if (mBluetoothAdapter != null) {
			if (mBluetoothAdapter.isDiscovering()) {
				mBluetoothAdapter.cancelDiscovery();
			}
		}
		
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		
		super.onDestroy();
	}
	
	private void showEnabled() {
		mStatusTv.setText(getResources().getString(R.string.text_bluetooth_on));
		mStatusTv.setTextColor(Color.BLUE);
		
		mActivateBtn.setText(getResources().getString(R.string.text_disable));
		mActivateBtn.setEnabled(true);
		
		mPairedBtn.setEnabled(true);
		mScanBtn.setEnabled(true);
		btn_pag_teste.setEnabled(true);
	}
	
	private void showDisabled() {
		mStatusTv.setText(getResources().getString(R.string.text_bluetooth_off));
		mStatusTv.setTextColor(Color.RED);
		
		mActivateBtn.setText(getResources().getString(R.string.text_enable));
		mActivateBtn.setEnabled(true);
		
		mPairedBtn.setEnabled(false);
		mScanBtn.setEnabled(false);
	}
	
	private void showUnsupported() {
		mStatusTv.setText("Bluetooth não é suportado para este dispositivo");
		
		mActivateBtn.setText(getResources().getString(R.string.text_enable));
		mActivateBtn.setEnabled(false);
		
		mPairedBtn.setEnabled(false);
		mScanBtn.setEnabled(false);
	}
	
	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        
	        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
	        	final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
	        	 
	        	if (state == BluetoothAdapter.STATE_ON) {
	        		showToast("Enabled");
	        		 
	        		showEnabled();
	        	 }
	        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	        	mDeviceList = new ArrayList<BluetoothDevice>();
				
				mProgressDlg.show();
	        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

	        	mProgressDlg.dismiss();
	        	
	        	Intent newIntent = new Intent(Bluetooth_main.this, DeviceListActivity.class);
	        	
	        	newIntent.putParcelableArrayListExtra("device.list", mDeviceList);
				
				startActivity(newIntent);
	        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	        	BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		        
	        	mDeviceList.add(device);
	        	
	        	showToast("Dispositivo encontrado " + device.getName());
	        }
	    }
	};

	public void impteste(View v){
		EnvImp ei = new EnvImp(this, config,true,null);
		ei.delegate =  Bluetooth_main.this;
		ei.execute(new String[0]);
	}

	@Override
	public void processFinish(String output) {

	}
}