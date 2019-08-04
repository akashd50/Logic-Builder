package com.akashd50.lb.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.akashd50.lb.R;
import com.akashd50.lb.logic.TextureContainer;
import com.akashd50.lb.objects.Texture;

public class Utilities {
    public static float SCR_ACT_HEIGHT = -1;
    public static float SCR_ACT_WIDTH=-1;

    public static String gateFolderT = "gateFolder";
    public static String wireFolderT = "wireFolderT";
    public static String boardFolderT = "boardFolderT";

    public static String orGateT = "orGateT";
    public static String andGateT = "andGateT";
    public static String notGateT = "notGateT";
    public static String xorGateT = "xorGateT";

    public static String wirelrT = "wirelrT";
    public static String wirerlT = "wirerlT";
    public static String wiretbT = "wiretbT";
    public static String wirebtT = "wirebtT";

    public static String wireblT = "wireblT";
    public static String wirebrT = "wirebrT";
    public static String wiretlT = "wiretlT";
    public static String wiretrT = "wiretrT";
    public static String wirelbT = "wirelbT";
    public static String wireltT = "wireltT";
    public static String wirerbT = "wirerbT";
    public static String wirertT = "wirertT";

    public static String wiretrbT = "wiretrbT";
    public static String wiretlbT = "wiretlbT";
    public static String wirerblT = "wirerblT";
    public static String wirertlT = "wirertlT";
    public static String wirebltT = "wirebltT";
    public static String wirebrtT = "wirebrtT";
    public static String wirelbrT = "wirelbrT";
    public static String wireltrT = "wireltrT";

    public static String wiretrblT = "wiretrblT";
    public static String wirerbltT = "wirerbltT";
    public static String wirebltrT = "wirebltrT";
    public static String wireltrbT = "wireltrbT";

    public static final String wire_ps_tblr = "wire_ps_tblr";
    public static final String wire_ps_tbrl = "wire_ps_tbrl";
    public static final String wire_ps_btlr = "wire_ps_btlr";
    public static final String wire_ps_btrl = "wire_ps_bt_rl";

    public static String showoptionsT = "showOptions";

    public static String displayEmptyT = "displayEmptyT";
    public static String displayOneT = "displayOneT";
    public static String displayZeroT = "displayZeroT";

    public static float getScreenHeightPixels(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static float getScreenWidthPixels(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static float getStatusBarHeightPixels(Context context) {
        float result = 0;
        float resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize((int)resourceId);
        }
        return result;
    }
    public static float getScreenTop(Context context){
        return (getScreenHeightPixels() - getStatusBarHeightPixels(context))/getScreenWidthPixels();
    }

    public static float getScreenBottom(Context context){
        return -(getScreenHeightPixels() - getStatusBarHeightPixels(context))/getScreenWidthPixels();
    }

    public static void setScreenVars(float h, float w){
        SCR_ACT_HEIGHT = h;
        SCR_ACT_WIDTH = w;
    }

    public static void loadTextures(TextureContainer textureContainer, Context context){
        Texture t = new Texture(Utilities.gateFolderT, context, R.drawable.gate_folder);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wireFolderT, context, R.drawable.wire_folder);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.boardFolderT, context, R.drawable.four_bit_buffer);
        textureContainer.addTexture(t);

        t = new Texture(Utilities.andGateT, context, R.drawable.and_gate);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.orGateT, context, R.drawable.or_gate);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.notGateT, context, R.drawable.not_gate);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.xorGateT, context, R.drawable.xor_gate_ii);
        textureContainer.addTexture(t);

        t = new Texture(Utilities.wirelrT, context, R.drawable.wire_lr);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirerlT, context, R.drawable.wire_rl);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wiretbT, context, R.drawable.wire_tb);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirebtT, context, R.drawable.wire_bt);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wireblT, context, R.drawable.wire_turn_bl);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirebrT, context, R.drawable.wire_turn_br);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wiretlT, context, R.drawable.wire_turn_tl);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wiretrT, context, R.drawable.wire_turn_tr);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirertT, context, R.drawable.wire_turn_rt);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirerbT, context, R.drawable.wire_turn_rb);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wireltT, context, R.drawable.wire_turn_lt);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirelbT, context, R.drawable.wire_turn_lb);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wireltrbT, context, R.drawable.wire_turn_trb);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wiretlbT, context, R.drawable.wire_turn_tlb);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirerblT, context, R.drawable.wire_turn_rbl);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirertlT, context, R.drawable.wire_turn_rtl);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirebltT, context, R.drawable.wire_turn_blt);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirebrtT, context, R.drawable.wire_turn_brt);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirelbrT, context, R.drawable.wire_turn_lbr);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wireltrT, context, R.drawable.wire_turn_ltr);
        textureContainer.addTexture(t);

        t = new Texture(Utilities.wiretrblT, context, R.drawable.wire_turn_trbl);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirerbltT, context, R.drawable.wire_turn_rblt);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wirebltrT, context, R.drawable.wire_turn_bltr);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wireltrbT, context, R.drawable.wire_turn_ltrb);
        textureContainer.addTexture(t);


        t = new Texture(Utilities.wire_ps_tblr, context, R.drawable.wire_turn_pt_tb_lr);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wire_ps_tbrl, context, R.drawable.wire_turn_pt_tb_rl);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wire_ps_btlr, context, R.drawable.wire_turn_pt_bt_lr);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.wire_ps_btrl, context, R.drawable.wire_turn_pt_bt_rl);
        textureContainer.addTexture(t);

        t = new Texture(Utilities.showoptionsT, context, R.drawable.show_items_menu);
        textureContainer.addTexture(t);

        t = new Texture(Utilities.displayEmptyT, context, R.drawable.display_empty_ii);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.displayOneT, context, R.drawable.display_one_ii);
        textureContainer.addTexture(t);
        t = new Texture(Utilities.displayZeroT, context, R.drawable.display_zero_ii);
        textureContainer.addTexture(t);
    }

    public static Texture generateTexture(String text, String tag){
        Bitmap.Config cf = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(200,200,cf);
        Canvas canvas = new Canvas(bitmap);

        Paint p = new Paint();
        p.setColor(Color.argb(255,255,255,255));
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(60);
        canvas.drawText(text,100,100,p);

        Texture texture = new Texture(tag);
        texture.loadTexture(bitmap);

        return texture;
    }
}
