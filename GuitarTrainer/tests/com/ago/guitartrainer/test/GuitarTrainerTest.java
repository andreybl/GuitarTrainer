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

import com.ago.guitartrainer.MasterActivity;
import com.ago.guitartrainer.instruments.guitar.GuitarFingeringHelper;
import com.ago.guitartrainer.instruments.guitar.GuitarUtils;
import com.ago.guitartrainer.instruments.guitar.Position;
import com.ago.guitartrainer.notation.Degree;
import com.ago.guitartrainer.notation.Note;
import com.ago.guitartrainer.notation.NoteStave;
import com.ago.guitartrainer.scalegrids.ScaleGrid;
import com.ago.guitartrainer.utils.ArrayUtils;
import com.jayway.android.robotium.solo.Solo;

public class GuitarTrainerTest extends ActivityInstrumentationTestCase2<MasterActivity> {

    private Solo solo;

    public GuitarTrainerTest() {
        super(MasterActivity.class);

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

    // public void test_GridShape1() {
    // GridShape aGs = GridShape.create(AlphaGridShape.class, 3);
    // assertNotNull(aGs);
    // }

    public void test_GridShape_byFret() {
        ScaleGrid alphaScaleGrid = ScaleGrid.create(ScaleGrid.Type.ALPHA, 2);

        List<Note> notes = alphaScaleGrid.degree2Notes(Degree.SEVEN);
        assertEquals(3, notes.size());

        assertEquals(Note.C4di, notes.get(0));
        assertEquals(Note.C4di, notes.get(1));
        assertEquals(Note.C3di, notes.get(2));

        /*
         * assuming, we play the note B3, which may be due playing positions [1/0], [2/4] or [3/9] . This is reflected
         * as content of the positions array.
         */
        List<Position> positions = new ArrayList<Position>();
        positions.add(new Position(1, 0)); // B3, first possible position
        positions.add(new Position(2, 4)); // B3, second possible position
        positions.add(new Position(3, 9)); // B3, third possible position

        // expect [2/5], because only it is in the shape
        // List<Position> positionsInAlpha = alphaScaleGrid.applyShape(positions);
        // assertEquals(1, positionsInAlpha.size());
        // assertEquals(2, positionsInAlpha.get(0).getStringIndex());
        // assertEquals(4, positionsInAlpha.get(0).getFret());
    }

    public void test_GuitarFrets() {
        {
            /*-
             * The E3 is presented three times on the fret. Each of the positions represent the note E3 uniquely on the areas:
             *      > 0..6   (E3 on fret 2)
             *      > 3..11  (E3 on fret 7)
             *      > 8..12  (E3 on fret 12)
             */

            List<Position> e3Positions = GuitarFingeringHelper.getInstance().resolvePositions(Note.E3);
            Position e3First = e3Positions.get(0);
            Position e3Second = e3Positions.get(1);
            Position e3Thirds = e3Positions.get(2);
            int[] e3FirstArea = GuitarUtils.calculateUniqueAreaForPosition(e3Positions, e3First);
            int[] e3SecondArea = GuitarUtils.calculateUniqueAreaForPosition(e3Positions, e3Second);
            int[] e3ThirdArea = GuitarUtils.calculateUniqueAreaForPosition(e3Positions, e3Thirds);

            assertTrue("Wrong unique area calculation for first E3 position",
                    ArrayUtils.isEqual(new int[] { 0, 6 }, e3FirstArea));
            assertTrue("Wrong unique area calculation for second E3 position",
                    ArrayUtils.isEqual(new int[] { 3, 11 }, e3SecondArea));
            assertTrue("Wrong unique area calculation for third E3 position",
                    ArrayUtils.isEqual(new int[] { 8, 12 }, e3ThirdArea));
        }

        {
            List<Position> positions = GuitarFingeringHelper.getInstance().resolvePositions(Note.A3);
            Position firstPos = positions.get(0);
            Position secondPos = positions.get(1);
            Position thirdPos = positions.get(2);
            int[] firstArea = GuitarUtils.calculateUniqueAreaForPosition(positions, firstPos);
            int[] secondArea = GuitarUtils.calculateUniqueAreaForPosition(positions, secondPos);
            int[] thirdArea = GuitarUtils.calculateUniqueAreaForPosition(positions, thirdPos);
            assertTrue(ArrayUtils.isEqual(new int[] { 0, 6 }, firstArea));
            assertTrue(ArrayUtils.isEqual(new int[] { 3, 11 }, secondArea));
            assertTrue(ArrayUtils.isEqual(new int[] { 8, 12 }, thirdArea));

            System.out.println("x");
        }
    }

    public void test_NoteStave() {
        NoteStave noteStave = NoteStave.getInstance();
        Note note = Note.A3;
        Note next = noteStave.next(note);
        assertEquals(Note.A3di, next);

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
