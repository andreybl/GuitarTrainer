package com.ago.guitartrainer;

import java.util.List;

import com.ago.guitartrainer.Notes.Note;
import com.ago.guitartrainer.gridshapes.AlphaGridShape;
import com.ago.guitartrainer.gridshapes.GridShape;

public class Main {

    public static void main(String[] args) {
        {
            // TODO: JUnit for calculations on degrees
            Degree x = Degree.SEVEN.addFrets(1);
            System.out.println(x.toString()); // ONE
        }

        {
            Degree x = Degree.TWO.addFrets(3);
            System.out.println(x.toString()); // FOUR
        }

        {
            Degree x = Degree.SEVEN.addFrets(0);
            System.out.println(x.toString()); // SEVEN
        }
        System.out.println("---------");
        {
            GridShape al = new AlphaGridShape(Note.D4);
            List<Note> notes = al.calculateNotes(Degree.SEVEN);
            System.out.println(notes); // expected: C4di, C3di
        }
        
        {
            GridShape al = new AlphaGridShape(Note.C4);
            List<Note> notes = al.calculateNotes(Degree.SEVEN);
            System.out.println(notes); // expected: B3, B2
        }
        
    }
}
