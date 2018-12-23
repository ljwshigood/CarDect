package com.zzteck.cardect.btprinter.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;


public class ImageUtils {
	
	
	public Bitmap rotateByBitmap(Bitmap src, int degree) {
		Matrix matrix = new Matrix();
		matrix.setRotate((float) degree);
		Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
				src.getHeight(), matrix, true);
		/*try {
			FileOutputStream f;
			
			File dstFile = File.createTempFile(ConstCode.TMP_FILE_PREFIX,
					ConstCode.TMP_FILE_PREFIX);
			f = new FileOutputStream(dstFile);
			DataOutputStream out = new DataOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.close();
			src.recycle();
			bitmap.recycle();

			//File srcFile = new File(srcnm);
//			if (srcFile.delete()) {
//				dstFile.renameTo(srcFile);
//				return true;
//			} else {
//				dstFile.delete();
//				return false;
//			}
		} catch (Exception e) {
			return false;
		}*/
		return bitmap;
	}
}
