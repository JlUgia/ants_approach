package ugia.ants;

import ugia.ants.application.AntsApplication;
import ugia.ants.engine.object.AntUpdateCallback;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class QueenAntActivity extends Activity {

    private EditText nAntsET;
    private EditText distanceET;
    private TextView totalFoodView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	nAntsET = (EditText) findViewById(R.id.nAntsEditText);
	distanceET = (EditText) findViewById(R.id.distanceEditText);
	totalFoodView = (TextView) findViewById(R.id.totalFoodView);

    }

    public void startAnts(View v) {

	if (AntsApplication.bound) {

	    int nAnts = !nAntsET.getText().toString().equals("") ? Integer.parseInt(nAntsET
		    .getText().toString()) : 0;
	    int distance = !distanceET.getText().toString().equals("") ? Integer
		    .parseInt(distanceET.getText().toString()) : 0;

	    AntsApplication.antsService.startSendingAnts(this, nAnts, distance,
		    new AntUpdateCallback<Double>() {

			@Override
			public void update(final Double result) {
			    runOnUiThread(new Runnable() {

				@Override
				public void run() {
				    totalFoodView.setText("Total food gathered (mg): "
					    + Math.round(result));
				}
			    });
			}
		    });
	}
    }
}