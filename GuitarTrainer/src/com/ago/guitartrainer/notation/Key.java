package com.ago.guitartrainer.notation;

public enum Key {
    C, Cdi, D, Ddi, E, F, Fdi, G, Gdi, A, Adi, B;

    /**
     * The keys corresponds to the main degrees of the C-major scale: C, D, E etc. The main reason to exclude keys with
     * sharps/flats: the appropriate images are not currently not available in the {@link NotesView}. But on the other
     * side it could be enough just to no the position of the main keys.
     * */
    public static final Key[] mainKeys = new Key[] { Key.C, Key.D, Key.E, Key.F, Key.G, Key.A, Key.B };
}
