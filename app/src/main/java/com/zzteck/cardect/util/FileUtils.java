package com.zzteck.cardect.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.zzteck.cardect.bean.FileBean;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class FileUtils {

	private static boolean isMount(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

    public static void writeFileToSDCard(String fileName,String resultString) throws IOException {
    	
    	if(isMount()){
    		
    		File parent_path = Environment.getExternalStorageDirectory();  
            File dir = new File(parent_path.getAbsoluteFile(), "zzteck");
            if(!dir.exists()){
            	dir.mkdir();	
            }
      
            File file = new File(dir.getAbsoluteFile(), fileName);
            
            //Log.d("文件路径", file.getAbsolutePath());  
            file.createNewFile();  
      
            FileOutputStream fos = new FileOutputStream(file);  
      
            //String data = "hello,world! Zhang Phil @ CSDN";  
            byte[] buffer = resultString.getBytes();  
      
            fos.write(buffer, 0, buffer.length);  
            fos.flush();  
            fos.close();  
            Log.d("文件写入", "成功");
            	
    	}
    }  
    
    public static void writeFileToSDCard2(String fileName,String resultString) throws IOException {
    	
    	if(isMount()){
    		
    		File parent_path = Environment.getExternalStorageDirectory();  
            File dir = new File(parent_path.getAbsoluteFile(), "zzteckjson");
            if(!dir.exists()){
            	dir.mkdir();	
            }
      
            File file = new File(dir.getAbsoluteFile(), fileName);
            
            //Log.d("文件路径", file.getAbsolutePath());  
            file.createNewFile();  
      
            FileOutputStream fos = new FileOutputStream(file);  
      
            //String data = "hello,world! Zhang Phil @ CSDN";  
            byte[] buffer = resultString.getBytes();  
      
            fos.write(buffer, 0, buffer.length);  
            fos.flush();  
            fos.close();  
            Log.d("文件写入", "成功");
            	
    	}
    }  
    
    
	public static void getFiles(ArrayList<FileBean> fileList, String path) {

		File rootFile = new File(path) ;

		if(!rootFile.exists()){
			return ;
		}

		File[] allFiles = new File(path).listFiles();

		for (int i = 0; i < allFiles.length; i++) {
			File file = allFiles[i];
			if (file.isFile()) {
				FileBean bean = new FileBean() ;
				bean.setFilePath(file.getAbsolutePath()) ;
				bean.setFileName(file.getName()) ;
				fileList.add(bean);
			}
		}
	}

	public static ArrayList<String> ReadTxtFile(String strFilePath) {
		String path = strFilePath;
		ArrayList<String> newList = new ArrayList<String>();
		// 打开文件
		File file = new File(path);
		// 如果path是传递过来的参数，可以做一个非目录的判断
		if (file.isDirectory()) {
			Log.d("TestFile", "The File doesn't not exist.");
		} else {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					// 分行读取
					while ((line = buffreader.readLine()) != null) {
						if(!TextUtils.isEmpty(line)){
							newList.add(line);
						}
					}
					instream.close();
				}
			} catch (java.io.FileNotFoundException e) {
				Log.d("TestFile", "The File doesn't not exist.");
			} catch (IOException e) {
				Log.d("TestFile", e.getMessage());
			}
		}
		return newList;
	}

	
	public static  String loadFromSDFile(String filePath) {  
        String result="";
        try {
            File f=new File(filePath);
            int length=(int)f.length();  
            byte[] buff=new byte[length];  
            FileInputStream fin=new FileInputStream(f);  
            fin.read(buff);  
            fin.close();  
            result=new String(buff,"UTF-8");  
        }catch (Exception e){  
            e.printStackTrace();  
            //Toast.makeText(MainActivity.this,"没有找到指定文件",Toast.LENGTH_SHORT).show();  
        }  
        return result;  
    }  
	
	
}
