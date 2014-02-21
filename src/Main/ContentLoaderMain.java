package Main;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;

public class ContentLoaderMain {
	private String cacheOutputDir;

	LinkedList<String> urlList = new LinkedList<String>();
	LinkedList<Thread> threadList = new LinkedList<Thread>();

	Loader loader = null;

	ContentLoaderMain(ArrayList<String> urlLis, String outputDir) {
		this.urlList.addAll(urlLis);

		cacheOutputDir = new String(outputDir + "\\Cache");
		File file = new File(cacheOutputDir);
		file.mkdirs();

		loader = new Loader(outputDir, ProgramConstants.loadThreadsCount);
	}

	public void startParsing() {
		for (int i = 0; i < ProgramConstants.parsingThreadsCount; i++) {
			threadList.add(new Thread() {
				public void run() {
					while(true) {

						while (!urlList.isEmpty()) {
							final String url;
							synchronized(urlList) {
								url = urlList.poll();
								File file = new File(cacheOutputDir + "\\" + replaceExtra(url) + ".html");
								if (file.exists()) {
									continue;
								} 
							}

							System.out.println(url);

							try {
								String data = null;

								try {
									data = WebUtils.loadSite(url);
								} catch (Exception e) {
									//System.out.println("Error with load site. " + e.getMessage());
									continue;
								}

								if (data == null || data.length() == 0) {
									//System.out.println("Data is null.");
									continue;
								}

								File file = new File(cacheOutputDir + "\\" + replaceExtra(url) + ".html");
								file.createNewFile();
								FileWriter fw = new FileWriter(file);
								fw.write(data);
								fw.close();


								for (String s : WebUtils.getAllLinks(data, url)) {
									if (s.endsWith(".flv") ||
											s.endsWith(".gif") ||
											s.endsWith(".png") ||
											s.endsWith(".avi") ||
											s.endsWith(".jpg")) {
										synchronized (loader) {
											loader.addImageUrl(s);
											//System.out.println("Add media:" + s);
										}
									} else {
										synchronized (urlList) {
											urlList.add(s);
											//System.out.println("Add not media:" + s);
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println(e.getMessage());
								continue;
							}
						}

						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.out.println(e.getMessage());
						}
					}
				}
			});
		}

		for (Thread t : threadList) {
			t.start();
		}
	}


	public static String replaceExtra(String str) {
		String replased = new String(str);
		return replased.replaceAll("[^a-zA-Z0-9]", "");
	}

}
