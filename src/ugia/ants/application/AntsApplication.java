package ugia.ants.application;

import ugia.ants.engine.services.AntServiceManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class AntsApplication extends Application {

    private static AntsApplication sInstance;
    public static boolean bound = false;
    public static AntServiceManager antsService;

    /**
     * Returns class instance
     * 
     * @return instance
     */
    public synchronized static AntsApplication getInstance() {
	return sInstance;
    }

    private final ServiceConnection connection = new ServiceConnection() {

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {

	    AntServiceManager.AntsBinder binder = (AntServiceManager.AntsBinder) service;
	    antsService = binder.getService();

	    bound = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
	    bound = false;
	}
    };

    @Override
    public void onCreate() {

	super.onCreate();
	sInstance = this;

	Intent i = new Intent(this, AntServiceManager.class);
	bindService(i, connection, Context.BIND_AUTO_CREATE);
    }
}