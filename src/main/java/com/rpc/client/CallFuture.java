package com.rpc.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class CallFuture<T> implements Callback<T> , Future<T> {

	private final CountDownLatch latch = new CountDownLatch(1);
	private T result = null;
	private Throwable error = null;
	
	@Override
	public void handleError(Throwable error) {
		this.error = error;
		latch.countDown();
	}

	@Override
	public void handleResult(T result) {
		this.result = result;
		latch.countDown();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		latch.await();
		if (error != null) {
	        throw new ExecutionException(error);
	      }
	      return result;
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
		if(latch.await(timeout, unit)){
			if (error != null) {
		        throw new ExecutionException(error);
		      }
		      return result;
		}else{
			throw new TimeoutException();
		}
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return latch.getCount() <=0;
	}

}
