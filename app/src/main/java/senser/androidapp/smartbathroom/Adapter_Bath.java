package senser.androidapp.smartbathroom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static senser.androidapp.smartbathroom.R.id.guideImage;

public class Adapter_Bath extends PagerAdapter{

    private int[] images = {R.drawable.tempcontrol, R.drawable.waterheightcontrol, R.drawable.waterresult, R.drawable.watercontrol, R.drawable.waterstart, R.drawable.waterstop};

    private LayoutInflater inflater;
    private Context context;

    public Adapter_Bath(Context context){
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

        View v = inflater.inflate(R.layout.slider_bath, container, false);

        ImageView bathImage = (ImageView) v.findViewById(R.id.bathImage);
        bathImage.setImageResource(images[position]);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
