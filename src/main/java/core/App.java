package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;

public class App {

	private static Map<String, FileDescriptor> checksums = new HashMap<String, FileDescriptor>();

	private static Map<String, List<FileDescriptor>> doubles = new HashMap<String, List<FileDescriptor>>();
	
	private static StringBuffer html = new StringBuffer();

	private static void checkAndAddDuplicates(String md5, File file) {
		FileDescriptor existing = checksums.get(md5);
		FileDescriptor incoming = new FileDescriptor(file);
		if (null != existing) {
			System.out.println("Duplicated file found! existing.name:"
					+ existing.getName() + " incoming.name:"
					+ incoming.getName());
			List<FileDescriptor> list = doubles.get(md5);
			if (null != list && list.size() > 0) {
				list.add(incoming);
			} else {
				List<FileDescriptor> newList = new ArrayList<FileDescriptor>();
				newList.add(existing);
				newList.add(incoming);
				doubles.put(md5, newList);
			}
		}
		checksums.put(md5, incoming);
	}

	private static void generateDoublesHtml() {
		for (String md5 : doubles.keySet()) {
			List<FileDescriptor> list = doubles.get(md5);
			if (null != list && list.size() > 0) {
				html.append("<tr colspan='"+list.size()+"'>");
				
				for(FileDescriptor fd:list)
				{
					fd.getPath();
					html.append("<td>");
					html.append("<div>");
					html.append("<span>"+fd.getPath()+"</span>");
					html.append("</ br>");
					html.append("<image src='"+fd.getPath()+"'>");
					html.append("</div>");
					html.append("</td>");
				}
				html.append("</tr>");
			}
		}
		String text = "<html><body><table>" + html.toString() + "</table></body></html>";
		PrintWriter out;
		try {
			out = new PrintWriter("/Users/ramazan_aslan/doubles.html");
			out.println(text);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void walkInto(File directoryOrFile, int directoryDepth)
			throws IOException {
		if (directoryOrFile.isDirectory()) {
			// System.out.println("walking into :" + directoryOrFile.getName());
			File[] files = directoryOrFile.listFiles();
			StopWatch timer = new StopWatch();
			timer.start();

			int size = files.length;
			int cnt = 0;
			for (File file : files) {
				cnt++;
				walkInto(file, directoryDepth + 1);
				double thousandth = Math
						.floor(((new Integer(cnt)).doubleValue() / (new Integer(
								size)).doubleValue()) * 1000);
				if ((thousandth % 300) == 0) {
					timer.split();
					System.out.println("directoryDepth:" + directoryDepth
							+ " cnt:" + cnt + " size:" + size + " processing "
							+ thousandth + " thousandth on dir:"
							+ directoryOrFile.getName() + " execution time:"
							+ timer.getSplitTime());
				}
				timer.split();
				if(timer.getSplitTime() > 60000)
				{
					System.out.println("60 saniyeyi gecti.. bitirelim..");
					generateDoublesHtml();
					return;
				}
			}
		} else {
			// System.out.println("filename:" + directoryOrFile.getName());
			try {
				byte[] fileToByteArray = FileUtils
						.readFileToByteArray(directoryOrFile);
				String md5 = DigestUtils.md5Hex(fileToByteArray);
				// System.out.println("filename:" + directoryOrFile.getName()
				// + " md5:" + md5);
				checkAndAddDuplicates(md5, directoryOrFile);
			} catch (IllegalArgumentException e) {
				System.out.println("filename:" + directoryOrFile.getName()
						+ " Exception:" + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		File tmpdir = new File("/Users/ramazan_aslan/ALBUMLER");
		try {
			App.walkInto(tmpdir, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
