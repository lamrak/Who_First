package net.validcat.whofirst.util;

import net.validcat.whofirst.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

public class BitmapUtils {
    
    int[] chips = {
			R.drawable.ic_chips_1,
			R.drawable.ic_chips_10,
			R.drawable.ic_chips_12,
			R.drawable.ic_chips_17,
			R.drawable.ic_chips_2,
			R.drawable.ic_chips_3,
			R.drawable.ic_chips_4,
			R.drawable.ic_chips_5,
			R.drawable.ic_chips_6,
			R.drawable.ic_chips_9
    };
    
    static SparseArray<Bitmap> bitmapMap = new SparseArray<Bitmap>();

    public Bitmap[] loadChipsBitmap(Resources resources, int size) {
    	Bitmap[] chipsBitmap = new Bitmap[chips.length];
        for (int i = 0; i < chips.length; ++i) {
            chipsBitmap[i] = getThumbnail(getBitmap(resources, chips[i]), size);
        }
        return chipsBitmap;
    }

    /**
     * Utility method to get bitmap from cache or, if not there, load it
     * from its resource.
     */
    static Bitmap getBitmap(Resources resources, int resourceId) {
        Bitmap bitmap = bitmapMap.get(resourceId);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(resources, resourceId);
            bitmapMap.put(resourceId, bitmap);
        }        
        return bitmap;
    }
    
    /**
     * Create and return a thumbnail image given the original source bitmap and a max
     * dimension (width or height).
     */
    private Bitmap getThumbnail(Bitmap original, int maxDimension) {
        int width = original.getWidth();
        int height = original.getHeight();
        int scaledWidth, scaledHeight;
        if (width >= height) {
            float scaleFactor = (float) maxDimension / width;
            scaledWidth = maxDimension;
            scaledHeight = (int) (scaleFactor * height);
        } else {
            float scaleFactor = (float) maxDimension / height;
            scaledWidth = (int) (scaleFactor * width);
            scaledHeight = maxDimension;
        }
        Bitmap thumbnail = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);
        
        return thumbnail;
    }
    

}
