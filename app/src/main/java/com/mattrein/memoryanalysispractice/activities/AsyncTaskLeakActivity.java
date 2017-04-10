package com.mattrein.memoryanalysispractice.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mattrein.memoryanalysispractice.R;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.Unbinder;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;

/**
 * This Activity starts an AsyncTask that is defined as an Inner Class which has an implicit ref
 * to this class as its enclosing class.  It demonstrates how unintentional references that occur
 * with Inner Classes can be prone to errors, especially if the two classes have different life
 * cycles.
 * <p>
 * Most of this code was borrowed from :
 * https://medium.com/freenet-engineering/memory-leaks-in-android-identify-treat-and-avoid-d0b1233acc8
 */
public class AsyncTaskLeakActivity extends AppCompatActivity {

    private static final long BACKGROUND_TASK_DURATION_MILLIS = 6000L;
    private Unbinder unbinder;

    private AsyncTask task;

    @BindView(R.id.textViewToUpdate)
    TextView textViewToUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_leak);

        unbinder = bind(this);

        task = new StaticBackgroundTask(textViewToUpdate).execute();

        //Uncomment the line below to invoke a Leaky, non-static inner class AsyncTask that will
        //cause errors and memory leaks.
        //new LeakyBackgroundTask().execute();
    }

    @Override
    protected void onDestroy() {

        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }

        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }


    /**
     * This static inner class is better b/c it doesn't have an implicit reference to the enclosing
     * Activity.  However, it still leaks until we introduce a WeakReference wrapper around the
     * provided TextView argument to its constructor.
     */
    private static class StaticBackgroundTask extends AsyncTask<Void, Void, String> {

        private final WeakReference<TextView> textViewWeakReference;

        public StaticBackgroundTask(TextView resultTextView) {
            this.textViewWeakReference = new WeakReference<>(resultTextView);
        }

        @Override
        protected void onCancelled() {
            // Cancel task. Code omitted.
        }

        @Override
        protected String doInBackground(Void... params) {
            // Do background work. Code omitted.
            try {
                Thread.sleep(BACKGROUND_TASK_DURATION_MILLIS);
            } catch (InterruptedException e) {
                Timber.e(e, "Interrupted exception in StaticBackgroundTask#doInBackground()");
            }
            return "New text value!";
        }

        @Override
        protected void onPostExecute(String result) {
            TextView resultTextView = textViewWeakReference.get();

            if (resultTextView != null) {
                resultTextView.setText(result);
            }
        }

    }

    private class LeakyBackgroundTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // Do background work. Code omitted.
            try {
                Thread.sleep(BACKGROUND_TASK_DURATION_MILLIS);
            } catch (InterruptedException e) {
                Timber.e(e, "Interrupted exception in LeakyBackgroundTask#doInBackground()");
            }
            return "updated text value";
        }

        @Override
        protected void onPostExecute(String result) {
            textViewToUpdate.setText(result);
        }
    }

}
