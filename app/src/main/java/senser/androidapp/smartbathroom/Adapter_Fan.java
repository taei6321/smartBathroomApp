package senser.androidapp.smartbathroom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static senser.androidapp.smartbathroom.R.id.guideImage;

public class Adapter_Fan extends PagerAdapter{

    private int[] images = {R.drawable.fanswitch, R.drawable.fanuseon, R.drawable.fanautouse, R.drawable.fanautoon, R.drawable.fanonoffhum};

    private LayoutInflater inflater;
    private Context context;


    public Adapter_Fan(Context context){
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

        View v = inflater.inflate(R.layout.slider_fan, container, false);

        ImageView fanImage = (ImageView) v.findViewById(R.id.fanImage);
        fanImage.setImageResource(images[position]);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
