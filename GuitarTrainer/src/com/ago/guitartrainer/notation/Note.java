package com.ago.guitartrainer.notation;

public enum Note {
    // @formatter:off
    D2di(Octave.II, Key.Ddi, 77.78), // not available on the guitar
    E2(Octave.II, Key.E, 82.407), 
    F2(Octave.II, Key.F, 87.31), 
    F2di(Octave.II, Key.Fdi, 92.5), 
    G2(Octave.II, Key.G, 98.0), 
    G2di(Octave.II, Key.Gdi, 103.83), 
    A2(Octave.II, Key.A, 110d), 
    A2di(Octave.II, Key.Adi,  116.54), 
    B2(Octave.II, Key.B, 123.47),

    
    C3(Octave.III, Key.C, 130.81), 
    C3di(Octave.III, Key.Cdi, 138.59), 
    D3(Octave.III, Key.D, 146.83), 
    D3di(Octave.III, Key.Ddi, 155.56), 
    E3(Octave.III, Key.E, 164.81), 
    F3(Octave.III, Key.F, 174.61), 
    F3di(Octave.III, Key.Fdi, 185d), 
    G3(Octave.III, Key.G, 196d), 
    G3di(Octave.III, Key.Gdi, 207.65), 
    A3(Octave.III, Key.A, 220d), 
    A3di(Octave.III, Key.Adi, 233.08), 
    B3(Octave.III, Key.B, 246.94),
    
    C4(Octave.IV, Key.C, 261.63), 
    C4di(Octave.IV, Key.Cdi, 277.18), 
    D4(Octave.IV, Key.D, 293.67), 
    D4di(Octave.IV, Key.Ddi, 311.13), 
    E4(Octave.IV, Key.E, 329.63), 
    F4(Octave.IV, Key.F, 349.23), 
    F4di(Octave.IV, Key.Fdi, 369.99), 
    G4(Octave.IV, Key.G, 392d), 
    G4di(Octave.IV, Key.Gdi, 415.3), 
    A4(Octave.IV, Key.A, 440d), 
    A4di(Octave.IV, Key.Adi, 466.16), 
    B4(Octave.IV, Key.B, 493.88), 
    
    C5(Octave.V, Key.C, 523.25), 
    C5di(Octave.V, Key.Cdi, 554.37), 
    D5(Octave.V, Key.D, 587.33), 
    D5di(Octave.V, Key.Ddi, 622.25), 
    E5(Octave.V, Key.E, 659.26),  
    F5(Octave.V, Key.F, 698.46); // over the 12th fret
    // @formatter:on

    private Octave octave;

    private Key key;

    private Double pitch;

    Note(Octave octave, Key key, Double pitch) {
        this.octave = octave;
        this.key = key;
        this.pitch = pitch;
    }

    public Octave getOctave() {
        return octave;
    }

    public Key getKey() {
        return key;
    }

    public Double getPitch() {
        return pitch;
    }

}