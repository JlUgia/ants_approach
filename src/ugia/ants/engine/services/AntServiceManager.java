package ugia.ants.engine.services;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ugia.ants.application.AntsApplication;
import ugia.ants.engine.core.AntRequest;
import ugia.ants.engine.object.AntListenableFuture;
import ugia.ants.engine.object.AntUpdateCallback;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class AntServiceManager extends Service {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final int ANT_AVERAGE_WEIGHT_IN_MG = 3;

    private double totalAmountOfFood;

    private final IBinder mBinder = new AntsBinder();

    private LinkedBlockingQueue<Runnable> mQueue;

    private volatile ListeningExecutorService mExecutorService;

    public class AntsBinder extends Binder {
	public AntServiceManager getService() {
	    return AntServiceManager.this;
	}
    }

    /* METHODS FOR CLIENTS */
    public ListenableFuture<Double> enqueueRequest(final AntRequest request) {
	AntListenableFuture<Double> ftask = AntListenableFuture.create(request);
	mExecutorService.execute(ftask);

	return ftask;
    }

    public void startSendingAnts(Context _c, final int numberOfAnts, final int distanceToFood,
	    final AntUpdateCallback<Double> updateCallback) {

	totalAmountOfFood = 0;
	stopSendingAnts();

	AntRequest request;
	ListenableFuture<Double> ftask;

	for (int i = 0; i < numberOfAnts; i++) {
	    request = new AntRequest(ANT_AVERAGE_WEIGHT_IN_MG + ANT_AVERAGE_WEIGHT_IN_MG
		    * (Math.random() / 10 - 0.05), distanceToFood, i);

	    ftask = AntsApplication.antsService.enqueueRequest(request);
	    Futures.addCallback(ftask, new FutureCallback<Double>() {

		@Override
		public void onFailure(Throwable arg0) {
		    Log.w(LOG_TAG, "Unknown error");
		    return;
		}

		@Override
		public void onSuccess(Double arg0) {

		    updateCallback.update(totalAmountOfFood += arg0);
		}
	    });
	}

    }

    public void stopSendingAnts() {
	if (mExecutorService != null) {
	    mExecutorService.shutdownNow();
	    mExecutorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(3, 5, 15,
		    TimeUnit.SECONDS, mQueue));
	}
    }

    @Override
    public void onCreate() {
	super.onCreate();

	mQueue = new LinkedBlockingQueue<Runnable>();
	mExecutorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(3, 5, 15,
		TimeUnit.SECONDS, mQueue));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
	return mBinder;
    }

}