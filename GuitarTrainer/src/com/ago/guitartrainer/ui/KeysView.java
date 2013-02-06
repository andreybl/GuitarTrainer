package com.ago.guitartrainer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.notation.Key;

public class KeysView extends AbstractNotationView<Key> {

    public KeysView(Context context) {
        super(context);
    }

    public KeysView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeysView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void doInit(View mainLayout) {
        registerElement((Button) mainLayout.findViewById(R.id.key_c), Key.C);
        registerElement((Button) mainLayout.findViewById(R.id.key_c_di), Key.Cdi);
        registerElement((Button) mainLayout.findViewById(R.id.key_d), Key.D);
        registerElement((Button) mainLayout.findViewById(R.id.key_d_di), Key.Ddi);
        registerElement((Button) mainLayout.findViewById(R.id.key_e), Key.E);
        registerElement((Button) mainLayout.findViewById(R.id.key_f), Key.F);
        registerElement((Button) mainLayout.findViewById(R.id.key_g), Key.G);
        registerElement((Button) mainLayout.findViewById(R.id.key_g_di), Key.Gdi);
        registerElement((Button) mainLayout.findViewById(R.id.key_a), Key.A);
        registerElement((Button) mainLayout.findViewById(R.id.key_a_di), Key.Adi);
        registerElement((Button) mainLayout.findViewById(R.id.key_b), Key.B);

        isMainKeysOnly(true);

    }

    public void isOutput(boolean b) {
        if (b) {
            cbIsRandomInput.setVisibility(GONE);
        } else {
            cbIsRandomInput.setVisibility(VISIBLE);
        }
    }
    
    public void isMainKeysOnly(boolean b) {
        if (b) {
            resolveButton(Key.Cdi).setVisibility(INVISIBLE);
            resolveButton(Key.Ddi).setVisibility(INVISIBLE);
            resolveButton(Key.Gdi).setVisibility(INVISIBLE);
            resolveButton(Key.Adi).setVisibility(INVISIBLE);
        } else {
            resolveButton(Key.Cdi).setVisibility(VISIBLE);
            resolveButton(Key.Ddi).setVisibility(VISIBLE);
            resolveButton(Key.Gdi).setVisibility(VISIBLE);
            resolveButton(Key.Adi).setVisibility(VISIBLE);
        }
    }

    @Override
    protected int defaultLayoutResource() {
        return R.layout.keys_view;
    }

    @Override
    protected Key defaultElement() {
        return Key.C;
    }

}
