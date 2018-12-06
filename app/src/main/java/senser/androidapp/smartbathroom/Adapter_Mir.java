package senser.androidapp.smartbathroom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static senser.androidapp.smartbathroom.R.id.guideImage;

public class Adapter_Mir extends PagerAdapter{

    private int[] images = {R.drawable.smartmiruse_1, R.drawable.smartmiruse_2, R.drawable.smartmiruse_3, R.drawable.smartmiruse_4, R.drawable.smartmiruse_5, R.drawable.smartmiruse_6};

    private LayoutInflater inflater;
    private Context context;


    public Adapter_Mir(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.slider_mir, container, false);

        ImageView mirImage = (ImageView) v.findViewById(R.id.mirImage);
        mirImage.setImageResource(images[position]);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
