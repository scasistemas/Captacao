package br.com.mobiwork.captacao.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by LuisGustavo on 01/02/2016.
 */
public class utilBluetooth {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private Context ctx;
    Activity habBT ;
    public static String BTENCONTRADO="IMPRESSORA ENCONTRADA";
    public static String BTINDISPONIVEL="BLUETOOTH INDISPONIVEL";
    public static String BTOPEN="CONEXÃO ABERTA";
    public static String BTENVIADO="ENVIADO COM SUCESSO";

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    public utilBluetooth(Context ctx){
        this.ctx=ctx;
        habBT= (Activity)ctx;
    }

    public String findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return BTINDISPONIVEL;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            habBT.startActivityForResult(enableBluetooth, 0);
            }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                    // MP300 is the name of the bluetooth printer device
                String name=device.getName();
                if (device.getName().equals("MPT-II")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        return BTENCONTRADO;

    }

    public boolean  BTopen(Activity sinc) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            // Standard SerialPortService ID
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);

        try {
            mmSocket.connect();
        }catch (IOException e) {
                mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
                mmSocket.connect();

        }
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        return beginListenForData();
        //return false;
    }

    boolean beginListenForData() {
            // This is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            final Thread workerThread;
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
        workerThread.start();
        return stopWorker;
    }

    public String sendData(File file) throws IOException {
        try {
            FileInputStream f = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(f);
            try {
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                // we need to know how may bytes were read to write them to the byteBuffer
                int len = 0;
                while ((len = bis.read(buffer)) != -1) {
                    mmOutputStream.write(buffer, 0, len);
                }
            } finally {
                f.close();
                bis.close();
            }
            // tell the user data were sent
            return BTENVIADO;

        } catch (NullPointerException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();

        }
    }
    public void close() throws IOException {
        stopWorker = true;
        if(mmOutputStream!=null) {
            mmOutputStream.close();
        }
        if(mmInputStream!=null) {
            mmInputStream.close();
        }
        if(mmSocket!=null) {
            mmSocket.close();
        }
    }




}
