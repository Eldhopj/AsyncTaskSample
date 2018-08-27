package in.eldhopj.asynctasksample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

/**Commit 1 : AsyncTask
 *      AsyncTask is an android class which makes to do some work on the background thread and publish that work into MainThread
 *      When to use? : for short operations which takes only a few seconds long*/
public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);
    }

    public void startAsyncTask(View view) {
        new AsyncTaskClass().execute(10);
    }

    /** 1st params -> Type of parameter which we are passing eg: URL, Integer ..
     *  2nd params -> Progress units we need to publish
     *  3rd params -> Type of results we need to get Bitmap,String ..
     *
     *  NOTE : If we don't want to pass some params we can put Void and we cant pass primitive data types
     */
    private class AsyncTaskClass extends AsyncTask<Integer,Integer,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
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
            progressBar.setProgress(values[0]);
        }

        //The return value from the doInBackground come in here
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            progressBar.setProgress(0);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
