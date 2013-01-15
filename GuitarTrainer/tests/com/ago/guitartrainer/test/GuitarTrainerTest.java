/*
 * This is an example test project created in Eclipse to test NotePad which is a sample 
 * project located in AndroidSDK/samples/android-11/NotePad
 * 
 * 
 * You can run these test cases either on the emulator or on device. Right click
 * the test project and select Run As --> Run As Android JUnit Test
 * 
 * @author Renas Reda, renas.reda@jayway.com
 * 
 */

package com.ago.guitartrainer.test;

import java.util.ArrayList;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;

import com.ago.guitartrainer.GuitarTrainerActivity;
import com.ago.guitartrainer.gridshapes.AlphaGridShape;
import com.ago.guitartrainer.gridshapes.GridShape;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.Position;
import com.jayway.android.robotium.solo.Solo;

public class GuitarTrainerTest extends ActivityInstrumentationTestCase2<GuitarTrainerActivity> {

    private Solo solo;

    public GuitarTrainerTest() {
        super(GuitarTrainerActivity.class);

    }

    @Override
    public void setUp() throws Exception {
        // setUp() is run before a test case is started.
        // This is where the solo object is created.
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        // tearDown() is run after a test case has finished.
        // finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

//    public void test_GridShape1() {
//        GridShape aGs = GridShape.create(AlphaGridShape.class, 3);
//        assertNotNull(aGs);
//    }
    
    public void test_GridShape_byFret() {
//        GridShape gs = GridShape.create(AlphaGridShape.class, Note.D4);
        GridShape gs = GridShape.create(AlphaGridShape.class, 2);

        List<Note> notes = gs.degree2Notes(Degree.SEVEN);
        assertEquals(2, notes.size());

        assertEquals(Note.C4di, notes.get(0));
        assertEquals(Note.C3di, notes.get(1));

        /*
         * assuming, we play the note B3, which may be due playing positions [1/0], [2/4] or [3/9] . This is reflected
         * as content of the positions array.
         */
        List<Position> positions = new ArrayList<Position>();
        positions.add(new Position(1, 0)); // B3, first possible position
        positions.add(new Position(2, 4)); // B3, second possible position
        positions.add(new Position(3, 9)); // B3, third possible position

        // expect [2/5], because only it is in the shape
        List<Position> inShape = gs.applyShape(positions);
        assertEquals(1, inShape.size());
        assertEquals(2, inShape.get(0).string);
        assertEquals(4, inShape.get(0).fret);
    }

    // public void testAddNote() throws Exception {
    // solo.clickOnMenuItem("Add note");
    // //Assert that NoteEditor activity is opened
    // solo.assertCurrentActivity("Expected NoteEditor activity", "NoteEditor");
    // //In text field 0, add Note 1
    // solo.enterText(0, "Note 1");
    // solo.goBack();
    // //Clicks on menu item
    // solo.clickOnMenuItem("Add note");
    // //In text field 0, add Note 2
    // solo.enterText(0, "Note 2");
    // //Go back to first activity named "NotesList"
    // solo.goBackToActivity("NotesList");
    // //Takes a screenshot and saves it in "/sdcard/Robotium-Screenshots/".
    // solo.takeScreenshot();
    // boolean expected = true;
    // boolean actual = solo.searchText("Note 1") && solo.searchText("Note 2");
    // //Assert that Note 1 & Note 2 are found
    // assertEquals("Note 1 and/or Note 2 are not found", expected, actual);
    //
    // }
    //
    // public void testEditNote() throws Exception {
    // // Click on the second list line
    // solo.clickInList(2);
    // // Change orientation of activity
    // solo.setActivityOrientation(Solo.LANDSCAPE);
    // // Change title
    // solo.clickOnMenuItem("Edit title");
    // //In first text field (0), add test
    // solo.enterText(0, " test");
    // solo.goBack();
    // boolean expected = true;
    // // (Regexp) case insensitive
    // boolean actual = solo.waitForText("(?i).*?note 1 test");
    // //Assert that Note 1 test is found
    // assertEquals("Note 1 test is not found", expected, actual);
    //
    // }
    //
    // public void testRemoveNote() throws Exception {
    // //(Regexp) case insensitive/text that contains "test"
    // solo.clickOnText("(?i).*?test.*");
    // //Delete Note 1 test
    // solo.clickOnMenuItem("Delete");
    // //Note 1 test & Note 2 should not be found
    // boolean expected = false;
    // boolean actual = solo.searchText("Note 1 test");
    // //Assert that Note 1 test is not found
    // assertEquals("Note 1 Test is found", expected, actual);
    // solo.clickLongOnText("Note 2");
    // //Clicks on Delete in the context menu
    // solo.clickOnText("Delete");
    // actual = solo.searchText("Note 2");
    // //Assert that Note 2 is not found
    // assertEquals("Note 2 is found", expected, actual);
    // }
}
