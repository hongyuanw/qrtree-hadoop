/**
 * 递归获取指定目录下的所有文件
 * by Lorand Bendig from stackoverflow & hongyuan wang
 */
package common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class FileRecursiveTool {
	public static Path[] getRecursivePaths(FileSystem fs, String basePath)
			throws IOException, URISyntaxException {
		List<Path> result = new ArrayList<Path>();
		FileStatus[] listStatus = fs.globStatus(new Path(basePath + "/*"));
		for (FileStatus fstat : listStatus) {
			readSubDirectory(fstat, basePath, fs, result);
		}
		return (Path[]) result.toArray(new Path[result.size()]);
	}

	private static void readSubDirectory(FileStatus fileStatus,
			String basePath, FileSystem fs, List<Path> paths)
			throws IOException, URISyntaxException {
		if (!fileStatus.isDir()) {
			//可以在此指定保留的文件
			String fileName = fileStatus.getPath().getName();
			if(fileName.charAt(0) == '(') {
				paths.add(fileStatus.getPath());
			}
		} else {
			String subPath = fileStatus.getPath().toString();
			FileStatus[] listStatus = fs.globStatus(new Path(subPath + "/*"));
			if (listStatus.length == 0) {
				paths.add(fileStatus.getPath());
			}
			for (FileStatus fst : listStatus) {
				readSubDirectory(fst, subPath, fs, paths);
			}
		}
	}
}
