package pl.slapps;

import android.test.ActivityInstrumentationTestCase2;

import pl.slapps.dot.MainActivity;

/**
 * Created by piotr on 01.01.16.
 */
public class ActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{

    public MainActivity activity;
    public ActivityTest() {
        super(MainActivity.class);
    }

    public void setUp() throws Exception
    {
        super.setUp();;
        activity=getActivity();
    }
}
