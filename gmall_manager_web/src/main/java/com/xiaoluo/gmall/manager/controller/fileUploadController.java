package com.xiaoluo.gmall.manager.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@RestController
@CrossOrigin
public class fileUploadController {
	@Value("${fileUpload.server}")
	private String filePrefix;

	@PostMapping("fileUpload")
	public String fileUpload(MultipartFile file) throws IOException, MyException {
		String originalFilename = file.getOriginalFilename();
		String resource = this.getClass().getClassLoader().getResource("tracker.conf").getFile();
		ClientGlobal.init(resource);
		TrackerClient trackerClient = new TrackerClient();
		String filePath = filePrefix;
			TrackerServer connection = trackerClient.getConnection();
			StorageClient storageClient = new StorageClient(connection,null);
			String[] strings = storageClient.upload_file(file.getBytes(), StringUtils.substringAfterLast(originalFilename, "."), null);
			for (int i = 0; i < strings.length; i++) {
				filePath += "/" + strings[i];
				System.out.println(strings[i]);

			}

		System.out.println(filePath);

		return filePath;
	}
}
