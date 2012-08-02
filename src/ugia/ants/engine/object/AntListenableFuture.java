package ugia.ants.engine.object;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ListenableFuture;

public final class AntListenableFuture<V> extends FutureTask<V> implements ListenableFuture<V>,
	Comparable<AntListenableFuture<V>> {

    // The execution list to hold our listeners.
    private final ExecutionList executionList = new ExecutionList();

    private final Object object;

    /**
     * Creates a {@code ListenableFutureTask} that will upon running, execute
     * the given {@code Callable}.
     * 
     * @param callable
     *            the callable task
     * @since 10.0
     */
    public static <V> AntListenableFuture<V> create(Callable<V> callable) {
	return new AntListenableFuture<V>(callable);
    }

    /**
     * Creates a {@code ListenableFutureTask} that will upon running, execute
     * the given {@code Runnable}, and arrange that {@code get} will return the
     * given result on successful completion.
     * 
     * @param runnable
     *            the runnable task
     * @param result
     *            the result to return on successful completion. If you don't
     *            need a particular result, consider using constructions of the
     *            form:
     *            {@code ListenableFuture<?> f = ListenableFutureTask.create(runnable,
     *     null)}
     * @since 10.0
     */
    public static <V> AntListenableFuture<V> create(Runnable runnable, V result) {
	return new AntListenableFuture<V>(runnable, result);
    }

    private AntListenableFuture(Callable<V> callable) {
	super(callable);

	object = callable;
    }

    private AntListenableFuture(Runnable runnable, V result) {
	super(runnable, result);

	object = runnable;
    }

    @Override
    public void addListener(Runnable listener, Executor exec) {
	executionList.add(listener, exec);
    }

    /**
     * Internal implementation detail used to invoke the listeners.
     */
    @Override
    protected void done() {
	executionList.execute();
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(AntListenableFuture<V> o) {
	if (this == o)
	    return 0;
	if (o == null)
	    return -1; // high priority
	if (object != null && o.object != null) {
	    if (object.getClass().equals(o.object.getClass())) {
		if (object instanceof Comparable)
		    return ((Comparable<Object>) object).compareTo(o.object);
	    }
	}
	return 0;
    }
}