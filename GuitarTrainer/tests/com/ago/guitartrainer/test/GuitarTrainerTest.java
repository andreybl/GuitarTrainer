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
import com.ago.guitartrainer.notation.Key;
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

    public void test_Keys_ScaleMode2Key() {
        {
            Key key = Key.modeByParentAndDegree(Key.D, Degree.THREE);
            assertEquals(Key.Fdi, key);
        }
        {
            Key key = Key.modeByParentAndDegree(Key.A, Degree.FIVE);
            assertEquals(Key.E, key);
        }

        {
            Key key = Key.modeByParentAndDegree(Key.G, Degree.THREE);
            assertEquals(Key.B, key);
        }

        {
            Key key = Key.modeByParentAndDegree(Key.B, Degree.SEVEN);
            assertEquals(Key.Adi, key);
        }

        for (Key k : Key.values()) {
            for (Degree d : Degree.NATURAL_DEGREES) {
                Key key = Key.modeByParentAndDegree(k, d);
                System.out.print(key + "\t");
            }
            System.out.println();

        }
    }

    public void test_Keys_KeyDegree2ParentScale() {
        {
            Key key = Key.parentByModeAndDegree(Key.C, Degree.ONE);
            assertEquals(Key.C, key);
        }

        {
            Key key = Key.parentByModeAndDegree(Key.C, Degree.FIVE);
            assertEquals(Key.F, key);
        }

        {
            Key key = Key.parentByModeAndDegree(Key.C, Degree.SEVEN);
            assertEquals(Key.Cdi, key);
        }

        {
            Key key = Key.parentByModeAndDegree(Key.A, Degree.TWO);
            assertEquals(Key.G, key);
        }
    }

    public void test_GridShape_byFret() {
        ScaleGrid alphaScaleGrid = ScaleGrid.create(ScaleGrid.Type.ALPHA, 2);

        List<Note> notes = alphaScaleGrid.degree2Notes(Degree.SEVEN);
        assertEquals(2, notes.size());
        // assertEquals(3, notes.size());

        assertEquals(Note.C4di, notes.get(0));
        // assertEquals(Note.C4di, notes.get(1));
        assertEquals(Note.C3di, notes.get(1));

        /*
         * assuming, we play the note B3, which may be due playing positions [1/0], [2/4] or [3/9] . This is reflected
         * as content of the positions array.
         */
        List<Position> positions = new ArrayList<Position>();
        positions.add(new Position(1, 0)); // B3, first possible position
        positions.add(new Position(2, 4)); // B3, second possible position
        positions.add(new Position(3, 9)); // B3, third possible position

    }

    public void test_GuitarFrets() {
        {
            /*-
             * The E3 is presented three times on the fret. Each of the positions represent the note E3 uniquely on the areas:
             * > 0..6 (E3 on fret 2)
             * > 3..11 (E3 on fret 7)
             * > 8..12 (E3 on fret 12)
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
}
