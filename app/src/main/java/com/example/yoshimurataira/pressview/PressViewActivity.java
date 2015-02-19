package com.example.yoshimurataira.pressview;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;

public class PressViewActivity extends Activity implements OnClickListener ,OnTouchListener,Camera.PictureCallback {

    //メンバ
    private Button btn1, btn2,btn3;

    private FrameLayout frameLayout;
    private ImageView imgTemp;
    private int image_number = 1;                              //アップロード処理のカウンター変数
    public final static int REQUEST_GALLEY = 0;              //ギャラリー起動時の引数
    public final static int REQUEST_CAMERA = 10001;            //カメラ時同時の引数

    //あほ
    private int targetLocalX;
    private int targetLocalY;
    private int screenX;
    private int screenY;
    private boolean imageUri;
    //int inum=20;
    private int imgId;
    //private int imgId[] = new int[inum];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_press_view);

        frameLayout = (FrameLayout)findViewById(R.id.framelayout);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.menu_press_view, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("requestCode",String.valueOf(requestCode));
        Log.v("RESULT_OK",String.valueOf(RESULT_OK));

        if (requestCode != REQUEST_GALLEY) {
            return;
        }

        //error
      if (resultCode != RESULT_OK) {
          return;
        }try {
            // dataからInputStreamを開く
            InputStream is = getContentResolver().openInputStream(data.getData());
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            InputData(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "ImageLoad Error.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //for (int i=0; i<inum; i++ ){
            if(v.getId()==imgId) {
                Log.i("onTouch",String.valueOf(imgId));
                imgTemp = (ImageView) findViewById(imgId);
                imageMotion(event, imgTemp);
            }
        return true;
    }

    private void imageMotion(MotionEvent event, ImageView imgObj) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE:

                int diffX = screenX - x;
                int diffY = screenY - y;

                targetLocalX -= diffX;
                targetLocalY -= diffY;

                imgObj.layout(targetLocalX,
                        targetLocalY,
                        targetLocalX + imgObj.getWidth(),
                        targetLocalY + imgObj.getHeight());

                screenX = x;
                screenY = y;

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //ギャラリーなどの実装
            case R.id.btn1:

                Intent intentG = new Intent();
                intentG.setType("image/*");
                intentG.setAction(Intent.ACTION_PICK);
                startActivityForResult(intentG, REQUEST_GALLEY);
                break;

            //カメラ実装
            case R.id.btn2:

                Intent intentC = new Intent();
                intentC.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intentC.addCategory(Intent.CATEGORY_DEFAULT);
                intentC.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intentC, REQUEST_CAMERA);
                break;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        // Bitmapデータの作成
       Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
       InputData(bitmap);
    }

    private void InputData(Bitmap bitmap) {
        if(bitmap!=null) {
            imgTemp = new ImageView(this);
            imgId = imgTemp.getId();
            imgTemp.setOnTouchListener(this);
            imgTemp.setImageBitmap(bitmap);
            frameLayout.addView(imgTemp);
            image_number++;

            Log.i("image_number",String.valueOf(image_number));
            Log.i("InputData",String.valueOf(imgId));
        }
    }
}
