package use.cpu.com.gjnm.optimizeprogress;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import use.cpu.com.gjnm.optimizeprogress.view.OptimizeArcProgressBar;

public class MainActivity extends AppCompatActivity {
    private Button but0;
    private Button but1;
    private Button but2;
    private OptimizeArcProgressBar bar0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar0 = (OptimizeArcProgressBar) findViewById(R.id.bar0);
        but0 = (Button) findViewById(R.id.butt0);
        but1 = (Button) findViewById(R.id.butt1);
        but2 = (Button) findViewById(R.id.butt2);

        but0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar0.setCurrentValues(70);
                bar0.setOutCurrentValues(40, OptimizeArcProgressBar.LEFT_PROGRESS);
                bar0.setOutCurrentValues(90, OptimizeArcProgressBar.RIGHT_PROGRESS);
            }
        });

        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar0.setCurrentValues(20);
                bar0.setOutCurrentValues(100, OptimizeArcProgressBar.LEFT_PROGRESS);
                bar0.setOutCurrentValues(40, OptimizeArcProgressBar.RIGHT_PROGRESS);
            }
        });

        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar0.setCurrentValues(0);
                bar0.setOutCurrentValues(0, OptimizeArcProgressBar.LEFT_PROGRESS);
                bar0.setOutCurrentValues(0, OptimizeArcProgressBar.RIGHT_PROGRESS);
            }
        });
    }
}
