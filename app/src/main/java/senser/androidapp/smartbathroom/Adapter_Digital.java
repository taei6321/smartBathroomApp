package senser.androidapp.smartbathroom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Adapter_Digital extends PagerAdapter{

    private int[] images = {R.drawable.digitalshoweruse, R.drawable.digitalshoweruse2};

    private LayoutInflater inflater;
    private Context context;

    public Adapter_Digital(Context context){
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

        View v = inflater.inflate(R.layout.slider_digital, container, false);

        ImageView digitalImage = (ImageView) v.findViewById(R.id.digitalImage);
        digitalImage.setImageResource(images[position]);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
