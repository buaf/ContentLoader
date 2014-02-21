package Main;

import java.util.ArrayList;


public class Main {
	public static void main(String args[]) { 
		ArrayList<String> urlList = new ArrayList<String>();
		urlList.add("https://www.google.com.ua/search?q=%D0%BB%D0%BE%D1%85&safe=off&espv=210&es_sm=122&source=lnms&tbm=isch&sa=X&ei=4ku5Ur28J86MyQP6moGwCg&ved=0CAkQ_AUoAQ&biw=1600&bih=775");

		ContentLoaderMain loader = new ContentLoaderMain(urlList, ProgramConstants.outputDir);
		loader.startParsing();
	} 
}