package Main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Random;

public class Loader {
	LinkedList<Thread> threadList = new LinkedList<Thread>();
	LinkedList<String> urlList = new LinkedList<String>();

	String imageOutputDir;
	String cacheOutputDir;

	Loader(String outputImageDir, int numberOfThreads) {
		imageOutputDir = outputImageDir;
		cacheOutputDir = new String(outputImageDir + "\\Cache\\ImageCache");

		File outputCacheDir = new File(cacheOutputDir);
		outputCacheDir.mkdirs();

		for (int i = 0; i < numberOfThreads; i++) {
			threadList.add(new Thread() {
				public void run() {
					while(true) {
						while (!urlList.isEmpty()) {
							String imageUrl = null;
							synchronized (urlList) {
								imageUrl = urlList.poll();
							}
							String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
							int imageSize = getFileSize(imageUrl);

							if (imageSize < ProgramConstants.imageBytesMinimalSize) {
								File img = new File(cacheOutputDir + "\\" + imageName);
								if (!img.exists()) {
									download(imageUrl, cacheOutputDir);
								} else {
									if (img.length() != imageSize) {
										String randName = WebUtils.generateString(new Random(), "qwertyuioplkjhgfdsazxcvbnm123456789", 10);
										download(imageUrl, new String(randName + imageName), cacheOutputDir);
									}
								}
							} else {
								File img = new File(imageOutputDir + "\\" + imageName);
								if (!img.exists()) {
									download(imageUrl, imageOutputDir);
								} else {
									if (img.length() != imageSize) {
										String randName = WebUtils.generateString(new Random(), "qwertyuioplkjhgfdsazxcvbnm123456789", 10);
										download(imageUrl, new String(randName + imageName), imageOutputDir);
									}
								}
							}

						}


						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}

		for (Thread t : threadList) {
			t.start();
		}
	}

	void addImageUrl(String s) {
		urlList.add(s);
	}
	
	public void download(String address, String localFileName, String outputDir) {
		OutputStream out = null;
		URLConnection conn = null;
		InputStream in = null;

		try {
			URL url = new URL(address);
			conn = url.openConnection();
			in = conn.getInputStream();

			try {
			out = new BufferedOutputStream(new FileOutputStream(outputDir + "\\" + localFileName));
			} catch (FileNotFoundException e) {
				System.out.println("Invalid file name:" + localFileName);
				return;
			}
			
			byte[] buffer = new byte[1024];

			int numRead;
			long numWritten = 0;

			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}

			System.out.println(localFileName + "\t" + numWritten + " bytes.");
		} 
		catch (Exception exception) { 
			exception.printStackTrace();
			System.out.println(exception.getMessage());
		} 
		finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} 
			catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
		}
	}

	public void download(String address, String outputDir) {			
		int lastSlashIndex = address.lastIndexOf('/');
		if (lastSlashIndex >= 0 &&
				lastSlashIndex < address.length() - 1) {
			download(address, address.substring(lastSlashIndex + 1), outputDir);
		} 
		else {
			System.err.println("Could not figure out local file name for "+address);
		}
	}

	@SuppressWarnings("unused")
	private int getFileSize(URL url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.getInputStream();
			return conn.getContentLength();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return -1;
		} finally {
			conn.disconnect();
		}
	}

	private int getFileSize(String str) {

		URL url = null;
		try {
			url = new URL(str);
		} catch (MalformedURLException e1) {
			System.out.println(e1.getMessage());
			e1.printStackTrace();
		}

		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.getInputStream();
			return conn.getContentLength();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return -1;
		} finally {
			conn.disconnect();
		}
	}

}
