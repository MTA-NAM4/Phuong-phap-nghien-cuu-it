package com.binh.maytinhsieuthi;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.binh.maytinhsieuthi.database.DatabaseHelper;
import com.binh.maytinhsieuthi.model.Product;
import com.google.zxing.Result;
import com.klinker.android.sliding.MultiShrinkScroller;
import com.klinker.android.sliding.SlidingActivity;
import com.r0adkll.slidr.Slidr;
import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        file.writeToFile("", getApplicationContext(), "ma");
        file.writeToFile("", getApplicationContext(), "ten");
        file.writeToFile("", getApplicationContext(), "gia");
        file.writeToFile("", getApplicationContext(), "tt");
        file.writeToFile("", getApplicationContext(), "t");
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        final EditText ed = (EditText) findViewById(R.id.editText);
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = ed.getText().toString();
                if(t!=""){
                    file.writeToFile("0@", getApplicationContext(), "ma");
                    file.writeToFile("0@", getApplicationContext(), "ten");
                    file.writeToFile("0@", getApplicationContext(), "gia");
                    file.writeToFile("0@", getApplicationContext(), "tt");
                    file.writeToFile(t, getApplicationContext(), "t");
                    Intent intent = new Intent(getApplicationContext(), DifferentMenuActivity.class);
                    startActivity(intent);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.out);
                    findViewById(R.id.go).startAnimation(animation);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ed.setVisibility(View.GONE);
                            findViewById(R.id.go).setVisibility(View.GONE);
                            findViewById(R.id.logo).setVisibility(View.GONE);
                            findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
                        }
                    }, 2000);
                }
                else{
                    Toast.makeText(getApplicationContext(),"nhập ngân sách",Toast.LENGTH_LONG).show();
                }
            }
        });

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        DatabaseHelper mDBHelper = new DatabaseHelper(this);
        File database = getApplicationContext().getDatabasePath(DatabaseHelper.DBNAME);
        if (false == database.exists()) {
            mDBHelper.getReadableDatabase();
            if (copyDatabase(this)) {
            } else {
                return;
            }
        }
        List<Product> mProductList = mDBHelper.getListProduct();
        for (int i = 0; i < mProductList.size(); i++) {
            if (mProductList.get(i).getMa().equals(rawResult.getText())) {

                String chuoi = file.readFromFile(getApplicationContext(), "ma");
                chuoi = chuoi + mProductList.get(i).getMa() + "@";
                file.writeToFile(chuoi, getApplicationContext(), "ma");

                String chuoi1 = file.readFromFile(getApplicationContext(), "ten");
                chuoi1 = chuoi1 + mProductList.get(i).getName() + "@";
                file.writeToFile(chuoi1, getApplicationContext(), "ten");

                String chuoi2 = file.readFromFile(getApplicationContext(), "gia");
                chuoi2 = chuoi2 + mProductList.get(i).getGia() + "@";
                file.writeToFile(chuoi2, getApplicationContext(), "gia");

                String chuoi3 = file.readFromFile(getApplicationContext(), "tt");
                chuoi3 = chuoi3 + mProductList.get(i).getThongtin() + "@";
                file.writeToFile(chuoi3, getApplicationContext(), "tt");
                Intent intent = new Intent(getApplicationContext(), DifferentMenuActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "đây không phải là sẩn phẩm của cửa hàng", Toast.LENGTH_LONG).show();
            }
        }
        mScannerView.resumeCameraPreview(MainActivity.this);
    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 40;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }

    private boolean copyDatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(DatabaseHelper.DBNAME);
            String outFileName = DatabaseHelper.DBLOCATION + DatabaseHelper.DBNAME;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            Log.w("MainActivity", "DB copied");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), DifferentMenuActivity.class);
        startActivity(intent);
    }
}

