package first.project.nikzhebindev.organizer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("first.project.nikzhebindev.organizer", appContext.getPackageName());
    }


            /*case R.id.btnReadDatabase:

                // TESTING
                editTask.setText("");
                String[] str = DataBaseLists.readDBLists(databaseLists);

                String bufferStr = "";

                for(int i = 0; i < str.length; i++)
                {
                    bufferStr += str[i];
                    bufferStr += "\n";
                }
                editTask.setText(bufferStr);
                break;
            case R.id.btnReadDBTasks:

                // TESTING
                editTask.setText("");
                String[] str1 = DataBaseTasks.readDataBase(databaseTasks);

                bufferStr = "";

                for(int i = 0; i < str1.length; i++)
                {
                    bufferStr += str1[i];
                    bufferStr += "\n\n";
                }
                editTask.setText(bufferStr);
                break;

            case R.id.btnReadDBFTasks:

                // TESTING
                editTask.setText("");

                DataBaseFinishedTasks dbFT = new DataBaseFinishedTasks(this);
                SQLiteDatabase dbFinishedTasks = dbFT.getWritableDatabase();

                String[] str2 = DataBaseFinishedTasks.readDataBase(dbFinishedTasks);

                bufferStr = "";

                for(int i = 0; i < str2.length; i++)
                {
                    bufferStr += str2[i];
                    bufferStr += "\n\n";
                }
                editTask.setText(bufferStr);
                break;


            case R.id.btnClearDatabase:
                DataBaseLists.clearDataBase(databaseLists);
                editTask.setText("DataBase is DELETED!");
                break;
            case R.id.btnClearDBTasks:
                DataBaseTasks.clearDataBase(databaseTasks);
                editTask.setText("DataBaseTasks is DELETED!");
                break;
            case R.id.btnClearDBFTasks:
                DataBaseFinishedTasks dbFT2 = new DataBaseFinishedTasks(this);
                SQLiteDatabase dbFinishedTasks2 = dbFT2.getWritableDatabase();
                DataBaseFinishedTasks.clearDataBase(dbFinishedTasks2);
                editTask.setText("DataBaseFinished is DELETED!");
                break;

            case R.id.btnCheckTime:
                editTask.setText(dateAndTime.getTime().toString());
                break;*/

}
