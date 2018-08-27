package in.eldhopj.asynctasksample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**Commit 1 : AsyncTask
 *      AsyncTask is an android class which makes to do some work on the background thread and publish that work into MainThread
 *      When to use? : for short operations which takes only a few seconds long
 *
 * Commit 2 : Fix for the AsyncTaskClass memory leak
 *          Problem : The AsyncTaskClass have an implicit reference to the outer class (MainActivity) eg: progressBar
 *                  when we press backButton or rotate the device usually the activity is distorted but asyncTask live until the task finishes
 *                  And it remains in the memory and causes memory leak
 *          Solution : WeakReference
 *                   Can garbage collected even the activity is distorted
 *                  */
public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);
    }

    public void startAsyncTask(View view) {
        new AsyncTaskClass(MainActivity.this).execute(10);
    }

    /** 1st params -> Type of parameter which we are passing eg: URL, Integer ..
     *  2nd params -> Progress units we need to publish
     *  3rd params -> Type of results we need to get Bitmap,String ..
     *
     *  NOTE : If we don't want to pass some params we can put Void and we cant pass primitive data types
     */
    private static class AsyncTaskClass extends AsyncTask<Integer,Integer,String>{ // make class static to prevent memory leak

        private WeakReference<MainActivity> activityWeakReference;
        //WeakReference can still garbage collected even the activity is distorted

        AsyncTaskClass(MainActivity activity){ // Constructor
            activityWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /* This will get a strong reference to the activity
            * The scope of this strong reference is inside this method only,
            * Since the weak reference is there in the MainClass(AsyncTaskClass) this still garbage collected*/
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()){ // MainActivity is destroyed or is in a process of destroying
                return;// if the main activity is destroyed this will return
            }
            activity.progressBar.setVisibility(View.VISIBLE);
        }

        //This is the only method which runs on the background thread except all runs on the mainThread
        @Override
        protected String doInBackground(Integer... integers) {

            for (int i=0;i<integers[0];i++)
            {
                publishProgress((i*100)/integers[0]); //publishProgress method returns progress into onProgressUpdate
                try {
                    Thread.sleep(1000); //Background thread freezes on ever 1sec , but it wont effect the mainThread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Finished";
        }

        //The value from the doInBackground -> publishProgress comes here
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()){ // MainActivity is destroyed or is in a process of destroying
                return;// if the main activity is destroyed this will return
            }
            activity.progressBar.setProgress(values[0]);
        }

        //The return value from the doInBackground come in here
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()){ // MainActivity is destroyed or is in a process of destroying
                return;// if the main activity is destroyed this will return
            }
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            activity.progressBar.setProgress(0);
            activity.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
