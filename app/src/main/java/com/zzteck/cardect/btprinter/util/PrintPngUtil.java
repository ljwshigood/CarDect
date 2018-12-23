package com.zzteck.cardect.btprinter.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import com.bluetooth.byteUtil;
import com.zzteck.cardect.bean.SettingBean;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PrintPngUtil {
	byteUtil byteUtil = new byteUtil();

	private static final float rate = 1.3f;// 1.6f;
	private SettingBean settingBean = SettingBean.getInstance();

	// 打印纸宽
	// private static final int PRINT_WIDTH = 464;
	private static final int PRINT_WIDTH = 384;
	// 打印纸高
	// private static final int PRINT_HEIGHT = 832;
	// ditherTable X
	private static final int TABLE_X = 8;
	// ditherTable Y
	private static final int TABLE_Y = 8;
	// 源bitmap
	private Bitmap srcBitmap;
	private int srcImgPrintWidth = 0;
	private int srcImgPrintHeight = 0;
	private ByteBuffer srcBuffer;

	private Bitmap partBitmap;
	private Canvas canvas;
	private Rect dstRect;

	// private int offsetX = 0;

	private void readSrcImg() {
		canvas.drawColor(Color.WHITE);
		dstRect.top = 0;
		dstRect.bottom = srcImgPrintHeight;
		// dstRect.left = offsetX;
		// dstRect.right = srcImgPrintWidth;
		// dstRect.bottom = PRINT_HEIGHT;
		dstRect.left = 0;
		dstRect.right = PRINT_WIDTH;

		canvas.drawBitmap(srcBitmap, null, dstRect, null);
	}

	public PrintPngUtil(Bitmap srcBitmap) {
		this.srcBitmap = srcBitmap;
		srcImgPrintWidth = srcBitmap.getWidth();
		srcImgPrintHeight = srcBitmap.getHeight();
		// original size
		// if (srcImgPrintWidth <= settingBean.getImageMaxWidth()) {
		// srcImgPrintWidth = (int) (srcBitmap.getWidth() * rate);
		// srcImgPrintHeight = (int) (srcBitmap.getHeight() * rate);
		// } else {
		// srcImgPrintWidth = srcBitmap.getWidth();
		// srcImgPrintHeight = srcBitmap.getHeight();
		// }
		//
		// offsetX = (settingBean.getImagePrintWidth() - srcImgPrintWidth) / 2;
		//
		// MicroLog.debug("srcImgPrintWidth " + srcImgPrintWidth + "/"
		// + "srcImgPrintHeight" + srcImgPrintHeight);
		// MicroLog.debug("offsetX " + offsetX);

		float rate = PRINT_WIDTH / (float) srcImgPrintWidth;
		srcImgPrintHeight = (int) (srcImgPrintHeight * rate);

		float rate2 = (float) srcImgPrintHeight / 8;
		srcImgPrintHeight = 8 * (int) Math.ceil(rate2);

		// partBitmap = Bitmap.createBitmap(PRINT_WIDTH, PRINT_HEIGHT,
		// Config.ARGB_8888);
		partBitmap = Bitmap.createBitmap(PRINT_WIDTH, srcImgPrintHeight,
				Config.ARGB_8888);

		// partBitmap = Bitmap.createBitmap(settingBean.getImagePrintWidth(),
		// srcImgPrintHeight, Config.ARGB_8888);

		canvas = new Canvas(partBitmap);
		dstRect = new Rect();

		int byteSize = (partBitmap.getRowBytes() * partBitmap.getHeight());
		// System.out.println("partBitmap.getRowBytes() / "
		// +partBitmap.getRowBytes() );
		srcBuffer = ByteBuffer.allocate(byteSize);
	}

	private byte[] getGrayBytes(Bitmap partBitmap, ByteBuffer srcBuffer) {
		byte[] grayBytes = new byte[partBitmap.getWidth()
				* partBitmap.getHeight()];
		int srcWidth = partBitmap.getWidth();
		int srcHeight = partBitmap.getHeight();
		partBitmap.copyPixelsToBuffer(srcBuffer);
		byte[] srcBytes = srcBuffer.array();
		for (int y = 0; y < srcHeight; y++) {
			int oPos = srcWidth * y;
			int iPos = y * srcWidth * 4;
			for (int x = 0; x < srcWidth; x++) {
				int eachPoint = partBitmap.getPixel(x, y);
				byte r = (byte) Color.red(eachPoint);
				byte g = (byte) Color.green(eachPoint);
				byte b = (byte) Color.blue(eachPoint);
				grayBytes[oPos] = byteUtil.rgb2Gray(r, g, b);

				oPos++;
				iPos += 4;
			}
		}
		srcBuffer.clear();

		return grayBytes;
	}

	// 打印
	public byte[] printGrayMode() {
		// 设定打印位置
		readSrcImg();
		// 得到ditherTable
		byte[] ditherTBytes = byteUtil.getDither();
		// 灰度转换
		byte[] grayBytes = getGrayBytes(partBitmap, srcBuffer);
		// 得到打印流字节
		// return byteUtil.getPrinterBytes(ditherTBytes, grayBytes, PRINT_WIDTH,
		// PRINT_HEIGHT, TABLE_X, TABLE_Y);
		// return getPrinterBytes(ditherTBytes, grayBytes, PRINT_WIDTH,
		// PRINT_HEIGHT, TABLE_X, TABLE_Y);
		return getPrinterBytes(ditherTBytes, grayBytes, PRINT_WIDTH,
				srcImgPrintHeight, TABLE_X, TABLE_Y);
		// return byteUtil.getPrinterBytes(ditherTBytes, grayBytes,
		// settingBean.getImagePrintWidth(), srcImgPrintHeight, TABLE_X,
		// TABLE_Y);
	}

	// 得到打印流字节
	public byte[] getPrinterBytes(byte[] ditherTBytes, byte[] srcBits,
			int width, int height, int tableX, int tableY) {

		int cbSrc = width;
		int cbDst = width + 4;
		int cbDstHeight = height / 8;
		byte[] dstBits = new byte[cbDst * cbDstHeight + 8];
		int idxSrc = 0;
		int idxDst = 0;
		int val;
		int x;
		dstBits[0] = 0x1B;
		dstBits[1] = 0x4B;
		dstBits[2] = (byte) (width & 0xFF);
		dstBits[3] = (byte) ((width >> 8) & 0xFF);
		for (int y = 0; y < height; y++) {
			if ((y != 0) && (y % 8) == 0) {
				idxDst += cbDst;
				dstBits[idxDst + 0] = 0x1B;
				dstBits[idxDst + 1] = 0x4B;
				dstBits[idxDst + 2] = (byte) (width & 0xFF);
				dstBits[idxDst + 3] = (byte) ((width >> 8) & 0xFF);
			}
			for (x = 0; x < width; x++) {
				val = (short) srcBits[idxSrc + x] & 0xFF;
				if (val < ((short) ditherTBytes[((y) % tableY) * tableX
						+ (x % tableX)] & 0xFF)) {
					dstBits[idxDst + 4 + x] |= 0x01 << (7 - (y % 8));
				}
			}
			idxSrc += cbSrc;
		}
		dstBits[dstBits.length - 7] = 0x1B;
		dstBits[dstBits.length - 6] = 0x4A;
		dstBits[dstBits.length - 5] = 0x40;
		dstBits[dstBits.length - 4] = 0x0A;
		dstBits[dstBits.length - 3] = 0x0A;
		dstBits[dstBits.length - 2] = 0x0A;
		dstBits[dstBits.length - 1] = 0x0A;
		// MicroLog.debug(Arrays.toString(dstBits));

		File bytefile = new File("sdcard/testimg.prn");
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(
					bytefile));
			dos.write(dstBits);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		return dstBits;
	}
}
